package com.dji.sample.cloudapi.controller;

import com.dji.sample.psdk.model.dto.PsdkWidgetValuesDTO;
import com.dji.sample.psdk.model.dto.SpeakerContentDTO;
import com.dji.sample.psdk.model.param.SpeakerPlayParam;
import com.dji.sample.psdk.model.param.SpeakerPlaySetParam;
import com.dji.sample.psdk.service.IPsdkService;
import com.dji.sample.psdk.service.ISpeakerContentService;
import com.dji.sample.psdk.service.ISpeakerJobService;
import com.dji.sample.psdk.service.ISpeakerPlayService;
import com.dji.sdk.common.HttpResultResponse;
import com.dji.sdk.common.PaginationData;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Objects;

/**
 * 互联互通API接口
 *
 * @author Qfei
 * @date 2024/4/24 14:07
 */
@Slf4j
@RestController
@RequestMapping("${url.cloud-api.prefix}${url.cloud-api.version}/psdk/workspaces")
@RequiredArgsConstructor
@Validated
public class PsdkApiController {

    private final ISpeakerJobService speakerJobService;
    private final ISpeakerContentService speakerContentService;
    private final ISpeakerPlayService speakerPlayService;
    private final IPsdkService psdkService;

    @PostMapping("/{workspace_id}/devices/{device_sn}/issue/audio")
    public HttpResultResponse issueCreateAudioJob(
            @PathVariable("workspace_id") String workspaceId,
            @PathVariable("device_sn") String deviceSn,
            @RequestParam(name = "creator", defaultValue = "manager-server") String creator,
            @RequestParam("code") String organizationCode, MultipartFile file) {
        if (Objects.isNull(file)) {
            return HttpResultResponse.error("没有接收到内容文件，喊话命令下发失败。");
        }
        return speakerJobService.issueCreateAudioJob(workspaceId, deviceSn, file, creator, organizationCode);
    }

    @PostMapping("{workspace_id}/audio/play-start")
    public HttpResultResponse speakerAudioPlayStart(
            @PathVariable("workspace_id") String workspaceId,
            @RequestBody SpeakerPlayParam speakerPlayParam) {
        return speakerJobService.speakerAudioPlayStart(workspaceId, speakerPlayParam);
    }

    @PostMapping("{workspace_id}/audio/play-stop")
    public HttpResultResponse speakerAudioPlayStop(
            @PathVariable("workspace_id") String workspaceId,
            @RequestBody SpeakerPlayParam speakerPlayParam) {
        return speakerJobService.speakerPlayStop(workspaceId, speakerPlayParam);
    }

    @PostMapping("/{workspace_id}/speaker/play/set-mode")
    public HttpResultResponse setPlayMode(
            @PathVariable("workspace_id") String workspaceId,
            @RequestBody SpeakerPlaySetParam setParam) {
        return speakerPlayService.setPlayMode(workspaceId, setParam);
    }

    @PostMapping("/{workspace_id}/speaker/play/set-volume")
    public HttpResultResponse setPlayVolume(
            @PathVariable("workspace_id") String workspaceId,
            @RequestBody SpeakerPlaySetParam setParam) {
        return speakerPlayService.setPlayVolume(workspaceId, setParam);
    }

    @GetMapping("/{workspace_id}/contents")
    public HttpResultResponse<PaginationData<SpeakerContentDTO>> contents(
            @PathVariable("workspace_id") String workspaceId,
            @RequestParam(defaultValue = "1") Long page,
            @RequestParam(name = "page_size", defaultValue = "10") Long pageSize,
            @RequestParam("code") String organizationCode,
            @RequestParam(name = "key", required = false) String key) {
        return HttpResultResponse.success(speakerContentService.getSpeakerContents(workspaceId, page, pageSize, key, organizationCode));
    }

    @PostMapping("/{workspace_id}/contents")
    public HttpResultResponse create(
            @PathVariable("workspace_id") String workspaceId,
            @RequestParam(name = "creator", defaultValue = "manager-server") String creator,
            @RequestParam("code") String organizationCode, MultipartFile file) {
        return HttpResultResponse.success(speakerContentService.create(workspaceId, file, creator, organizationCode));
    }

    @PutMapping("/{workspace_id}/contents/rename")
    public HttpResultResponse<Boolean> rename(
            @PathVariable("workspace_id") String workspaceId,
            @RequestParam("content_id") String contentId,
            @RequestParam("name") String name,
            @RequestParam("update_user") String updateUser) {
        return HttpResultResponse.success(speakerContentService.rename(workspaceId, contentId, name, updateUser));
    }

    @DeleteMapping("/{workspace_id}/contents")
    public HttpResultResponse<Boolean> delete(
            @PathVariable("workspace_id") String workspaceId,
            @RequestParam("content_id") String contentId) {
        return HttpResultResponse.success(speakerContentService.delete(workspaceId, contentId));
    }

    @GetMapping("/{workspace_id}/psdk-widgets")
    public HttpResultResponse<List<PsdkWidgetValuesDTO>> getPsdkWidgetValues(
            @PathVariable("workspace_id") String workspaceId) {
        return HttpResultResponse.success(psdkService.getPsdkWidgetValues(workspaceId));
    }
}
