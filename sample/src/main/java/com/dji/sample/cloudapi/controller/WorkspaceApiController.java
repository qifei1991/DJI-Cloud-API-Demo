package com.dji.sample.cloudapi.controller;

import com.dji.sample.manage.model.dto.WorkspaceDTO;
import com.dji.sample.manage.service.IWorkspaceService;
import com.dji.sdk.common.HttpResultResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

/**
 * 工作空间api控制器
 *
 * @author Qfei
 * @date 2024/6/24 16:44
 */
@RestController
@RequestMapping("${url.cloud-api.prefix}${url.cloud-api.version}/workspaces")
public class WorkspaceApiController {

    @Autowired
    private IWorkspaceService workspaceService;

    /**
     * Gets information about the workspace according to the bindCode.
     * @param bindCode 绑定码
     * @return
     */
    @GetMapping("/current/{bind_code}")
    public HttpResultResponse getCurrentWorkspace(@PathVariable("bind_code") String bindCode) {
        Optional<WorkspaceDTO> workspaceOpt = workspaceService.getWorkspaceNameByBindCode(bindCode);
        return HttpResultResponse.success(workspaceOpt);
    }

    @PostMapping
    public HttpResultResponse createWorkspace(@RequestBody WorkspaceDTO workspaceDTO) {
        return HttpResultResponse.success(workspaceService.createWorkspace(workspaceDTO));
    }

}
