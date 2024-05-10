package com.dji.sample.interconnection.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.dji.sample.component.oss.model.OssConfiguration;
import com.dji.sample.component.oss.service.impl.OssServiceContext;
import com.dji.sample.interconnection.dao.ISpeakerContentMapper;
import com.dji.sample.interconnection.model.dto.SpeakerContentDTO;
import com.dji.sample.interconnection.model.entity.SpeakerContentEntity;
import com.dji.sample.interconnection.model.enums.SpeakerContentTypeEnum;
import com.dji.sample.interconnection.service.ISpeakerContentService;
import com.dji.sdk.cloudapi.interconnection.PlayAudioFormatEnum;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

/**
 * @author Qfei
 * @date 2024/4/24 14:38
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SpeakerContentServiceImpl implements ISpeakerContentService {

    private final ISpeakerContentMapper mapper;
    private final OssServiceContext ossService;

    @Override
    public String saveSpeakerContent(String workspaceId, SpeakerContentDTO fileDTO) {

        String interconnectionId = UUID.randomUUID().toString();
        SpeakerContentEntity entity = fileDto2Entity(fileDTO);
        entity.setInterconnectionId(interconnectionId);
        entity.setWorkspaceId(workspaceId);

        if (!StringUtils.hasText(fileDTO.getSign())) {
            try (InputStream object = ossService.getObject(OssConfiguration.bucket, fileDTO.getObjectKey())) {
                if (object.available() == 0) {
                    throw new RuntimeException(String.format("无法获取播放文件, objectKey:[%s], bucket[%s].",
                            fileDTO.getObjectKey(), OssConfiguration.bucket));
                }
                entity.setSign(DigestUtils.md5DigestAsHex(object));
            } catch (IOException e) {
                log.error("保存喊话器播放文件失败", e);
            }
        }

        return mapper.insert(entity) > 0 ? interconnectionId : null;
    }

    @Override
    public SpeakerContentDTO getSpeakerContentById(String workspaceId, String interconnectionId) {
        SpeakerContentEntity entity = mapper.selectOne(
                Wrappers.lambdaQuery(SpeakerContentEntity.class)
                        .eq(SpeakerContentEntity::getWorkspaceId, workspaceId)
                        .eq(SpeakerContentEntity::getInterconnectionId, interconnectionId));

        return entity2Dto(entity);
    }

    private SpeakerContentDTO entity2Dto(SpeakerContentEntity entity) {
        return Optional.ofNullable(entity)
                .map(x -> SpeakerContentDTO.builder()
                        .workspaceId(entity.getWorkspaceId())
                        .interconnectionId(entity.getInterconnectionId())
                        .name(entity.getName())
                        .type(SpeakerContentTypeEnum.find(entity.getType()))
                        .objectKey(entity.getObjectKey())
                        .sign(entity.getSign())
                        .audioFormat(Objects.nonNull(entity.getAudioFormat()) ? PlayAudioFormatEnum.find(entity.getAudioFormat()) : null)
                        .createTime(entity.getCreateTime())
                        .updateTime(entity.getUpdateTime())
                        .build()).orElse(null);
    }

    @Override
    public URL getAudioFileUrl(String workspaceId, String interconnectionId) {
        SpeakerContentDTO contentDTO = getSpeakerContentById(workspaceId, interconnectionId);
        if (Objects.isNull(contentDTO)) {
            throw new RuntimeException("音频文件不存在。");
        }
        return ossService.getObjectUrl(OssConfiguration.bucket, contentDTO.getObjectKey());
    }

    private SpeakerContentEntity fileDto2Entity(SpeakerContentDTO fileDTO) {
        return SpeakerContentEntity.builder()
                .workspaceId(fileDTO.getWorkspaceId())
                .name(fileDTO.getName())
                .type(fileDTO.getType().getType())
                .objectKey(fileDTO.getObjectKey())
                .audioFormat(fileDTO.getAudioFormat().getFormat())
                .sign(fileDTO.getSign())
                .username(fileDTO.getUsername())
                .build();
    }
}
