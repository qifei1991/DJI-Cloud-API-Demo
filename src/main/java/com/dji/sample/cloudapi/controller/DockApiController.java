package com.dji.sample.cloudapi.controller;

import com.dji.sample.common.model.ResponseResult;
import com.dji.sample.control.model.param.RemoteDebugParam;
import com.dji.sample.control.service.IControlService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 机场控制API控制器
 *
 * @author Qfei
 * @date 2023/8/9 11:15
 */
@Slf4j
@RestController
@RequestMapping("${url.cloud-api.prefix}${url.cloud-api.version}/control/devices")
public class DockApiController {

    @Autowired
    private IControlService controlService;

    @PostMapping("/{sn}/jobs/{service_identifier}")
    public ResponseResult createControlJob(@PathVariable String sn, @PathVariable("service_identifier") String serviceIdentifier,
            @RequestBody(required = false) RemoteDebugParam param) {
        return controlService.controlDockDebug(sn, serviceIdentifier, param);
    }
}
