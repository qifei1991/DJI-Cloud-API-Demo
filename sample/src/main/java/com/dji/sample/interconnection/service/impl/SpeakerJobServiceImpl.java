package com.dji.sample.interconnection.service.impl;

import cn.hutool.core.io.file.FileNameUtil;
import com.dji.sample.component.oss.model.OssConfiguration;
import com.dji.sample.component.oss.service.impl.OssServiceContext;
import com.dji.sample.interconnection.model.dto.SpeakerContentDTO;
import com.dji.sample.interconnection.model.enums.SpeakerContentTypeEnum;
import com.dji.sample.interconnection.service.ISpeakerContentService;
import com.dji.sample.interconnection.service.ISpeakerJobService;
import com.dji.sdk.cloudapi.interconnection.PlayAudioFile;
import com.dji.sdk.cloudapi.interconnection.PlayAudioFormatEnum;
import com.dji.sdk.cloudapi.interconnection.SpeakerAudioPlayStartRequest;
import com.dji.sdk.common.HttpResultResponse;
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
import java.io.IOException;
import java.net.URL;

/**
 * @author Qfei
 * @date 2024/4/23 17:05
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SpeakerJobServiceImpl implements ISpeakerJobService {

    public static final String AUDIO_FILE_PREFIX = "/speaker-audio";

    private final OssServiceContext ossService;
    private final SDKInterconnectionService abstractInterconnectionService;
    private final ISpeakerContentService speakerContentService;

    @Resource
    private IMqttTopicService topicService;

    @Override
    public HttpResultResponse issueCreateAudioJob(String workspaceId, String deviceSn, MultipartFile file, String creator) {

        // 设备定义topic
        this.subscribe(deviceSn);

        String filename = file.getOriginalFilename();
        if (!isAudioFile(filename)) {
            return HttpResultResponse.error("音频文件格式错误，喊话命令下发失败。");
        }

        String name = deviceSn + "-audio-" + System.currentTimeMillis();
        SpeakerContentDTO contentDTO = SpeakerContentDTO.builder()
                .name(name)
                .type(SpeakerContentTypeEnum.AUDIO)
                .objectKey(OssConfiguration.objectDirPrefix + AUDIO_FILE_PREFIX + FileNameUtil.UNIX_SEPARATOR + filename)
                .audioFormat(PlayAudioFormatEnum.find(FileNameUtil.getSuffix(filename).toLowerCase()))
                .username(creator)
                .build();

        try {
            ossService.putObject(OssConfiguration.bucket, contentDTO.getObjectKey(), file.getInputStream());
        } catch (IOException e) {
            log.error("喊话器文件上传失败，喊话命令下发失败。", e);
            return HttpResultResponse.error(e.getMessage());
        }

        String interconnectionId = speakerContentService.saveSpeakerContent(workspaceId, contentDTO);
        if (!StringUtils.hasText(interconnectionId)) {
            return HttpResultResponse.error("音频文件保存失败，喊话命令下发失败。");
        }

        SpeakerContentDTO dto = speakerContentService.getSpeakerContentById(workspaceId, interconnectionId);
        URL url = speakerContentService.getAudioFileUrl(workspaceId, interconnectionId);
        TopicServicesResponse<ServicesReplyData> serviceReply = abstractInterconnectionService.speakerAudioPlayStart(deviceSn,
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

    private boolean isAudioFile(String filename) {
        return FileNameUtil.isType(filename, "mp3", "pcm", "wav", "amr");
    }
}
