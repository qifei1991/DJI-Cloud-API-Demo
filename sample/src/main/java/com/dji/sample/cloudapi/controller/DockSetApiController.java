package com.dji.sample.cloudapi.controller;

import com.dji.sample.manage.model.dto.DockSetDTO;
import com.dji.sample.manage.service.IDockSetService;
import com.dji.sdk.common.HttpResultResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 机场设置
 *
 * @author Qfei
 * @date 2026/5/20 18:21
 */
@RestController
@RequestMapping("${url.cloud-api.prefix}${url.cloud-api.version}/docks")
@RequiredArgsConstructor
public class DockSetApiController {

    private final IDockSetService dockSetService;

    @GetMapping("/workspaces/{workspace_id}/settings")
    public HttpResultResponse getDockSettings(@PathVariable("workspace_id") String workspaceId) {
        return HttpResultResponse.success(dockSetService.getDockSettings(workspaceId));
    }

    @GetMapping("/workspaces/{workspace_id}/devices/{dock_sn}/settings")
    public HttpResultResponse getDockSettingsByDeviceSn(
            @PathVariable("workspace_id") String workspaceId,
            @PathVariable("dock_sn") String dockSn) {
        return HttpResultResponse.success(dockSetService.getDockSettings(workspaceId, dockSn));
    }

    @PostMapping("/workspaces/{workspace_id}/settings")
    public HttpResultResponse saveDockSettings(@PathVariable("workspace_id") String workspaceId,
            @RequestBody List<DockSetDTO> dockSetList) {
        dockSetService.saveDockSettings(workspaceId, dockSetList);
        return HttpResultResponse.success();
    }
}
