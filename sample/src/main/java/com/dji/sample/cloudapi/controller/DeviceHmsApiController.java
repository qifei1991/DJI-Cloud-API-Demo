package com.dji.sample.cloudapi.controller;

import com.dji.sdk.common.HttpResultResponse;
import com.dji.sample.manage.model.dto.DeviceHmsDTO;
import com.dji.sample.manage.model.param.DeviceHmsQueryParam;
import com.dji.sample.manage.service.IDeviceHmsService;
import com.dji.sdk.common.PaginationData;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

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
     * Page to query the hms information of the device.
     * @param param
     * @param workspaceId
     * @return
     */
    @GetMapping("/{workspace_id}/devices/hms")
    public HttpResultResponse<PaginationData<DeviceHmsDTO>> getHmsInformation(DeviceHmsQueryParam param,
            @PathVariable("workspace_id") String workspaceId) {
        PaginationData<DeviceHmsDTO> devices = deviceHmsService.getDeviceHmsByParam(param);
        return HttpResultResponse.success(devices);
    }

    /**
     * Get hms messages for a single device.
     * @param deviceSn
     * @return
     */
    @GetMapping("/{workspace_id}/devices/hms/{device_sn}")
    public HttpResultResponse<List<DeviceHmsDTO>> getUnreadHmsByDeviceSn(@PathVariable("device_sn") String deviceSn) {
        PaginationData<DeviceHmsDTO> paginationData = deviceHmsService.getDeviceHmsByParam(
                DeviceHmsQueryParam.builder()
                        .deviceSn(new HashSet<>(Set.of(deviceSn)))
                        .updateTime(0L)
                        .page(1L)
                        .pageSize(9999L)
                        .build());
        return HttpResultResponse.success(paginationData.getList());
    }

    /**
     * Update unread hms messages to read status.
     * @param deviceSn
     * @return
     */
    @PutMapping("/{workspace_id}/devices/hms/{device_sn}")
    public HttpResultResponse updateReadHmsByDeviceSn(@PathVariable("device_sn") String deviceSn,
            @RequestParam(name = "hms_id", required = false) Set<String> hmsIds, @PathVariable String workspace_id) {
        deviceHmsService.updateUnreadHmsByHmsKey(deviceSn, hmsIds);
        return HttpResultResponse.success();
    }

    /**
     * Get hms messages for batch device.
     * @param param
     * @return
     */
    @GetMapping("/{workspace_id}/devices/hms/group-by-sn")
    public HttpResultResponse<Map<String, List<DeviceHmsDTO>>> getUnreadHmsByDeviceSn(DeviceHmsQueryParam param,
            @PathVariable String workspace_id) {
        param.setUpdateTime(0L);
        PaginationData<DeviceHmsDTO> paginationData = deviceHmsService.getDeviceHmsByParam(param);
        return HttpResultResponse.success(paginationData.getList().stream().collect(Collectors.groupingBy(DeviceHmsDTO::getSn)));
    }
}
