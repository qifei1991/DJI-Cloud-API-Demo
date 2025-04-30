package com.dji.sample.cloudapi.controller;

import com.dji.sdk.common.HttpResultResponse;
import com.dji.sample.manage.model.dto.DeviceDTO;
import com.dji.sample.manage.service.IDeviceService;
import com.dji.sdk.exception.CloudSDKErrorEnum;
import com.dji.sdk.mqtt.property.PropertySetReplyResultEnum;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

/**
 * The Controller of Devices.
 *
 * @author Qfei
 * @date 2022/12/22 11:41
 */
@RestController
@RequestMapping("${url.cloud-api.prefix}${url.cloud-api.version}/devices")
public class DeviceApiController {

    @Autowired
    private IDeviceService deviceService;

    /**
     * Get the topology list of all online devices in one workspace.
     * @param workspaceId workspace ID
     * @return Dock devices
     */
    @GetMapping("/{workspace_id}/devices")
    public HttpResultResponse<List<DeviceDTO>> getDockDevices(@PathVariable("workspace_id") String workspaceId) {
        List<DeviceDTO> devicesList = deviceService.getDevicesTopoForWeb(workspaceId);
        return HttpResultResponse.success(devicesList);
    }

    @GetMapping("/{device_sn}")
    public HttpResultResponse getDevice(@PathVariable("device_sn") String deviceSn) {
        Optional<DeviceDTO> deviceOpt = deviceService.getDeviceBySnWithHms(deviceSn);
        return deviceOpt.isEmpty() ? HttpResultResponse.error("device not found.") : HttpResultResponse.success(deviceOpt.get());
    }

    /**
     * Set the property parameters of the drone.
     * @param workspaceId
     * @param dockSn
     * @param param
     * @return
     */
    @PutMapping("/{workspace_id}/devices/{device_sn}/property")
    public HttpResultResponse devicePropertySet(@PathVariable("workspace_id") String workspaceId,
            @PathVariable("device_sn") String dockSn, @RequestBody JsonNode param) {
        if (param.size() != 1) {
            return HttpResultResponse.error(CloudSDKErrorEnum.INVALID_PARAMETER);
        }

        int result = deviceService.devicePropertySet(workspaceId, dockSn, param);
        return PropertySetReplyResultEnum.SUCCESS.getResult() == result ?
                HttpResultResponse.success() : HttpResultResponse.error(result, String.valueOf(result));
    }

}
