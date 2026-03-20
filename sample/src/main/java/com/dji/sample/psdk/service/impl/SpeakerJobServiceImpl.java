package com.dji.sample.psdk.service.impl;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.text.StrPool;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.dji.sample.cloudapi.model.param.CreateSpeakerContentParam;
import com.dji.sample.psdk.dao.ISpeakerJobMapper;
import com.dji.sample.psdk.model.dto.SpeakerContentDTO;
import com.dji.sample.psdk.model.dto.SpeakerJobDTO;
import com.dji.sample.psdk.model.entity.SpeakerJobEntity;
import com.dji.sample.psdk.model.enums.SpeakerContentTypeEnum;
import com.dji.sample.psdk.model.param.SpeakerPlayParam;
import com.dji.sample.psdk.service.IPsdkService;
import com.dji.sample.psdk.service.IPsdkWidgetRedisService;
import com.dji.sample.psdk.service.ISpeakerContentService;
import com.dji.sample.psdk.service.ISpeakerJobService;
import com.dji.sdk.cloudapi.psdk.*;
import com.dji.sdk.common.HttpResultResponse;
import com.dji.sdk.common.SDKManager;
import com.dji.sdk.mqtt.services.ServicesReplyData;
import com.dji.sdk.mqtt.services.TopicServicesResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.net.URL;
import java.util.Date;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

/**
 * @author Qfei
 * @date 2024/4/23 17:05
 */
@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class SpeakerJobServiceImpl implements ISpeakerJobService {

    public static final String AUDIO_FILE_PREFIX = "/speaker-audio";

    private final SDKPsdkPublishService sdkPsdkPublishService;
    private final ISpeakerContentService speakerContentService;
    private final ISpeakerJobMapper speakerJobMapper;
    private final IPsdkWidgetRedisService psdkWidgetRedisService;
    private final MzPilotSpeakerService mzPilotSpeakerService;
    private final IPsdkService psdkService;

    @Override
    public HttpResultResponse issueCreateAudioJob(String workspaceId, String deviceSn, MultipartFile file,
            String creator, String organizationCode) {
        if (mzPilotSpeakerService.isDroneSn(deviceSn)) {
            mzPilotSpeakerService.subscribe(deviceSn);
        }
        String contentId = speakerContentService.create(workspaceId, file,
                new CreateSpeakerContentParam()
                        .setType(SpeakerContentTypeEnum.AUDIO)
                        .setCreator(creator)
                        .setCode(organizationCode));
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
                deviceSn,
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
    public HttpResultResponse speakerPlayStart(String workspaceId, SpeakerPlayParam speakerPlayParam) {
        Integer psdkIndex = psdkService.getSetParamPsdkIndex(speakerPlayParam);
        Optional<SpeakerContentDTO> contentOpt = speakerContentService.getSpeakerContentById(workspaceId, speakerPlayParam.getContentId());
        if (contentOpt.isEmpty()) {
            return HttpResultResponse.error("喊话内容不存在，不能下发喊话任务！");
        }
        // 创建喊话任务
        SpeakerContentDTO contentDTO = contentOpt.get();
        String jobId = UUID.randomUUID().toString();
        insertSpeakerJob(new SpeakerJobEntity()
                .setJobId(jobId)
                .setName(contentDTO.getName()
                        .concat(StrPool.DASHED)
                        .concat(DatePattern.PURE_DATETIME_FORMAT.format(new Date())))
                .setContentId(contentDTO.getContentId())
                .setDeviceSn(speakerPlayParam.getDeviceSn())
                .setStatus(SpeakerJobStatusEnum.PREPARE.getStatus())
                .setUsername(speakerPlayParam.getUsername()));

        // 根据内容类型创建不同任务
        TopicServicesResponse<ServicesReplyData> serviceReply;
        switch (contentDTO.getType()) {
            case AUDIO:
                URL url = speakerContentService.getAudioFileUrl(workspaceId, speakerPlayParam.getContentId());
                serviceReply = sdkPsdkPublishService.speakerAudioPlayStart(speakerPlayParam.getDeviceSn(),
                        new SpeakerAudioPlayStartRequest()
                                .setJobId(jobId)
                                .setPsdkIndex(psdkIndex)
                                .setFile(new PlayAudioFile()
                                        .setName(contentDTO.getName())
                                        .setFormat(contentDTO.getAudioFormat())
                                        .setMd5(contentDTO.getSign())
                                        .setUrl(url.toString())));
                break;
            case TTS:
                serviceReply = sdkPsdkPublishService.speakerTtsPlayStart(SDKManager.getDeviceSDK(speakerPlayParam.getDeviceSn()),
                                new SpeakerTtsPlayStartRequest()
                                        .setJobId(jobId)
                                        .setPsdkIndex(psdkIndex)
                                        .setTts(new PlayTtsFile()
                                                .setName(contentDTO.getName())
                                                .setMd5(contentDTO.getSign())
                                                .setText(contentDTO.getContent())));
                break;
            default:
                return HttpResultResponse.error("不支持的喊话器内容类型，喊话失败。");
        }
        if (!serviceReply.getData().getResult().isSuccess()) {
            updateJobStatus(jobId, SpeakerJobStatusEnum.PLAY_START_FAIL);
            return HttpResultResponse.error(serviceReply.getData().getResult().getMessage());
        }
        return HttpResultResponse.success(jobId);
    }

    @Override
    public HttpResultResponse speakerPlayStop(String workspaceId, SpeakerPlayParam speakerPlayParam) {
        Integer psdkIndex = psdkService.getSetParamPsdkIndex(speakerPlayParam);
        TopicServicesResponse<ServicesReplyData> serviceReply = sdkPsdkPublishService.speakerPlayStop(
                speakerPlayParam.getDeviceSn(), new SpeakerPlayRequest().setPsdkIndex(psdkIndex));
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
}
