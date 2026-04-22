package com.dji.sample.control.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.cron.CronUtil;
import com.dji.sample.component.mqtt.config.MqttPropertyConfiguration;
import com.dji.sample.component.mqtt.model.EventsReceiver;
import com.dji.sample.component.mqtt.model.MapKeyConst;
import com.dji.sample.component.redis.RedisConst;
import com.dji.sample.component.redis.RedisOpsUtils;
import com.dji.sample.control.model.dto.JwtAclDTO;
import com.dji.sample.control.model.dto.MqttAclAccessRule;
import com.dji.sample.control.model.enums.DroneAuthorityEnum;
import com.dji.sample.control.model.enums.MqttAclAccessActionEnum;
import com.dji.sample.control.model.param.DrcConnectParam;
import com.dji.sample.control.model.param.DrcModeParam;
import com.dji.sample.control.service.IControlService;
import com.dji.sample.control.service.IDrcService;
import com.dji.sample.manage.model.dto.DeviceDTO;
import com.dji.sample.manage.service.IDeviceRedisService;
import com.dji.sample.manage.service.IDeviceService;
import com.dji.sample.wayline.model.enums.WaylineJobStatusEnum;
import com.dji.sample.wayline.model.enums.WaylineTaskStatusEnum;
import com.dji.sample.wayline.model.param.UpdateJobParam;
import com.dji.sample.wayline.service.IFlightTaskService;
import com.dji.sample.wayline.service.IWaylineJobService;
import com.dji.sample.wayline.service.IWaylineRedisService;
import com.dji.sdk.cloudapi.control.DrcModeEnterRequest;
import com.dji.sdk.cloudapi.control.DrcModeMqttBroker;
import com.dji.sdk.cloudapi.control.HeartBeatRequest;
import com.dji.sdk.cloudapi.control.api.AbstractControlService;
import com.dji.sdk.cloudapi.device.DockModeCodeEnum;
import com.dji.sdk.cloudapi.device.OsdDockDrone;
import com.dji.sdk.cloudapi.wayline.FlighttaskProgress;
import com.dji.sdk.common.HttpResultResponse;
import com.dji.sdk.common.SDKManager;
import com.dji.sdk.config.version.CloudSDKVersionEnum;
import com.dji.sdk.config.version.Dock2ThingVersionEnum;
import com.dji.sdk.config.version.GatewayManager;
import com.dji.sdk.mqtt.TopicConst;
import com.dji.sdk.mqtt.services.ServicesReplyData;
import com.dji.sdk.mqtt.services.TopicServicesResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @author sean
 * @version 1.3
 * @date 2023/1/11
 */
@Service
@Slf4j
public class DrcServiceImpl implements IDrcService {

    // DRC heartbeat cron 没10秒一次
    private static String DRC_HEART_CRON = "0/10 * * * * ?";

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private IWaylineJobService waylineJobService;

    @Autowired
    private IFlightTaskService flighttaskService;

    @Autowired
    private IDeviceService deviceService;

    @Autowired
    private IControlService controlService;

    @Autowired
    private IDeviceRedisService deviceRedisService;

    @Autowired
    private IWaylineRedisService waylineRedisService;

    @Autowired
    private AbstractControlService abstractControlService;

    @Autowired
    private DrcRedisService drcRedisService;

    @Override
    public void setDrcModeInRedis(String dockSn, String clientId) {
        RedisOpsUtils.setWithExpire(RedisConst.DRC_PREFIX + dockSn, clientId, RedisConst.DRC_MODE_ALIVE_SECOND);
    }

    @Override
    public String getDrcModeInRedis(String dockSn) {
        return (String) RedisOpsUtils.get(RedisConst.DRC_PREFIX + dockSn);
    }

    @Override
    public Boolean delDrcModeInRedis(String dockSn) {
        return RedisOpsUtils.del(RedisConst.DRC_PREFIX + dockSn);
    }

    @Override
    public DrcModeMqttBroker userDrcAuth(String workspaceId, String userId, String username, DrcConnectParam param) {

        // refresh token
        String clientId = param.getClientId();
        // first time
        if (!StringUtils.hasText(clientId) || !RedisOpsUtils.checkExist(RedisConst.MQTT_ACL_PREFIX + clientId)) {
            clientId = userId + "-" + System.currentTimeMillis();
            // RedisOpsUtils.hashSet(RedisConst.MQTT_ACL_PREFIX + clientId, "", MqttAclAccessEnum.ALL.getValue());
            DrcAclRedisUtil.hashSet(RedisConst.MQTT_ACL_PREFIX + clientId,
                    "", new MqttAclAccessRule().setAction(MqttAclAccessActionEnum.ALL.getAction()));
        }

        String key = RedisConst.MQTT_ACL_PREFIX + clientId;

        try {
            RedisOpsUtils.expireKey(key, RedisConst.DRC_MODE_ALIVE_SECOND);

            return MqttPropertyConfiguration.getWebMqttBrokerWithDrc(
                    clientId, username, param.getExpireSec(), Collections.emptyMap());
        } catch (RuntimeException e) {
            RedisOpsUtils.del(key);
            throw e;
        }
    }

    private void checkDrcModeCondition(String workspaceId, String dockSn) {
        Optional<EventsReceiver<FlighttaskProgress>> runningOpt = waylineRedisService.getRunningWaylineJob(dockSn);
        if (runningOpt.isPresent() && WaylineJobStatusEnum.IN_PROGRESS == waylineJobService.getWaylineState(dockSn)) {
            flighttaskService.updateJobStatus(workspaceId, runningOpt.get().getBid(),
                    UpdateJobParam.builder().status(WaylineTaskStatusEnum.PAUSE).build());
        }

        DockModeCodeEnum dockMode = deviceService.getDockMode(dockSn);
        Optional<DeviceDTO> dockOpt = deviceRedisService.getDeviceOnline(dockSn);
        if (dockOpt.isPresent() && (DockModeCodeEnum.IDLE == dockMode || DockModeCodeEnum.WORKING == dockMode)) {
            Optional<OsdDockDrone> deviceOsd = deviceRedisService.getDeviceOsd(dockOpt.get().getChildDeviceSn(), OsdDockDrone.class);
            if (deviceOsd.isEmpty() || deviceOsd.get().getElevation() <= 0) {
                throw new RuntimeException("飞机不在空中，不能进入手动控制飞行模式.");
            }
        } else {
            log.info("- [Drc Condition] Dock Mode: {}", dockMode);
            throw new RuntimeException("机场当前状态不支持进入手动控制飞行模式.");
        }

        HttpResultResponse result = controlService.seizeAuthority(dockSn, DroneAuthorityEnum.FLIGHT, null);
        log.info("- [Drc Condition] SeizeAuthority Response: {}", result);

        if (HttpResultResponse.CODE_SUCCESS != result.getCode()) {
            throw new IllegalArgumentException(result.getMessage());
        }
    }

    @Override
    public JwtAclDTO deviceDrcEnter(String workspaceId, DrcModeParam param) {
        String topic = TopicConst.THING_MODEL_PRE + TopicConst.PRODUCT + param.getDockSn() + TopicConst.DRC;
        String pubTopic = topic + TopicConst.DOWN;
        String subTopic = topic + TopicConst.UP;

        GatewayManager gatewayMgr = SDKManager.getDeviceSDK(param.getDockSn());

        // If the dock is in drc mode, refresh the permissions directly.
        if (deviceService.checkDockDrcMode(param.getDockSn()) && param.getClientId().equals(this.getDrcModeInRedis(param.getDockSn()))) {
            refreshAcl(param.getDockSn(), param.getClientId(), pubTopic, subTopic);
            // 添加定时心跳任务，10秒一次
            if (checkHeartBeatSinceVersion(gatewayMgr)) {
                addDrcHeartBeat(param, gatewayMgr);
            }
            return JwtAclDTO.builder().sub(List.of(subTopic)).pub(List.of(pubTopic)).build();
        }

        checkDrcModeCondition(workspaceId, param.getDockSn());

        String droneClientId = param.getDockSn() + "-" + System.currentTimeMillis();
        setDroneClientDrcAcl(droneClientId, subTopic, pubTopic);    // 这里是飞机的授权，和web端的相反
        TopicServicesResponse<ServicesReplyData> reply = abstractControlService.drcModeEnter(
                gatewayMgr,
                new DrcModeEnterRequest()
                        .setMqttBroker(MqttPropertyConfiguration.getMqttBrokerWithDrc(droneClientId, param.getDockSn(),
                                RedisConst.DRC_MODE_ALIVE_SECOND.longValue(),
                                Map.of(MapKeyConst.ACL, objectMapper.convertValue(JwtAclDTO.builder()
                                        .pub(List.of(subTopic))
                                        .sub(List.of(pubTopic))
                                        .build(), new TypeReference<Map<String, ?>>() {}))))
                        .setHsiFrequency(1)
                        .setOsdFrequency(10));

        if (!reply.getData().getResult().isSuccess()) {
            log.error("[Drc Enter] DrcModeEnter: DockSn: {}, Reply: {}", param.getDockSn(), reply);
            throw new RuntimeException("进入DRC飞行控制失败, 请稍候重试! SN: " + param.getDockSn() + "; Error:" + reply.getData().getResult());
        }

        refreshAcl(param.getDockSn(), param.getClientId(), pubTopic, subTopic);
        // 添加定时心跳任务，10秒一次
        if (checkHeartBeatSinceVersion(gatewayMgr)) {
            addDrcHeartBeat(param, gatewayMgr);
        }
        return JwtAclDTO.builder().sub(List.of(subTopic)).pub(List.of(pubTopic)).build();
    }

    private void addDrcHeartBeat(DrcModeParam param, GatewayManager gatewayMgr) {
        log.info("- [Drc HeartBeat] 先删除再添加, SN: {}", gatewayMgr.getGatewaySn());
        drcRedisService.getDrcHeartBeat(param.getDockSn()).ifPresent(CronUtil::remove);
        String taskId = UUID.randomUUID().toString();
        CronUtil.schedule(taskId, DRC_HEART_CRON, () -> {
            log.info("[Drc HeartBeat] 发送心跳, SN: {}", param.getDockSn());
            abstractControlService.heartBeatDown(gatewayMgr,
                    new HeartBeatRequest()
                            .setSeq(0L)
                            .setTimestamp(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()));
        });
        drcRedisService.setDrcHeartBeat(param.getDockSn(), taskId);
    }

    /**
     * 检查机场版本是否需要 定时心跳
     * @param gatewayMgr 网关设备
     */
    private boolean checkHeartBeatSinceVersion(GatewayManager gatewayMgr) {
        return null != gatewayMgr.getSdkVersion() && gatewayMgr.getSdkVersion().isSupported(CloudSDKVersionEnum.V1_0_3);
    }

    private void refreshAcl(String dockSn, String clientId, String pubTopic, String subTopic) {
        this.setDrcModeInRedis(dockSn, clientId);

        // assign acl，Match by clientId. https://docs.emqx.com/zh/emqx/v5.2/access-control/authz/redis.html
        // scheme: HSET mqtt_acl:[clientid] [topic] [access]
        String key = RedisConst.MQTT_ACL_PREFIX + clientId;
        // RedisOpsUtils.hashSet(key, pubTopic, MqttAclAccessEnum.PUB.getValue());
        // RedisOpsUtils.hashSet(key, subTopic, MqttAclAccessEnum.SUB.getValue());
        DrcAclRedisUtil.hashSet(key, pubTopic, new MqttAclAccessRule().setAction(MqttAclAccessActionEnum.PUBLISH.getAction()));
        DrcAclRedisUtil.hashSet(key, subTopic, new MqttAclAccessRule().setAction(MqttAclAccessActionEnum.SUBSCRIBE.getAction()));
        RedisOpsUtils.expireKey(key, RedisConst.DRC_MODE_ALIVE_SECOND);
    }

    private void setDroneClientDrcAcl(String clientId, String pubTopic, String subTopic) {
        // assign acl，Match by clientId. https://docs.emqx.com/zh/emqx/v5.2/access-control/authz/redis.html
        // scheme: HSET mqtt_acl:[clientid] [topic] [access]
        String key = RedisConst.MQTT_ACL_PREFIX + clientId;
        DrcAclRedisUtil.hashSet(RedisConst.MQTT_ACL_PREFIX + clientId, "",
                new MqttAclAccessRule().setAction(MqttAclAccessActionEnum.ALL.getAction()));
        DrcAclRedisUtil.hashSet(key, pubTopic, new MqttAclAccessRule().setAction(MqttAclAccessActionEnum.PUBLISH.getAction()));
        DrcAclRedisUtil.hashSet(key, subTopic, new MqttAclAccessRule().setAction(MqttAclAccessActionEnum.SUBSCRIBE.getAction()));
        RedisOpsUtils.expireKey(key, RedisConst.DRC_MODE_ALIVE_SECOND);
    }

    @Override
    public void deviceDrcExit(String workspaceId, DrcModeParam param) {
        if (!deviceService.checkDockDrcMode(param.getDockSn())) {
            throw new RuntimeException("机场不处于DRC飞行控制模式.");
        }
        GatewayManager gatewayMgr = SDKManager.getDeviceSDK(param.getDockSn());
        TopicServicesResponse<ServicesReplyData> reply = abstractControlService.drcModeExit(gatewayMgr);
        if (!reply.getData().getResult().isSuccess()) {
            log.error("[Drc exit] Error, sn: {}, Reply: {}", param.getDockSn(), reply);
            throw new RuntimeException("退出DRC飞行控制模式失败, 请稍候重试! SN: " + param.getDockSn() + "; Error:" + reply.getData().getResult());
        }

        String jobId = waylineRedisService.getPausedWaylineJobId(param.getDockSn());
        if (StringUtils.hasText(jobId)) {
            log.info("[Drc exit] Resume the running job, JobId: {}", jobId);
            flighttaskService.updateJobStatus(workspaceId, jobId, UpdateJobParam.builder().status(WaylineTaskStatusEnum.RESUME).build());
        }

        // 清除DRC心跳任务
        if (checkHeartBeatSinceVersion(gatewayMgr)) {
            log.info("- [Drc HeartBeat] 删除, SN: {}", gatewayMgr.getGatewaySn());
            drcRedisService.getDrcHeartBeat(param.getDockSn()).ifPresent(CronUtil::remove);
            drcRedisService.deleteDrcHeartBeat(param.getDockSn());
        }

        this.delDrcModeInRedis(param.getDockSn());
        RedisOpsUtils.del(RedisConst.MQTT_ACL_PREFIX + param.getClientId());
    }

    /**
     * 定时查看是否有无用的定时任务，如果设备下线，说明不需要执行DRC心跳检测则清除定时任务
     */
    @Scheduled(initialDelay = 10, fixedRate = 60, timeUnit = TimeUnit.SECONDS)
    public void cleanDrcHeartBeatTask() {
        Set<String> allKeys = RedisOpsUtils.getAllKeys(RedisConst.DRC_HEART_BEAT_PREFIX + "*");
        if (CollUtil.isEmpty(allKeys)) {
            return;
        }
        log.info("- [Drc HeartBeat] 定时任务数量: {}", allKeys.size());
        int start = RedisConst.DRC_HEART_BEAT_PREFIX.length();
        allKeys.forEach(key -> {
            String dockSn = key.substring(start);
            Optional<DeviceDTO> deviceOnlineOpt = deviceRedisService.getDeviceOnline(dockSn);
            if (deviceOnlineOpt.isEmpty() || !deviceService.checkDockDrcMode(dockSn)
                    || Objects.isNull(deviceOnlineOpt.get().getChildren())
                    || !deviceOnlineOpt.get().getChildren().getStatus()) {
                log.info("- [Drc HeartBeat] 删除设备心跳，ID: {}", dockSn);
                drcRedisService.getDrcHeartBeat(dockSn).ifPresent(CronUtil::remove);
                drcRedisService.deleteDrcHeartBeat(dockSn);
            }
        });
    }

    public static void main(String[] args) throws JsonProcessingException {
        log.info("version: {}", Dock2ThingVersionEnum.V1_3_0.compareTo(Dock2ThingVersionEnum.V1_3_1) >= 0);
    }
}
