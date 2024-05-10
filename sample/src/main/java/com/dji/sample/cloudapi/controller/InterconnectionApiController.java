package com.dji.sample.cloudapi.controller;

import com.dji.sample.interconnection.service.ISpeakerJobService;
import com.dji.sdk.common.HttpResultResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Objects;

/**
 * 互联互通API接口
 *
 * @author Qfei
 * @date 2024/4/24 14:07
 */
@Slf4j
@RestController
@RequestMapping("${url.cloud-api.prefix}${url.cloud-api.version}/interconnections/workspaces")
@RequiredArgsConstructor
public class InterconnectionApiController {

    private final ISpeakerJobService speakerJobService;

    @PostMapping("/{workspace_id}/devices/{device_sn}/issue/audio")
    public HttpResultResponse issueCreateAudioJob(@PathVariable("workspace_id") String workspaceId, @PathVariable("device_sn") String deviceSn,
            MultipartFile file, @RequestParam(name = "creator", defaultValue = "manager-server") String creator) {

        if (Objects.isNull(file)) {
            return HttpResultResponse.error("没有接收到内容文件，喊话命令下发失败。");
        }
        return speakerJobService.issueCreateAudioJob(workspaceId, deviceSn, file, creator);
    }
}
