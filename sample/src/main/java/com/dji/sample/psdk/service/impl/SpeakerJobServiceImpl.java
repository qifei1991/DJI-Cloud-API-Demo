package com.dji.sample.psdk.service.impl;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.file.FileNameUtil;
import cn.hutool.core.text.StrPool;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.dji.sample.psdk.dao.ISpeakerJobMapper;
import com.dji.sample.psdk.model.dto.SpeakerContentDTO;
import com.dji.sample.psdk.model.dto.SpeakerJobDTO;
import com.dji.sample.psdk.model.entity.SpeakerJobEntity;
import com.dji.sample.psdk.model.param.SpeakerPlayParam;
import com.dji.sample.psdk.service.IPsdkWidgetRedisService;
import com.dji.sample.psdk.service.ISpeakerContentService;
import com.dji.sample.psdk.service.ISpeakerJobService;
import com.dji.sdk.cloudapi.device.PsdkNameEnum;
import com.dji.sdk.cloudapi.device.PsdkWidget;
import com.dji.sdk.cloudapi.psdk.PlayAudioFile;
import com.dji.sdk.cloudapi.psdk.SpeakerAudioPlayStartRequest;
import com.dji.sdk.cloudapi.psdk.SpeakerJobStatusEnum;
import com.dji.sdk.cloudapi.psdk.SpeakerPlayRequest;
import com.dji.sdk.common.HttpResultResponse;
import com.dji.sdk.common.SDKManager;
import com.dji.sdk.config.version.GatewayManager;
import com.dji.sdk.mqtt.services.ServicesReplyData;
import com.dji.sdk.mqtt.services.TopicServicesResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.net.URL;
import java.util.*;

/**
 * @author Qfei
 * @date 2024/4/23 17:05
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SpeakerJobServiceImpl implements ISpeakerJobService {

    public static final String AUDIO_FILE_PREFIX = "/speaker-audio";

    private final SDKPsdkPublishService sdkPsdkPublishService;
    private final ISpeakerContentService speakerContentService;
    private final ISpeakerJobMapper speakerJobMapper;
    private final IPsdkWidgetRedisService psdkWidgetRedisService;

    @Override
    public HttpResultResponse issueCreateAudioJob(String workspaceId, String deviceSn, MultipartFile file,
            String creator, String organizationCode) {

        String contentId = speakerContentService.create(workspaceId, file, creator, organizationCode);
        if (!StringUtils.hasText(contentId)) {
            return HttpResultResponse.error("音频文件保存失败，喊话失败。");
        }

        Optional<SpeakerContentDTO> contentOpt = speakerContentService.getSpeakerContentById(workspaceId, contentId);
        if (contentOpt.isEmpty()) {
            return HttpResultResponse.error("音频文件不存在，喊话失败。");
        }
        SpeakerContentDTO dto = contentOpt.get();
        URL url = speakerContentService.getAudioFileUrl(workspaceId, contentId);
        TopicServicesResponse<ServicesReplyData> serviceReply = sdkPsdkPublishService.speakerAudioPlayStart(
                SDKManager.getDeviceSDK(deviceSn),
                new SpeakerAudioPlayStartRequest()
                        .setPsdkIndex(0)
                        .setFile(new PlayAudioFile()
                                .setName(dto.getName())
                                .setFormat(dto.getAudioFormat())
                                .setMd5(dto.getSign())
                                .setUrl(url.toString())));
        if (!serviceReply.getData().getResult().isSuccess()) {
            return HttpResultResponse.error(serviceReply.getData().getResult().getMessage());
        }
        return HttpResultResponse.success();
    }

    @Override
    public HttpResultResponse speakerAudioPlayStart(String workspaceId, SpeakerPlayParam issueJobParam) {

        Optional<SpeakerContentDTO> contentOpt = speakerContentService.getSpeakerContentById(workspaceId, issueJobParam.getContentId());
        if (contentOpt.isEmpty()) {
            return HttpResultResponse.error("音频文件不存在，下发喊话失败。");
        }
        GatewayManager gatewayManager = SDKManager.getDeviceSDK(issueJobParam.getDeviceSn());
        Optional<List<PsdkWidget>> dronePsdkValues = psdkWidgetRedisService.getPsdkWidgetValues(gatewayManager.getDroneSn());
        if (dronePsdkValues.isEmpty()) {
            return HttpResultResponse.error("设备不存在psdk负载");
        }
        Optional<PsdkWidget> speakerOpt = dronePsdkValues.get()
                .stream()
                .filter(x -> PsdkNameEnum.SPEAKER == x.getPsdkName()).findFirst();
        if (speakerOpt.isEmpty()) {
            return HttpResultResponse.error("设备不存在喊话器");
        }
        // 创建喊话任务
        SpeakerContentDTO contentDTO = contentOpt.get();
        String jobId = UUID.randomUUID().toString();
        insertSpeakerJob(new SpeakerJobEntity()
                .setJobId(jobId)
                .setName(contentDTO.getName()
                        .concat(StrPool.DASHED)
                        .concat(DateUtil.format(new Date(), DatePattern.PURE_DATETIME_PATTERN)))
                .setContentId(contentDTO.getContentId())
                .setDeviceSn(issueJobParam.getDeviceSn())
                .setStatus(SpeakerJobStatusEnum.PREPARE.getStatus())
                .setUsername(issueJobParam.getUsername()));
        URL url = speakerContentService.getAudioFileUrl(workspaceId, issueJobParam.getContentId());
        TopicServicesResponse<ServicesReplyData> serviceReply = sdkPsdkPublishService.speakerAudioPlayStart(
                gatewayManager,
                new SpeakerAudioPlayStartRequest()
                        .setJobId(jobId)
                        .setPsdkIndex(speakerOpt.get().getPsdkIndex())
                        .setFile(new PlayAudioFile()
                                .setName(contentDTO.getName())
                                .setFormat(contentDTO.getAudioFormat())
                                .setMd5(contentDTO.getSign())
                                .setUrl(url.toString())));
        if (!serviceReply.getData().getResult().isSuccess()) {
            updateJobStatus(jobId, SpeakerJobStatusEnum.PLAY_START_FAIL);
            return HttpResultResponse.error(serviceReply.getData().getResult().getMessage());
        }
        return HttpResultResponse.success();
    }

    @Override
    public HttpResultResponse speakerPlayStop(String workspaceId, SpeakerPlayParam speakerPlayParam) {
        GatewayManager gatewayManager = SDKManager.getDeviceSDK(speakerPlayParam.getDeviceSn());
        Optional<List<PsdkWidget>> dronePsdkValues = psdkWidgetRedisService.getPsdkWidgetValues(gatewayManager.getDroneSn());
        if (dronePsdkValues.isEmpty()) {
            return HttpResultResponse.error("设备不存在psdk负载");
        }
        Optional<PsdkWidget> speakerOpt = dronePsdkValues.get()
                .stream()
                .filter(x -> PsdkNameEnum.SPEAKER == x.getPsdkName()).findFirst();
        if (speakerOpt.isEmpty()) {
            return HttpResultResponse.error("设备不存在喊话器");
        }
        TopicServicesResponse<ServicesReplyData> serviceReply = sdkPsdkPublishService.speakerPlayStop(
                gatewayManager,
                new SpeakerPlayRequest().setPsdkIndex(speakerOpt.get().getPsdkIndex()));
        if (!serviceReply.getData().getResult().isSuccess()) {
            updateJobStatus(serviceReply.getBid(), SpeakerJobStatusEnum.PLAY_STOP_FAIL);
            return HttpResultResponse.error(serviceReply.getData().getResult().getMessage());
        }
        return HttpResultResponse.success();
    }

    @Override
    public int updateJobStatus(String jobId, SpeakerJobStatusEnum statusEnum) {
        return speakerJobMapper.update(new SpeakerJobEntity().setStatus(statusEnum.getStatus()),
                new LambdaUpdateWrapper<SpeakerJobEntity>().eq(SpeakerJobEntity::getJobId, jobId));
    }

    private Optional<SpeakerJobDTO> insertSpeakerJob(SpeakerJobEntity entity) {
        int insert = speakerJobMapper.insert(entity);
        return insert <= 0 ? Optional.empty() : Optional.ofNullable(entity2Dto(entity));
    }

    private SpeakerJobDTO entity2Dto(SpeakerJobEntity entity) {
        if (Objects.isNull(entity)) {
            return null;
        }
        return SpeakerJobDTO.builder()
                .jobId(entity.getJobId())
                .name(entity.getName())
                .contentId(entity.getContentId())
                .status(SpeakerJobStatusEnum.find(entity.getStatus()))
                .deviceSn(entity.getDeviceSn())
                .workspaceId(entity.getWorkspaceId())
                .username(entity.getUsername())
                .createTime(entity.getCreateTime())
                .updateTime(entity.getUpdateTime())
                .build();
    }

    private boolean isAudioFile(String filename) {
        return FileNameUtil.isType(filename, "mp3", "pcm", "wav", "amr");
    }
}
