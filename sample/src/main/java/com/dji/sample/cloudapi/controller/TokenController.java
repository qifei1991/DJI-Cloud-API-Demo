package com.dji.sample.cloudapi.controller;

import com.dji.sample.cloudapi.model.param.UserParam;
import com.dji.sample.cloudapi.service.TokenService;
import com.dji.sdk.common.HttpResultResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * The controller of User.
 *
 * @author Qfei
 * @date 2022/12/22 12:00
 */
@RestController
@RequestMapping("${url.cloud-api.prefix}${url.cloud-api.version}/token")
public class TokenController {

    @Autowired
    private TokenService tokenService;

    @PostMapping
    public HttpResultResponse getToken(@RequestBody UserParam userParam) {

        return this.tokenService.getToken(userParam);
    }
}
