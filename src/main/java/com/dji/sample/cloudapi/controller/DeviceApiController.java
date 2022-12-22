package com.dji.sample.cloudapi.controller;

import com.dji.sample.common.model.ResponseResult;
import com.dji.sample.manage.model.dto.DeviceDTO;
import com.dji.sample.manage.model.enums.DeviceDomainEnum;
import com.dji.sample.manage.service.IDeviceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

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
     * Get the topology list of all dock devices in one workspace.
     * @param workspaceId workspace ID
     * @return Dock devices
     */
    @GetMapping("/{workspace_id}/docks")
    public ResponseResult<List<DeviceDTO>> getDockDevices(@PathVariable("workspace_id") String workspaceId) {
        List<DeviceDTO> devicesList = deviceService.getDevicesTopoForWeb(workspaceId).stream()
                .filter(x -> DeviceDomainEnum.DOCK.getVal() == Integer.parseInt(x.getDomain()))
                .collect(Collectors.toList());
        return ResponseResult.success(devicesList);
    }

}
