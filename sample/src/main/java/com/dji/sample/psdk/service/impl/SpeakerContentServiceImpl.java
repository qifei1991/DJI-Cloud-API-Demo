package com.dji.sample.psdk.service.impl;

import cn.hutool.core.io.file.FileNameUtil;
import cn.hutool.core.text.CharSequenceUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
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
import org.springframework.util.Assert;
import org.springframework.util.DigestUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.dji.sample.psdk.service.impl.SpeakerJobServiceImpl.AUDIO_FILE_PREFIX;

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
    public PaginationData<SpeakerContentDTO> getSpeakerContents(String workspaceId, Long page, Long pageSize, String key, String organizationCode) {

        Page<SpeakerContentEntity> pageData = mapper.selectPage(new Page<>(page, pageSize), new QueryWrapper<SpeakerContentEntity>()
                .lambda()
                .eq(SpeakerContentEntity::getWorkspaceId, workspaceId)
                .likeRight(SpeakerContentEntity::getOrganizationCode, organizationCode)
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
        return ossService.deleteObject(OssConfiguration.bucket, contentOpt.get().getObjectKey());
    }

    @Override
    public String create(String workspaceId, MultipartFile file, String creator, String organizationCode) {
        String filename = file.getOriginalFilename();
        Assert.isTrue(isAudioFile(filename), "音频文件格式错误。");

        String contentId = UUID.randomUUID().toString();
        String objectKey = OssConfiguration.objectDirPrefix + AUDIO_FILE_PREFIX + FileNameUtil.UNIX_SEPARATOR + filename;
        try {
            ossService.putObject(OssConfiguration.bucket, objectKey, file.getInputStream());
        } catch (IOException e) {
            log.error("喊话器文件上传失败，喊话失败。", e);
            throw new RuntimeException("喊话文件上传失败");
        }

        SpeakerContentEntity entity = fileDto2Entity(SpeakerContentDTO.builder()
                .name(FileNameUtil.getName(FileNameUtil.getPrefix(filename)))
                .type(SpeakerContentTypeEnum.AUDIO)
                .objectKey(objectKey)
                .audioFormat(PlayAudioFormatEnum.find(FileNameUtil.getSuffix(filename).toLowerCase()))
                .username(creator)
                .organizationCode(organizationCode)
                .build());
        entity.setContentId(contentId);
        entity.setWorkspaceId(workspaceId);
        try (InputStream object = ossService.getObject(OssConfiguration.bucket, objectKey)) {
            if (object.available() == 0) {
                throw new RuntimeException(String.format("无法获取播放文件, objectKey:[%s], bucket[%s].",
                        contentId, OssConfiguration.bucket));
            }
            entity.setSign(DigestUtils.md5DigestAsHex(object));
        } catch (IOException e) {
            log.error("喊话内容保存失败", e);
            ossService.deleteObject(OssConfiguration.bucket, objectKey);
            throw new RuntimeException("喊话内容保存失败");
        }

        return mapper.insert(entity) > 0 ? contentId : null;
    }

    private boolean isAudioFile(String filename) {
        return FileNameUtil.isType(filename, "mp3", "pcm", "wav", "amr");
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
                .organizationCode(fileDTO.getOrganizationCode())
                .build();
    }
}
