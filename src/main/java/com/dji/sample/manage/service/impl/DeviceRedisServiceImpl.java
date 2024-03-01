package com.dji.sample.manage.service.impl;

import com.dji.sample.component.mqtt.model.EventsOutputProgressReceiver;
import com.dji.sample.component.mqtt.model.EventsReceiver;
import com.dji.sample.component.redis.RedisConst;
import com.dji.sample.component.redis.RedisOpsUtils;
import com.dji.sample.manage.model.dto.DeviceDTO;
import com.dji.sample.manage.model.receiver.FirmwareProgressExtReceiver;
import com.dji.sample.manage.service.IDeviceRedisService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author sean
 * @version 1.4
 * @date 2023/3/21
 */
@Slf4j
@Service
public class DeviceRedisServiceImpl implements IDeviceRedisService {

    @Override
    public Boolean checkDeviceOnline(String sn) {
        String key = RedisConst.DEVICE_ONLINE_PREFIX + sn;
        return RedisOpsUtils.checkExist(key) && RedisOpsUtils.getExpire(key) > 0;
    }

    @Override
    public Optional<DeviceDTO> getDeviceOnline(String sn) {
        return Optional.ofNullable((DeviceDTO) RedisOpsUtils.get(RedisConst.DEVICE_ONLINE_PREFIX + sn));
    }

    @Override
    public void setDeviceOnline(DeviceDTO device) {
        RedisOpsUtils.setWithExpire(RedisConst.DEVICE_ONLINE_PREFIX + device.getDeviceSn(), device, RedisConst.DEVICE_ALIVE_SECOND);
    }

    @Override
    public Boolean delDeviceOnline(String sn) {
        return RedisOpsUtils.del(RedisConst.DEVICE_ONLINE_PREFIX + sn);
    }

    @Override
    public <T> Optional<T> getDeviceOsd(String sn, Class<T> clazz) {
        return Optional.ofNullable(clazz.cast(RedisOpsUtils.get(RedisConst.OSD_PREFIX + sn)));
    }

    @Override
    public void setFirmwareUpgrading(String sn, EventsReceiver<EventsOutputProgressReceiver<FirmwareProgressExtReceiver>> events) {
        RedisOpsUtils.setWithExpire(RedisConst.FIRMWARE_UPGRADING_PREFIX + sn, events, RedisConst.DEVICE_ALIVE_SECOND * 20);
    }

    @Override
    public Optional<EventsReceiver<EventsOutputProgressReceiver<FirmwareProgressExtReceiver>>> getFirmwareUpgradingProgress(String sn) {
        return Optional.ofNullable((EventsReceiver<EventsOutputProgressReceiver<FirmwareProgressExtReceiver>>) RedisOpsUtils.get(RedisConst.FIRMWARE_UPGRADING_PREFIX + sn));
    }

    @Override
    public Boolean delFirmwareUpgrading(String sn) {
        return RedisOpsUtils.del(RedisConst.FIRMWARE_UPGRADING_PREFIX + sn);
    }

    @Override
    public void addEndHmsKeys(String sn, String... keys) {
        RedisOpsUtils.listRPush(RedisConst.HMS_PREFIX + sn, keys);
    }

    @Override
    public Set<String> getAllHmsKeys(String sn) {
        return RedisOpsUtils.listGetAll(RedisConst.HMS_PREFIX + sn).stream()
                .map(String::valueOf).collect(Collectors.toSet());
    }

    @Override
    public Boolean delHmsKeysBySn(String sn) {
        return RedisOpsUtils.del(RedisConst.HMS_PREFIX + sn);
    }

    @Override
    public Boolean delHmsKeysBySnAndHmsKey(String sn, Set<String> hmsKeys) {
        try {
            for (String hmsKey : hmsKeys) {
                RedisOpsUtils.listRemove(RedisConst.HMS_PREFIX + sn, 0, hmsKey);
            }
            return true;
        } catch (Exception e) {
            log.error("Failed to delete Redis Hms Key.", e);
            return false;
        }
    }
}
