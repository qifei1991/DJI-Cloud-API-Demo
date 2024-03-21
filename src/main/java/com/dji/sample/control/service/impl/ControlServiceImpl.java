package com.dji.sample.control.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.dji.sample.cloudapi.client.FlightTaskClient;
import com.dji.sample.common.error.CommonErrorEnum;
import com.dji.sample.common.model.ResponseResult;
import com.dji.sample.component.mqtt.model.*;
import com.dji.sample.component.mqtt.service.IMessageSenderService;
import com.dji.sample.component.redis.RedisConst;
import com.dji.sample.component.redis.RedisOpsUtils;
import com.dji.sample.component.websocket.model.BizCodeEnum;
import com.dji.sample.component.websocket.service.ISendMessageService;
import com.dji.sample.control.model.dto.FlyToProgressReceiver;
import com.dji.sample.control.model.dto.ResultNotifyDTO;
import com.dji.sample.control.model.dto.TakeoffProgressReceiver;
import com.dji.sample.control.model.enums.*;
import com.dji.sample.control.model.param.*;
import com.dji.sample.control.service.IControlService;
import com.dji.sample.manage.model.dto.DeviceDTO;
import com.dji.sample.manage.model.dto.DevicePayloadDTO;
import com.dji.sample.manage.model.enums.*;
import com.dji.sample.manage.model.receiver.OsdDockReceiver;
import com.dji.sample.manage.model.receiver.OsdSubDeviceReceiver;
import com.dji.sample.manage.service.IDevicePayloadService;
import com.dji.sample.manage.service.IDeviceRedisService;
import com.dji.sample.manage.service.IDeviceService;
import com.dji.sample.wayline.model.enums.WaylineErrorCodeEnum;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.messaging.MessageHeaders;
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
    private IMessageSenderService messageSenderService;

    @Autowired
    private ISendMessageService webSocketMessageService;

    @Autowired
    private IDeviceService deviceService;

    @Autowired
    private IDeviceRedisService deviceRedisService;

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private IDevicePayloadService devicePayloadService;

    @Autowired
    private FlightTaskClient flightTaskClient;

    private RemoteDebugHandler checkDebugCondition(String sn, RemoteDebugParam param, RemoteDebugMethodEnum controlMethodEnum) {
        RemoteDebugHandler handler = Objects.nonNull(controlMethodEnum.getClazz()) ?
                mapper.convertValue(Objects.nonNull(param) ? param : new Object(), controlMethodEnum.getClazz())
                : new RemoteDebugHandler();
        if (!handler.canPublish(sn)) {
            throw new RuntimeException("The current state of the dock does not support this function.");
        }
        if (Objects.nonNull(param) && !handler.valid()) {
            throw new RuntimeException(CommonErrorEnum.ILLEGAL_ARGUMENT.getErrorMsg());
        }
        return handler;
    }

    @Override
    public ResponseResult controlDockDebug(String sn, String serviceIdentifier, RemoteDebugParam param) {
        RemoteDebugMethodEnum controlMethodEnum = RemoteDebugMethodEnum.find(serviceIdentifier);
        if (RemoteDebugMethodEnum.UNKNOWN == controlMethodEnum) {
            return ResponseResult.error("调试方法[" + serviceIdentifier + "]不存在.");
        }

        RemoteDebugHandler data = checkDebugCondition(sn, param, controlMethodEnum);

        boolean isExist = deviceRedisService.checkDeviceOnline(sn);
        if (!isExist) {
            return ResponseResult.error("机场离线.");
        }
        String bid = UUID.randomUUID().toString();
        ServiceReply serviceReply = messageSenderService.publishServicesTopic(sn, serviceIdentifier, data, bid);

        if (ResponseResult.CODE_SUCCESS != serviceReply.getResult()) {
            return ResponseResult.error(serviceReply.getResult(),
                    "error: " + serviceIdentifier + serviceReply.getResult());
        }
        if (controlMethodEnum.getProgress()) {
            RedisOpsUtils.setWithExpire(serviceIdentifier + RedisConst.DELIMITER +  bid, sn,
                    RedisConst.DEVICE_ALIVE_SECOND * RedisConst.DEVICE_ALIVE_SECOND);
        }
        return ResponseResult.success();
    }

    /**
     * Handles multi-state command progress information.
     * @param receiver
     * @param headers
     * @return
     */
    @ServiceActivator(inputChannel = ChannelName.INBOUND_EVENTS_CONTROL_PROGRESS, outputChannel = ChannelName.OUTBOUND_EVENTS)
    public CommonTopicReceiver handleControlProgress(CommonTopicReceiver receiver, MessageHeaders headers) {
        String key = receiver.getMethod() + RedisConst.DELIMITER + receiver.getBid();
        if (RedisOpsUtils.getExpire(key) <= 0) {
            return receiver;
        }
        String sn = RedisOpsUtils.get(key).toString();

        EventsReceiver<EventsOutputProgressReceiver> eventsReceiver = mapper.convertValue(receiver.getData(),
                new TypeReference<EventsReceiver<EventsOutputProgressReceiver>>(){});
        eventsReceiver.setBid(receiver.getBid());
        eventsReceiver.setSn(sn);

        log.info("SN: {}, {} ===> Control progress: {}",
                sn, receiver.getMethod(), eventsReceiver.getOutput().getProgress().toString());

        if (eventsReceiver.getResult() != ResponseResult.CODE_SUCCESS) {
            log.error("SN: {}, {} ===> Error code: {}", sn, receiver.getMethod(), eventsReceiver.getResult());
        }

        if (eventsReceiver.getOutput().getProgress().getPercent() == 100 ||
                EventsResultStatusEnum.find(eventsReceiver.getOutput().getStatus()).getEnd()) {
            RedisOpsUtils.del(key);
        }

        Optional<DeviceDTO> deviceOpt = deviceRedisService.getDeviceOnline(sn);

        if (deviceOpt.isEmpty()) {
            throw new RuntimeException("The device is offline.");
        }

        DeviceDTO device = deviceOpt.get();
        webSocketMessageService.sendBatch(device.getWorkspaceId(), UserTypeEnum.WEB.getVal(),
                receiver.getMethod(), eventsReceiver);

        return receiver;
    }

    private void checkFlyToCondition(String dockSn) {
        // TODO 设备固件版本不兼容情况
        Optional<DeviceDTO> dockOpt = deviceRedisService.getDeviceOnline(dockSn);
        if (dockOpt.isEmpty()) {
            throw new RuntimeException("The dock is offline, please restart the dock.");
        }

        DeviceModeCodeEnum deviceMode = deviceService.getDeviceMode(dockOpt.get().getChildDeviceSn());
        if (DeviceModeCodeEnum.MANUAL != deviceMode) {
            throw new RuntimeException("The current state of the drone does not support this function, please try again later.");
        }

        ResponseResult result = seizeAuthority(dockSn, DroneAuthorityEnum.FLIGHT, null);
        if (ResponseResult.CODE_SUCCESS != result.getCode()) {
            throw new IllegalArgumentException(result.getMessage());
        }
    }

    @Override
    public ResponseResult flyToPoint(String sn, FlyToPointParam param) {
        checkFlyToCondition(sn);

        // modify by Qfei, 如果ID有值，直接赋值
        if (!StringUtils.hasText(param.getFlyToId())) {
            param.setFlyToId(UUID.randomUUID().toString());
        }
        ServiceReply reply = messageSenderService.publishServicesTopic(sn, DroneControlMethodEnum.FLY_TO_POINT.getMethod(), param, param.getFlyToId());
        return ResponseResult.CODE_SUCCESS != reply.getResult() ?
                ResponseResult.error("飞机飞向目标点失败." + reply.getResult())
                : ResponseResult.success();
    }

    @Override
    public ResponseResult flyToPointStop(String sn) {
        ServiceReply reply = messageSenderService.publishServicesTopic(sn, DroneControlMethodEnum.FLY_TO_POINT_STOP.getMethod(), null);
        return ResponseResult.CODE_SUCCESS != reply.getResult() ?
                ResponseResult.error("飞机停止飞向目标点失败. " + reply.getResult())
                : ResponseResult.success();
    }

    @Override
    @ServiceActivator(inputChannel = ChannelName.INBOUND_EVENTS_FLY_TO_POINT_PROGRESS, outputChannel = ChannelName.OUTBOUND_EVENTS)
    public CommonTopicReceiver handleFlyToPointProgress(CommonTopicReceiver receiver, MessageHeaders headers) {
        String dockSn  = receiver.getGateway();

        Optional<DeviceDTO> deviceOpt = deviceRedisService.getDeviceOnline(dockSn);
        if (deviceOpt.isEmpty()) {
            log.error("The dock is offline.");
            return null;
        }

        FlyToProgressReceiver eventsReceiver = mapper.convertValue(receiver.getData(), new TypeReference<FlyToProgressReceiver>(){});

        // 判断是否飞行完成
        if (FlyToStatusEnum.endCheck(eventsReceiver.getStatus())) {
            this.flightTaskClient.finishFlyTo(dockSn, eventsReceiver);
        }

        webSocketMessageService.sendBatch(deviceOpt.get().getWorkspaceId(), UserTypeEnum.WEB.getVal(),
                BizCodeEnum.FLY_TO_POINT_PROGRESS.getCode(),
                ResultNotifyDTO.builder().sn(dockSn)
                        .message(WaylineErrorCodeEnum.SUCCESS == eventsReceiver.getResult() ?
                                eventsReceiver.getStatus().getMessage() : eventsReceiver.getResult().getErrorMsg())
                        .result(eventsReceiver.getResult().getErrorCode())
                        .build());
        return receiver;
    }

    private void checkTakeoffCondition(String dockSn) {
        Optional<DeviceDTO> dockOpt = deviceRedisService.getDeviceOnline(dockSn);
        if (dockOpt.isEmpty() || DockModeCodeEnum.IDLE != deviceService.getDockMode(dockSn)) {
            throw new RuntimeException("机场当前状态不支持起飞.");
        }

        ResponseResult result = seizeAuthority(dockSn, DroneAuthorityEnum.FLIGHT, null);
        if (ResponseResult.CODE_SUCCESS != result.getCode()) {
            throw new IllegalArgumentException(result.getMessage());
        }
    }

    @Override
    public ResponseResult takeoffToPoint(String sn, TakeoffToPointParam param) {
        checkTakeoffCondition(sn);

        // modify by Qfei, 2023-9-10 11:38:49
        if (!StringUtils.hasText(param.getFlightId())) {
            param.setFlightId(UUID.randomUUID().toString());
        }
        ServiceReply reply = messageSenderService.publishServicesTopic(sn,
                DroneControlMethodEnum.TAKE_OFF_TO_POINT.getMethod(), param, param.getFlightId());

        if (ResponseResult.CODE_SUCCESS != reply.getResult()) {
            return ResponseResult.error("飞行器起飞失败. " + reply.getResult());
        } else {
            // 一键起飞的时候，创建飞行记录
            this.flightTaskClient.startTakeoffTo(sn, param);

            return ResponseResult.success();
        }
    }

    @Override
    @ServiceActivator(inputChannel = ChannelName.INBOUND_EVENTS_TAKE_OFF_TO_POINT_PROGRESS, outputChannel = ChannelName.OUTBOUND_EVENTS)
    public CommonTopicReceiver handleTakeoffToPointProgress(CommonTopicReceiver receiver, MessageHeaders headers) {
        String dockSn  = receiver.getGateway();

        Optional<DeviceDTO> deviceOpt = deviceRedisService.getDeviceOnline(dockSn);
        if (deviceOpt.isEmpty()) {
            log.error("The dock is offline.");
            return null;
        }
        TakeoffProgressReceiver eventsReceiver = mapper.convertValue(receiver.getData(), new TypeReference<TakeoffProgressReceiver>(){});

        // 判断是否飞行完成
        if (TakeoffStatusEnum.endCheck(eventsReceiver.getStatus())) {
            this.flightTaskClient.finishTakeoffTo(dockSn, eventsReceiver);
        }

        webSocketMessageService.sendBatch(deviceOpt.get().getWorkspaceId(), UserTypeEnum.WEB.getVal(),
                BizCodeEnum.TAKE_OFF_TO_POINT_PROGRESS.getCode(),
                ResultNotifyDTO.builder().sn(dockSn)
                        .message(WaylineErrorCodeEnum.SUCCESS == eventsReceiver.getResult() ?
                                eventsReceiver.getStatus().getMessage() : eventsReceiver.getResult().getErrorMsg())
                        .result(eventsReceiver.getResult().getErrorCode())
                        .build());

        return receiver;
    }

    @Override
    public ResponseResult checkAuthority(String sn, DroneAuthorityEnum authority, AuthorityBaseParam param) {
        Optional<DeviceDTO> dockOpt = deviceRedisService.getDeviceOnline(sn);
        if (dockOpt.isEmpty()) {
            return ResponseResult.error(DrcAuthorityErrorEnum.DOCK_DISCONNECTED);
        }

        String key = RedisConst.DRC_AUTHORITY_PREFIX + sn + RedisConst.DELIMITER + authority.getVal();
        Object id = RedisOpsUtils.hashGet(key, param.getId());
        // 如果当前用户在控制列表中，直接返回true
        if (Objects.nonNull(id)) {
            return ResponseResult.success(true);
        }

        switch (authority) {
            case FLIGHT:
                return dockOpt.map(gateway -> {
                    if (DeviceDomainEnum.DOCK.getVal() != gateway.getDomain() && DeviceDomainEnum.GATEWAY.getVal() != gateway.getDomain()) {
                        return ResponseResult.error(CommonErrorEnum.ILLEGAL_ARGUMENT);
                    }
                    if (ControlSourceEnum.B.getControlSource().equals(gateway.getControlSource())) {
                        return ResponseResult.error(DrcAuthorityErrorEnum.DRONE_CONTROL_B);
                    }
                    Set<Object> controlUsers = RedisOpsUtils.hashKeys(key);
                    if (CollUtil.isNotEmpty(controlUsers)) {
                        String osdKey = RedisConst.OSD_PREFIX + gateway.getDeviceSn();
                        OsdDockReceiver osdData = (OsdDockReceiver) RedisOpsUtils.get(osdKey);
                        if (DockModeCodeEnum.WORKING == osdData.getModeCode() || osdData.getDrcState() == DockDrcStateEnum.CONNECTING) {
                            return ResponseResult.error(DrcAuthorityErrorEnum.DRONE_CONTROLLING);
                        }
                        return ResponseResult.error(DrcAuthorityErrorEnum.DRONE_CONTROLLED_RECENT);
                    }
                    return ResponseResult.success(true);
                }).orElse(ResponseResult.success(true));
            case PAYLOAD:
                Optional<DeviceDTO> deviceOpt = deviceRedisService.getDeviceOnline(dockOpt.get().getChildDeviceSn());
                return deviceOpt.map(device -> {
                    if (CollectionUtils.isEmpty(device.getPayloadsList())) {
                        return ResponseResult.error(DrcAuthorityErrorEnum.PAYLOAD_MISSING);
                    }
                    Optional<DevicePayloadDTO> devicePayloadOpt = device.getPayloadsList()
                            .stream()
                            .filter(payload -> ((DronePayloadParam) param).getPayloadIndex().equals(payload.getPayloadIndex()))
                            .findAny();
                    if (devicePayloadOpt.isEmpty()) {
                        return ResponseResult.error(DrcAuthorityErrorEnum.PAYLOAD_CANNOT_GET);
                    }
                    if (ControlSourceEnum.B.getControlSource().equals(devicePayloadOpt.get().getControlSource())) {
                        return ResponseResult.error(DrcAuthorityErrorEnum.PAYLOAD_CONTROL_B);
                    }
                    Set<Object> controlUsers = RedisOpsUtils.hashKeys(key);
                    if (CollUtil.isNotEmpty(controlUsers)) {
                        String osdKey = RedisConst.OSD_PREFIX + device.getDeviceSn();
                        OsdSubDeviceReceiver subDeviceOsdData = (OsdSubDeviceReceiver) RedisOpsUtils.get(osdKey);
                        if (subDeviceOsdData.getModeCode().getVal() > DeviceModeCodeEnum.IDLE.getVal()
                                && subDeviceOsdData.getModeCode().getVal() < DeviceModeCodeEnum.RETURN_AUTO.getVal()) {
                            return ResponseResult.error(DrcAuthorityErrorEnum.PAYLOAD_CONTROLLING);
                        }
                        return ResponseResult.error(DrcAuthorityErrorEnum.PAYLOAD_CONTROLLED_RECENT);
                    }
                    return ResponseResult.success(true);
                }).orElse(ResponseResult.success(true));
            default:
                return ResponseResult.error(CommonErrorEnum.ILLEGAL_ARGUMENT);
        }
    }

    @Override
    public ResponseResult releaseAuthority(String sn, DroneAuthorityEnum authority, AuthorityBaseParam param) {
        String key = RedisConst.DRC_AUTHORITY_PREFIX + sn + RedisConst.DELIMITER + authority.getVal();
        RedisOpsUtils.hashDel(key, new Object[]{ param.getId() });
        return ResponseResult.success();
    }

    @Override
    public ResponseResult seizeAuthority(String sn, DroneAuthorityEnum authority, AuthorityBaseParam param) {

        Optional<DeviceDTO> dockOpt = deviceRedisService.getDeviceOnline(sn);
        if (dockOpt.isEmpty()) {
            return ResponseResult.error(DrcAuthorityErrorEnum.DOCK_DISCONNECTED);
        }
        String method;
        switch (authority) {
            case FLIGHT:
                if (deviceService.checkAuthorityFlight(sn)) {
                    this.saveDrcAuthority(sn, authority, param);
                    return ResponseResult.success();
                }
                method = DroneControlMethodEnum.FLIGHT_AUTHORITY_GRAB.getMethod();
                break;
            case PAYLOAD:
                if (devicePayloadService.checkAuthorityPayload(dockOpt.get().getChildDeviceSn(), ((DronePayloadParam) param).getPayloadIndex())) {
                    this.saveDrcAuthority(sn, authority, param);
                    return ResponseResult.success();
                }
                method = DroneControlMethodEnum.PAYLOAD_AUTHORITY_GRAB.getMethod();
                break;
            default:
                return ResponseResult.error(CommonErrorEnum.ILLEGAL_ARGUMENT);
        }
        ServiceReply serviceReply = messageSenderService.publishServicesTopic(sn, method, param);

        log.info("Authority_reply: " + serviceReply);
        if (ResponseResult.CODE_SUCCESS == serviceReply.getResult()) {
            this.saveDrcAuthority(sn, authority, param);
            return ResponseResult.success();
        }
        return ResponseResult.error(serviceReply.getResult(), "获取控制权失败, Method: " + method + " Error Code:" + serviceReply.getResult());
    }

    private void saveDrcAuthority(String sn, DroneAuthorityEnum authority, AuthorityBaseParam param) {
        if (Objects.nonNull(param)) {
            String key = RedisConst.DRC_AUTHORITY_PREFIX + sn + RedisConst.DELIMITER + authority.getVal();
            log.info("Authority key: " + key);
            RedisOpsUtils.hashSet(key, param.getId(), param.getUsername());
            RedisOpsUtils.expireKey(key, RedisConst.DRC_MODE_ALIVE_SECOND);
        }
    }

    @Override
    public ResponseResult payloadCommands(PayloadCommandsParam param) throws Exception {
        param.getCmd().getClazz()
                .getDeclaredConstructor(DronePayloadParam.class)
                .newInstance(param.getData())
                .checkCondition(param.getSn());

        ServiceReply serviceReply = messageSenderService.publishServicesTopic(param.getSn(), param.getCmd().getCmd(), param.getData());
        return ResponseResult.CODE_SUCCESS != serviceReply.getResult() ?
                ResponseResult.error(serviceReply.getResult(), " Error Code:" + serviceReply.getResult())
                : ResponseResult.success();
    }

}
