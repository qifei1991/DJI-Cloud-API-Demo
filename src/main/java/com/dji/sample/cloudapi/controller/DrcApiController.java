package com.dji.sample.cloudapi.controller;

import com.dji.sample.common.model.ResponseResult;
import com.dji.sample.control.model.dto.JwtAclDTO;
import com.dji.sample.control.model.dto.MqttBrokerDTO;
import com.dji.sample.control.model.param.DrcConnectParam;
import com.dji.sample.control.model.param.DrcModeParam;
import com.dji.sample.control.service.IDrcService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

/**
 * Drc
 *
 * @author Qfei
 * @date 2023/9/3 11:28
 */
@Slf4j
@RestController
@RequestMapping("${url.cloud-api.prefix}${url.cloud-api.version}/control")
public class DrcApiController {

    @Autowired
    private IDrcService drcService;

    @PostMapping("/workspaces/{workspace_id}/drc/connect")
    public ResponseResult drcConnect(@PathVariable("workspace_id") String workspaceId, HttpServletRequest request,
            @Valid @RequestBody DrcConnectParam param) {

        MqttBrokerDTO brokerDTO = drcService.userDrcAuth(workspaceId, param.getUserId(), param.getUsername(), param);
        return ResponseResult.success(brokerDTO);
    }

    @PostMapping("/workspaces/{workspace_id}/drc/enter")
    public ResponseResult drcEnter(@PathVariable("workspace_id") String workspaceId, @Valid @RequestBody DrcModeParam param) {
        JwtAclDTO acl = drcService.deviceDrcEnter(workspaceId, param);

        return ResponseResult.success(acl);
    }

    @PostMapping("/workspaces/{workspace_id}/drc/exit")
    public ResponseResult drcExit(@PathVariable("workspace_id") String workspaceId, @Valid @RequestBody DrcModeParam param) {
        drcService.deviceDrcExit(workspaceId, param);

        return ResponseResult.success();
    }
}
