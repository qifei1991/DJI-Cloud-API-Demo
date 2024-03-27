package com.dji.sample.control.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.dji.sample.cloudapi.client.FlightTaskClient;
import com.dji.sample.common.error.CommonErrorEnum;
import com.dji.sample.component.redis.RedisConst;
import com.dji.sample.component.redis.RedisOpsUtils;
import com.dji.sample.component.websocket.service.IWebSocketMessageService;
import com.dji.sample.control.model.enums.DrcAuthorityErrorEnum;
import com.dji.sample.control.model.enums.DroneAuthorityEnum;
import com.dji.sample.control.model.enums.RemoteDebugMethodEnum;
import com.dji.sample.control.model.param.*;
import com.dji.sample.control.service.IControlService;
import com.dji.sample.manage.model.dto.DeviceDTO;
import com.dji.sample.manage.model.dto.DevicePayloadDTO;
import com.dji.sample.manage.service.IDevicePayloadService;
import com.dji.sample.manage.service.IDeviceRedisService;
import com.dji.sample.manage.service.IDeviceService;
import com.dji.sdk.cloudapi.control.FlyToPointRequest;
import com.dji.sdk.cloudapi.control.PayloadAuthorityGrabRequest;
import com.dji.sdk.cloudapi.control.TakeoffToPointRequest;
import com.dji.sdk.cloudapi.control.api.AbstractControlService;
import com.dji.sdk.cloudapi.debug.DebugMethodEnum;
import com.dji.sdk.cloudapi.debug.api.AbstractDebugService;
import com.dji.sdk.cloudapi.device.*;
import com.dji.sdk.cloudapi.wayline.api.AbstractWaylineService;
import com.dji.sdk.common.HttpResultResponse;
import com.dji.sdk.common.SDKManager;
import com.dji.sdk.exception.CloudSDKErrorEnum;
import com.dji.sdk.mqtt.services.ServicesReplyData;
import com.dji.sdk.mqtt.services.TopicServicesResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

/**
 * @author sean
 * @version 1.2
 * @date 2022/7/29
 */
@Service
@Slf4j
public class ControlServiceImpl implements IControlService {

    @Autowired
    private IWebSocketMessageService webSocketMessageService;

    @Autowired
    private IDeviceService deviceService;

    @Autowired
    private IDeviceRedisService deviceRedisService;

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private IDevicePayloadService devicePayloadService;

    @Autowired
    private AbstractControlService abstractControlService;

    @Autowired
    private AbstractDebugService abstractDebugService;

    @Autowired
    @Qualifier("SDKWaylineService")
    private AbstractWaylineService abstractWaylineService;

    @Autowired
    private FlightTaskClient flightTaskClient;

    private RemoteDebugHandler checkDebugCondition(String sn, RemoteDebugParam param, RemoteDebugMethodEnum controlMethodEnum) {
        RemoteDebugHandler handler = Objects.nonNull(controlMethodEnum.getClazz()) ?
                mapper.convertValue(Objects.nonNull(param) ? param : new Object(), controlMethodEnum.getClazz())
                : new RemoteDebugHandler();
        if (!handler.canPublish(sn)) {
            throw new RuntimeException("The current state of the dock does not support this function.");
        }
        return handler;
    }

    @Override
    public HttpResultResponse controlDockDebug(String sn, RemoteDebugMethodEnum controlMethodEnum, RemoteDebugParam param) {
        DebugMethodEnum methodEnum = controlMethodEnum.getDebugMethodEnum();
        RemoteDebugHandler data = checkDebugCondition(sn, param, controlMethodEnum);

        boolean isExist = deviceRedisService.checkDeviceOnline(sn);
        if (!isExist) {
            return HttpResultResponse.error("机场已下线.");
        }
        TopicServicesResponse response;
        switch (controlMethodEnum) {
            case RETURN_HOME:
                response = abstractWaylineService.returnHome(SDKManager.getDeviceSDK(sn));
                break;
            case RETURN_HOME_CANCEL:
                response = abstractWaylineService.returnHomeCancel(SDKManager.getDeviceSDK(sn));
                break;
            default:
                response = abstractDebugService.remoteDebug(SDKManager.getDeviceSDK(sn), methodEnum,
                        Objects.nonNull(methodEnum.getClazz()) ? mapper.convertValue(data, methodEnum.getClazz()) : null);
        }
        ServicesReplyData serviceReply = (ServicesReplyData) response.getData();
        if (!serviceReply.getResult().isSuccess()) {
            return HttpResultResponse.error(serviceReply.getResult());
        }
        return HttpResultResponse.success();
    }

    private void checkFlyToCondition(String dockSn) {
        // TODO 设备固件版本不兼容情况
        Optional<DeviceDTO> dockOpt = deviceRedisService.getDeviceOnline(dockSn);
        if (dockOpt.isEmpty()) {
            throw new RuntimeException("The dock is offline, please restart the dock.");
        }

        DroneModeCodeEnum deviceMode = deviceService.getDeviceMode(dockOpt.get().getChildDeviceSn());
        if (DroneModeCodeEnum.MANUAL != deviceMode) {
            throw new RuntimeException("The current state of the drone does not support this function, please try again later.");
        }

        HttpResultResponse result = seizeAuthority(dockSn, DroneAuthorityEnum.FLIGHT, null);
        if (HttpResultResponse.CODE_SUCCESS != result.getCode()) {
            throw new IllegalArgumentException(result.getMessage());
        }
    }

    @Override
    public HttpResultResponse flyToPoint(String sn, FlyToPointParam param) {
        checkFlyToCondition(sn);

        // modify by Qfei, 如果ID有值，直接赋值
        if (!StringUtils.hasText(param.getFlyToId())) {
            param.setFlyToId(UUID.randomUUID().toString());
        }
        TopicServicesResponse<ServicesReplyData> response = abstractControlService.flyToPoint(
                SDKManager.getDeviceSDK(sn), mapper.convertValue(param, FlyToPointRequest.class));
        ServicesReplyData reply = response.getData();
        return reply.getResult().isSuccess() ?
                HttpResultResponse.success()
                : HttpResultResponse.error("飞机飞向目标点失败. " + reply.getResult());
    }

    @Override
    public HttpResultResponse flyToPointStop(String sn) {
        TopicServicesResponse<ServicesReplyData> response = abstractControlService.flyToPointStop(SDKManager.getDeviceSDK(sn));
        ServicesReplyData reply = response.getData();

        return reply.getResult().isSuccess() ?
                HttpResultResponse.success()
                : HttpResultResponse.error("飞机停止飞向目标点失败. " + reply.getResult());
    }

    private void checkTakeoffCondition(String dockSn) {
        Optional<DeviceDTO> dockOpt = deviceRedisService.getDeviceOnline(dockSn);
        if (dockOpt.isEmpty() || DockModeCodeEnum.IDLE != deviceService.getDockMode(dockSn)) {
            throw new RuntimeException("当前机场状态不支持起飞.");
        }

        HttpResultResponse result = seizeAuthority(dockSn, DroneAuthorityEnum.FLIGHT, null);
        if (HttpResultResponse.CODE_SUCCESS != result.getCode()) {
            throw new IllegalArgumentException("飞行器起飞失败, " + result.getMessage());
        }

    }

    @Override
    public HttpResultResponse takeoffToPoint(String sn, TakeoffToPointParam param) {
        checkTakeoffCondition(sn);

        // modify by Qfei, 2023-9-10 11:38:49
        if (!StringUtils.hasText(param.getFlightId())) {
            param.setFlightId(UUID.randomUUID().toString());
        }
        TopicServicesResponse<ServicesReplyData> response = abstractControlService.takeoffToPoint(
                SDKManager.getDeviceSDK(sn), mapper.convertValue(param, TakeoffToPointRequest.class));
        ServicesReplyData reply = response.getData();
        if (reply.getResult().isSuccess()) {
            // add by Qfei, 一键起飞的时候，创建飞行记录
            this.flightTaskClient.startTakeoffTo(sn, param);
            return HttpResultResponse.success();
        }
        return HttpResultResponse.error("飞行器一键起飞失败. " + reply.getResult());
    }

    @Override
    public HttpResultResponse checkAuthority(String sn, DroneAuthorityEnum authority, AuthorityBaseParam param) {
        Optional<DeviceDTO> dockOpt = deviceRedisService.getDeviceOnline(sn);
        if (dockOpt.isEmpty()) {
            return HttpResultResponse.error(DrcAuthorityErrorEnum.DOCK_DISCONNECTED);
        }

        String key = RedisConst.DRC_AUTHORITY_PREFIX + sn + RedisConst.DELIMITER + authority.getVal();
        Object id = RedisOpsUtils.hashGet(key, param.getId());
        // 如果当前用户在控制列表中，直接返回true
        if (Objects.nonNull(id)) {
            return HttpResultResponse.success(true);
        }

        switch (authority) {
            case FLIGHT:
                if (DeviceDomainEnum.DOCK != dockOpt.get().getDomain() && DeviceDomainEnum.REMOTER_CONTROL != dockOpt.get().getDomain()) {
                    return HttpResultResponse.error(CommonErrorEnum.ILLEGAL_ARGUMENT);
                }
                if (ControlSourceEnum.B.equals(dockOpt.get().getControlSource())) {
                    return HttpResultResponse.error(DrcAuthorityErrorEnum.DRONE_CONTROL_B);
                }
                Set<Object> controlUsers = RedisOpsUtils.hashKeys(key);
                if (CollUtil.isNotEmpty(controlUsers)) {
                    Optional<OsdDock> osdDataOpt = deviceRedisService.getDeviceOsd(dockOpt.get().getDeviceSn(), OsdDock.class);
                    if (DockModeCodeEnum.WORKING == osdDataOpt.get().getModeCode()
                            || osdDataOpt.get().getDrcState() == DrcStateEnum.CONNECTING) {
                        return HttpResultResponse.error(DrcAuthorityErrorEnum.DRONE_CONTROLLING);
                    }
                    return HttpResultResponse.error(DrcAuthorityErrorEnum.DRONE_CONTROLLED_RECENT);
                }
                return HttpResultResponse.success(true);
            case PAYLOAD:
                Optional<DeviceDTO> deviceOpt = deviceRedisService.getDeviceOnline(dockOpt.get().getChildDeviceSn());
                return deviceOpt.map(device -> {
                    if (CollectionUtils.isEmpty(device.getPayloadsList())) {
                        return HttpResultResponse.error(DrcAuthorityErrorEnum.PAYLOAD_MISSING);
                    }
                    Optional<DevicePayloadDTO> devicePayloadOpt = device.getPayloadsList()
                            .stream()
                            .filter(payload -> ((DronePayloadParam) param).getPayloadIndex().equals(payload.getPayloadIndex().toString()))
                            .findAny();
                    if (devicePayloadOpt.isEmpty()) {
                        return HttpResultResponse.error(DrcAuthorityErrorEnum.PAYLOAD_CANNOT_GET);
                    }
                    if (ControlSourceEnum.B.equals(devicePayloadOpt.get().getControlSource())) {
                        return HttpResultResponse.error(DrcAuthorityErrorEnum.PAYLOAD_CONTROL_B);
                    }
                    Set<Object> controlUser = RedisOpsUtils.hashKeys(key);
                    if (CollUtil.isNotEmpty(controlUser)) {
                        Optional<OsdDockDrone> osdDroneOpt = deviceRedisService.getDeviceOsd(device.getDeviceSn(), OsdDockDrone.class);
                        if (!osdDroneOpt.isEmpty() && (osdDroneOpt.get().getModeCode() != DroneModeCodeEnum.IDLE
                                && osdDroneOpt.get().getModeCode() != DroneModeCodeEnum.DISCONNECTED)) {
                            return HttpResultResponse.error(DrcAuthorityErrorEnum.PAYLOAD_CONTROLLING);
                        }
                        return HttpResultResponse.error(DrcAuthorityErrorEnum.PAYLOAD_CONTROLLED_RECENT);
                    }
                    return HttpResultResponse.success(true);
                }).orElse(HttpResultResponse.success(true));
            default:
                return HttpResultResponse.error(CommonErrorEnum.ILLEGAL_ARGUMENT);
        }
    }

    @Override
    public HttpResultResponse releaseAuthority(String sn, DroneAuthorityEnum authority, AuthorityBaseParam param) {
        String key = RedisConst.DRC_AUTHORITY_PREFIX + sn + RedisConst.DELIMITER + authority.getVal();
        RedisOpsUtils.hashDel(key, new Object[]{ param.getId() });
        return HttpResultResponse.success();
    }

    @Override
    public HttpResultResponse seizeAuthority(String sn, DroneAuthorityEnum authority, AuthorityBaseParam param) {
        TopicServicesResponse<ServicesReplyData> response;
        switch (authority) {
            case FLIGHT:
                if (deviceService.checkAuthorityFlight(sn)) {
                    this.saveDrcAuthority(sn, authority, param);
                    return HttpResultResponse.success();
                }
                response = abstractControlService.flightAuthorityGrab(SDKManager.getDeviceSDK(sn));
                break;
            case PAYLOAD:
                if (checkPayloadAuthority(sn, ((DronePayloadParam) param).getPayloadIndex())) {
                    this.saveDrcAuthority(sn, authority, param);
                    return HttpResultResponse.success();
                }
                response = abstractControlService.payloadAuthorityGrab(SDKManager.getDeviceSDK(sn),
                        new PayloadAuthorityGrabRequest().setPayloadIndex(new PayloadIndex(((DronePayloadParam) param).getPayloadIndex())));
                break;
            default:
                return HttpResultResponse.error(CloudSDKErrorEnum.INVALID_PARAMETER);
        }

        ServicesReplyData serviceReply = response.getData();
        this.saveDrcAuthority(sn, authority, param);
        return serviceReply.getResult().isSuccess() ?
                HttpResultResponse.success()
                : HttpResultResponse.error(serviceReply.getResult());
    }

    private void saveDrcAuthority(String sn, DroneAuthorityEnum authority, AuthorityBaseParam param) {
        if (Objects.nonNull(param)) {
            String key = RedisConst.DRC_AUTHORITY_PREFIX + sn + RedisConst.DELIMITER + authority.getVal();
            log.info("Authority key: " + key);
            RedisOpsUtils.hashSet(key, param.getId(), param.getUsername());
            RedisOpsUtils.expireKey(key, RedisConst.DRC_MODE_ALIVE_SECOND);
        }
    }

    private Boolean checkPayloadAuthority(String sn, String payloadIndex) {
        Optional<DeviceDTO> dockOpt = deviceRedisService.getDeviceOnline(sn);
        if (dockOpt.isEmpty()) {
            throw new RuntimeException("机场离线，请重启机场.");
        }
        return devicePayloadService.checkAuthorityPayload(dockOpt.get().getChildDeviceSn(), payloadIndex);
    }

    @Override
    public HttpResultResponse payloadCommands(PayloadCommandsParam param) throws Exception {
        param.getCmd().getClazz()
                .getDeclaredConstructor(DronePayloadParam.class)
                .newInstance(param.getData())
                .checkCondition(param.getSn());

        TopicServicesResponse<ServicesReplyData> response = abstractControlService.payloadControl(
                SDKManager.getDeviceSDK(param.getSn()), param.getCmd().getCmd(),
                mapper.convertValue(param.getData(), param.getCmd().getCmd().getClazz()));

        ServicesReplyData serviceReply = response.getData();
        return serviceReply.getResult().isSuccess() ?
                HttpResultResponse.success()
                : HttpResultResponse.error(serviceReply.getResult());
    }
}
