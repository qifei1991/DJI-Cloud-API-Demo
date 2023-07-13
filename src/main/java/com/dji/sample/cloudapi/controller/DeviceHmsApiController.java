package com.dji.sample.cloudapi.controller;

import com.dji.sample.common.model.PaginationData;
import com.dji.sample.common.model.ResponseResult;
import com.dji.sample.manage.model.dto.DeviceHmsDTO;
import com.dji.sample.manage.model.param.DeviceHmsQueryParam;
import com.dji.sample.manage.service.IDeviceHmsService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 设备Hms API接口
 *
 * @author Qfei
 * @date 2023/7/11 11:58
 */
@RestController
@RequestMapping("${url.cloud-api.prefix}${url.cloud-api.version}/devices")
@RequiredArgsConstructor
public class DeviceHmsApiController {

    private final IDeviceHmsService deviceHmsService;

    /**
     * Get hms messages for a single device.
     * @param deviceSn
     * @return
     */
    @GetMapping("/{workspace_id}/devices/hms/{device_sn}")
    public ResponseResult<List<DeviceHmsDTO>> getUnreadHmsByDeviceSn(@PathVariable("device_sn") String deviceSn) {
        PaginationData<DeviceHmsDTO> paginationData = deviceHmsService.getDeviceHmsByParam(
                DeviceHmsQueryParam.builder()
                        .deviceSn(new HashSet<>(Set.of(deviceSn)))
                        .updateTime(0L)
                        .build());
        return ResponseResult.success(paginationData.getList());
    }
}
