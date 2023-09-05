package com.dji.sample.cloudapi.controller;

import com.dji.sample.common.model.ResponseResult;
import com.dji.sample.control.model.enums.DroneAuthorityEnum;
import com.dji.sample.control.model.param.*;
import com.dji.sample.control.service.IControlService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

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

    @PostMapping("/{sn}/jobs/fly-to-point")
    public ResponseResult flyToPoint(@PathVariable String sn, @Valid @RequestBody FlyToPointParam param) {
        return controlService.flyToPoint(sn, param);
    }

    @DeleteMapping("/{sn}/jobs/fly-to-point")
    public ResponseResult flyToPointStop(@PathVariable String sn) {
        return controlService.flyToPointStop(sn);
    }

    @PostMapping("/{sn}/jobs/takeoff-to-point")
    public ResponseResult takeoffToPoint(@PathVariable String sn, @Valid @RequestBody TakeoffToPointParam param) {
        return controlService.takeoffToPoint(sn, param);
    }

    @PostMapping("/{sn}/authority/flight")
    public ResponseResult seizeFlightAuthority(@PathVariable String sn) {
        return controlService.seizeAuthority(sn, DroneAuthorityEnum.FLIGHT, null);
    }

    @PostMapping("/{sn}/authority/payload")
    public ResponseResult seizePayloadAuthority(@PathVariable String sn, @Valid @RequestBody DronePayloadParam param) {
        return controlService.seizeAuthority(sn, DroneAuthorityEnum.PAYLOAD, param);
    }

    @PostMapping("/{sn}/payload/commands")
    public ResponseResult payloadCommands(@PathVariable String sn, @Valid @RequestBody PayloadCommandsParam param) throws Exception {
        param.setSn(sn);
        return controlService.payloadCommands(param);
    }
}
