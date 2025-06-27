package com.dji.sample.interconnection.service.impl;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.file.FileNameUtil;
import cn.hutool.core.text.StrPool;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.dji.sample.cloudapi.model.param.CreateSpeakerContentParam;
import com.dji.sample.interconnection.dao.ISpeakerJobMapper;
import com.dji.sample.interconnection.model.dto.SpeakerContentDTO;
import com.dji.sample.interconnection.model.dto.SpeakerJobDTO;
import com.dji.sample.interconnection.model.entity.SpeakerJobEntity;
import com.dji.sample.interconnection.model.enums.SpeakerContentTypeEnum;
import com.dji.sample.interconnection.model.param.SpeakerPlayParam;
import com.dji.sample.interconnection.service.ISpeakerContentService;
import com.dji.sample.interconnection.service.ISpeakerJobService;
import com.dji.sdk.cloudapi.interconnection.*;
import com.dji.sdk.common.HttpResultResponse;
import com.dji.sdk.common.SDKManager;
import com.dji.sdk.mqtt.IMqttTopicService;
import com.dji.sdk.mqtt.events.EventsSubscribe;
import com.dji.sdk.mqtt.property.PropertySetSubscribe;
import com.dji.sdk.mqtt.requests.RequestsSubscribe;
import com.dji.sdk.mqtt.services.ServicesReplyData;
import com.dji.sdk.mqtt.services.ServicesSubscribe;
import com.dji.sdk.mqtt.services.TopicServicesResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
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
@RequiredArgsConstructor
public class SpeakerJobServiceImpl implements ISpeakerJobService {

    public static final String AUDIO_FILE_PREFIX = "/speaker-audio";

    private final SDKInternectionPublishService internectionPublishService;
    private final ISpeakerContentService speakerContentService;
    private final ISpeakerJobMapper speakerJobMapper;

    @Resource
    private IMqttTopicService topicService;

    @Override
    public HttpResultResponse issueCreateAudioJob(String workspaceId, String deviceSn, MultipartFile file,
            String creator, String organizationCode) {

        // 设备定义topic
        this.subscribe(deviceSn);

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
        TopicServicesResponse<ServicesReplyData> serviceReply = internectionPublishService.speakerAudioPlayStart(
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

    /**
     * @param deviceSn
     */
    @Override
    public void subscribe(String deviceSn) {
        // topicService.subscribe(String.format(StatusSubscribe.TOPIC, deviceSn));
        // topicService.subscribe(String.format(StateSubscribe.TOPIC, deviceSn));
        // topicService.subscribe(String.format(OsdSubscribe.TOPIC, deviceSn));
        topicService.subscribe(String.format(ServicesSubscribe.TOPIC, deviceSn));
        topicService.subscribe(String.format(EventsSubscribe.TOPIC, deviceSn));
        topicService.subscribe(String.format(RequestsSubscribe.TOPIC, deviceSn));
        topicService.subscribe(String.format(PropertySetSubscribe.TOPIC, deviceSn));
    }

    @Override
    public HttpResultResponse speakerAudioPlayStart(String workspaceId, SpeakerPlayParam issueJobParam) {

        Optional<SpeakerContentDTO> contentOpt = speakerContentService.getSpeakerContentById(workspaceId, issueJobParam.getContentId());
        if (contentOpt.isEmpty()) {
            return HttpResultResponse.error("音频文件不存在，下发喊话失败。");
        }

        SpeakerContentDTO contentDTO = contentOpt.get();

        // 创建喊话任务
        String jobId = UUID.randomUUID().toString();
        insertSpeakerJob(new SpeakerJobEntity()
                .setJobId(jobId)
                .setName(contentDTO.getName().concat(StrPool.DASHED).concat(DateUtil.format(new Date(), DatePattern.PURE_DATETIME_PATTERN)))
                .setContentId(contentDTO.getContentId())
                .setDeviceSn(issueJobParam.getDeviceSn())
                .setStatus(SpeakerJobStatusEnum.PREPARE.getStatus())
                .setUsername(issueJobParam.getUsername()));

        // 根据内容类型创建不同任务
        TopicServicesResponse<ServicesReplyData> serviceReply;
        switch (contentDTO.getType()) {
            case AUDIO:
                URL url = speakerContentService.getAudioFileUrl(workspaceId, issueJobParam.getContentId());
                serviceReply = internectionPublishService.speakerAudioPlayStart(SDKManager.getDeviceSDK(issueJobParam.getDeviceSn()),
                        new SpeakerAudioPlayStartRequest()
                                .setJobId(jobId)
                                .setPsdkIndex(issueJobParam.getPsdkIndex())
                                .setFile(new PlayAudioFile()
                                        .setName(contentDTO.getName())
                                        .setFormat(contentDTO.getAudioFormat())
                                        .setMd5(contentDTO.getSign())
                                        .setUrl(url.toString())));
                break;
            case TTS:
                serviceReply = internectionPublishService.speakerTtsPlayStart(SDKManager.getDeviceSDK(issueJobParam.getDeviceSn()),
                                new SpeakerTtsPlayStartRequest()
                                        .setPsdkIndex(issueJobParam.getPsdkIndex())
                                        .setTts(new PlayTtsFile()
                                                .setName(contentDTO.getName())
                                                .setMd5(contentDTO.getSign())
                                                .setText(contentDTO.getContent())));
                break;
            default:
                return HttpResultResponse.error("不支持的喊话器内容文件类型，喊话失败。");
        }
        if (!serviceReply.getData().getResult().isSuccess()) {
            updateJobStatus(jobId, SpeakerJobStatusEnum.PLAY_START_FAIL);
            return HttpResultResponse.error(serviceReply.getData().getResult().getMessage());
        }
        return HttpResultResponse.success();
    }

    @Override
    public HttpResultResponse speakerPlayStop(String workspaceId, SpeakerPlayParam speakerPlayParam) {
        TopicServicesResponse<ServicesReplyData> serviceReply = internectionPublishService.speakerPlayStop(
                SDKManager.getDeviceSDK(speakerPlayParam.getDeviceSn()),
                new SpeakerPlayRequest().setPsdkIndex(speakerPlayParam.getPsdkIndex()));
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
