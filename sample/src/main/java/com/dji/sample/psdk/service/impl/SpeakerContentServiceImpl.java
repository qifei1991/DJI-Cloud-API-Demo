package com.dji.sample.psdk.service.impl;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.io.file.FileNameUtil;
import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dji.sample.cloudapi.model.param.CreateSpeakerContentParam;
import com.dji.sample.component.oss.model.OssConfiguration;
import com.dji.sample.component.oss.service.impl.OssServiceContext;
import com.dji.sample.psdk.dao.ISpeakerContentMapper;
import com.dji.sample.psdk.model.dto.SpeakerContentDTO;
import com.dji.sample.psdk.model.entity.SpeakerContentEntity;
import com.dji.sample.psdk.model.enums.SpeakerContentTypeEnum;
import com.dji.sample.psdk.service.ISpeakerContentService;
import com.dji.sdk.cloudapi.psdk.PlayAudioFormatEnum;
import com.dji.sdk.common.Pagination;
import com.dji.sdk.common.PaginationData;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

import static com.dji.sample.psdk.service.impl.SpeakerJobServiceImpl.AUDIO_FILE_PREFIX;

/**
 * @author Qfei
 * @date 2024/4/24 14:38
 */
@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class SpeakerContentServiceImpl implements ISpeakerContentService {

    private final ISpeakerContentMapper mapper;
    private final OssServiceContext ossService;

    @Override
    public Optional<SpeakerContentDTO> getSpeakerContentById(String workspaceId, String contentId) {
        return Optional.ofNullable(entity2Dto(mapper.selectOne(
                Wrappers.lambdaQuery(SpeakerContentEntity.class)
                        .eq(SpeakerContentEntity::getWorkspaceId, workspaceId)
                        .eq(SpeakerContentEntity::getContentId, contentId))));
    }

    private SpeakerContentDTO entity2Dto(SpeakerContentEntity entity) {
        if (Objects.isNull(entity)) {
            return null;
        }
        return SpeakerContentDTO.builder()
                .workspaceId(entity.getWorkspaceId())
                .contentId(entity.getContentId())
                .name(entity.getName())
                .type(SpeakerContentTypeEnum.find(entity.getType()))
                .objectKey(entity.getObjectKey())
                .sign(entity.getSign())
                .audioFormat(Objects.nonNull(entity.getAudioFormat()) ? PlayAudioFormatEnum.find(entity.getAudioFormat()) : null)
                .organizationCode(entity.getOrganizationCode())
                .createTime(entity.getCreateTime())
                .updateTime(entity.getUpdateTime())
                .username(entity.getUsername())
                .content(entity.getContent())
                .build();
    }

    @Override
    public URL getAudioFileUrl(String workspaceId, String contentId) {
        Optional<SpeakerContentDTO> contentOpt = getSpeakerContentById(workspaceId, contentId);
        if (contentOpt.isEmpty()) {
            throw new RuntimeException("音频文件不存在。");
        }
        return ossService.getObjectUrl(OssConfiguration.bucket, contentOpt.get().getObjectKey());
    }

    @Override
    public PaginationData<SpeakerContentDTO> getSpeakerContents(String workspaceId, Long page, Long pageSize, String key, String orgCode) {
        Page<SpeakerContentEntity> pageData = mapper.selectPage(
                new Page<>(page, pageSize),
                Wrappers.lambdaQuery(SpeakerContentEntity.class)
                        .eq(SpeakerContentEntity::getWorkspaceId, workspaceId)
                        .likeRight(CharSequenceUtil.isNotBlank(orgCode), SpeakerContentEntity::getOrganizationCode, orgCode)
                        .like(CharSequenceUtil.isNotBlank(key), SpeakerContentEntity::getName, key)
                        .orderByDesc(SpeakerContentEntity::getCreateTime));
        List<SpeakerContentDTO> records = pageData.getRecords()
                .stream()
                .map(this::entity2Dto)
                .collect(Collectors.toList());

        return new PaginationData<>(records, new Pagination(pageData.getCurrent(), pageData.getSize(), pageData.getTotal()));
    }

    @Override
    public Boolean rename(String workspaceId, String contentId, String name, String updateUser) {
        return mapper.update(null,
                new LambdaUpdateWrapper<SpeakerContentEntity>()
                        .set(SpeakerContentEntity::getName, name)
                        .set(SpeakerContentEntity::getUsername, updateUser)
                        .set(SpeakerContentEntity::getUpdateTime, System.currentTimeMillis())
                        .eq(SpeakerContentEntity::getWorkspaceId, workspaceId)
                        .eq(SpeakerContentEntity::getContentId, contentId)) > 0;
    }

    @Override
    public Boolean delete(String workspaceId, String contentId) {
        Optional<SpeakerContentDTO> contentOpt = this.getSpeakerContentById(workspaceId, contentId);
        if (contentOpt.isEmpty()) {
            return true;
        }
        boolean isDel = mapper.delete(new LambdaUpdateWrapper<SpeakerContentEntity>()
                .eq(SpeakerContentEntity::getWorkspaceId, workspaceId)
                .eq(SpeakerContentEntity::getContentId, contentId)) > 0;
        if (!isDel) {
            return false;
        }
        if (!StringUtils.hasText(contentOpt.get().getObjectKey())) {
            return true;
        }
        return ossService.deleteObject(OssConfiguration.bucket, contentOpt.get().getObjectKey());
    }

    @Override
    public String create(String workspaceId, MultipartFile file, CreateSpeakerContentParam param) {
        if (Objects.isNull(param.getType())) {
            throw new RuntimeException("无法判断新建喊话器内容的类型，请查证");
        }
        String contentId = UUID.randomUUID().toString();
        SpeakerContentDTO.SpeakerContentDTOBuilder contentDTO = SpeakerContentDTO.builder()
                .contentId(contentId)
                .workspaceId(workspaceId)
                .type(param.getType())
                .username(param.getCreator())
                .organizationCode(param.getCode())
                .name(param.getName());

        switch (param.getType()) {
            case AUDIO:
                if (Objects.isNull(file) || !StringUtils.hasText(file.getOriginalFilename())) {
                    throw new RuntimeException("喊话内容音频文件格式错误");
                }
                String filename = file.getOriginalFilename();
                Assert.isTrue(isAudioFile(filename), "音频文件格式错误。");
                String objectKey = OssConfiguration.objectDirPrefix
                        + AUDIO_FILE_PREFIX
                        + FileNameUtil.UNIX_SEPARATOR
                        + contentId
                        + StrUtil.DOT
                        + FileNameUtil.extName(filename);
                try {
                    ossService.putObject(OssConfiguration.bucket, objectKey, file.getInputStream());
                } catch (IOException e) {
                    log.error("喊话器文件上传失败，喊话失败。", e);
                    throw new RuntimeException("喊话文件上传失败");
                }
                if (!StringUtils.hasText(param.getName())) {
                    contentDTO.name(FileNameUtil.getPrefix(filename));
                }
                contentDTO.objectKey(objectKey)
                        .audioFormat(PlayAudioFormatEnum.find(FileNameUtil.getSuffix(filename).toLowerCase()));
                try (InputStream object = ossService.getObject(OssConfiguration.bucket, objectKey)) {
                    if (object.available() == 0) {
                        throw new RuntimeException(String.format("无法获取播放文件, objectKey:[%s], bucket[%s].",
                                objectKey, OssConfiguration.bucket));
                    }
                    contentDTO.sign(DigestUtils.md5DigestAsHex(object));
                } catch (IOException e) {
                    log.error("喊话内容保存失败", e);
                    ossService.deleteObject(OssConfiguration.bucket, objectKey);
                    throw new RuntimeException("喊话内容保存失败");
                }
                break;
            case TTS:
                if (!StringUtils.hasText(param.getContent())) {
                    throw new RuntimeException("请输入正确的喊话内容类型。");
                }
                if (!StringUtils.hasText(param.getName())) {
                    contentDTO.name("TTS-" + DatePattern.PURE_DATETIME_FORMAT.format(new Date()));
                }
                contentDTO.content(param.getContent())
                        .sign(DigestUtils.md5DigestAsHex(param.getContent().getBytes()));
                break;
            default:
                throw new RuntimeException("未知的喊话内容类型");
        }
        return mapper.insert(fileDto2Entity(contentDTO.build())) > 0 ? contentId : null;
    }

    private boolean isAudioFile(String filename) {
        return FileNameUtil.isType(filename, "mp3", "pcm", "wav", "amr");
    }

    private SpeakerContentEntity fileDto2Entity(SpeakerContentDTO fileDTO) {
        SpeakerContentEntity entity = new SpeakerContentEntity()
                .setWorkspaceId(fileDTO.getWorkspaceId())
                .setContentId(fileDTO.getContentId())
                .setName(fileDTO.getName())
                .setType(fileDTO.getType().getType())
                .setObjectKey(fileDTO.getObjectKey())
                .setSign(fileDTO.getSign())
                .setContent(fileDTO.getContent())
                .setUsername(fileDTO.getUsername())
                .setOrganizationCode(fileDTO.getOrganizationCode())
                ;
        if (Objects.nonNull(fileDTO.getAudioFormat())) {
            entity.setAudioFormat(fileDTO.getAudioFormat().getFormat());
        }
        return entity;
    }

    public static void main(String[] args) {
        log.info("md5: {}", DigestUtils.md5DigestAsHex("大家不要聚集了啊".getBytes()));
    }
}
