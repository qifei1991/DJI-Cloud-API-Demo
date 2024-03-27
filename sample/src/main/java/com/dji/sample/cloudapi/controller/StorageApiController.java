package com.dji.sample.cloudapi.controller;

import com.dji.sdk.cloudapi.storage.StsCredentialsResponse;
import com.dji.sdk.common.HttpResultResponse;
import com.dji.sample.storage.service.IStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author sean
 * @version 0.3
 * @date 2021/12/29
 */
@RestController
@RequestMapping("${url.cloud-api.prefix}${url.cloud-api.version}/storage/")
public class StorageApiController {

    @Autowired
    private IStorageService storageService;

    /**
     * Get temporary credentials for uploading the media and wayline in DJI Pilot.
     */
    @GetMapping("/sts")
    public HttpResultResponse<StsCredentialsResponse> getSTSCredentials() {

        StsCredentialsResponse stsCredentials = storageService.getSTSCredentials();
        return HttpResultResponse.success(stsCredentials);
    }

}
