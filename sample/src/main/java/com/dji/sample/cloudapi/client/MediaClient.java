package com.dji.sample.cloudapi.client;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.file.FileNameUtil;
import cn.hutool.core.text.CharPool;
import com.dji.sample.cloudapi.config.MediaProperties;
import com.dji.sample.cloudapi.model.enums.MediaFileType;
import com.dji.sample.cloudapi.model.param.MediaFileParam;
import com.dji.sample.cloudapi.util.ClientUri;
import com.dji.sample.component.oss.model.OssConfiguration;
import com.dji.sample.media.model.MediaFileCountDTO;
import com.dji.sdk.cloudapi.media.FileUploadCallbackFlightTask;
import com.dji.sdk.cloudapi.media.MediaUploadCallbackRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.Collections;

/**
 * The client of report media-file.
 *
 * @author Qfei
 * @date 2022/12/22 16:43
 */
@Slf4j
@Component
public class MediaClient extends AbstractClient {

    /**
     * Report media-file upload progress.
     * @param jobId flight id
     * @param mediaFileCountDTO media file count information.
     */
    @Async("asyncThreadPool")
    public void reportMediaUploadProgress(String jobId, MediaFileCountDTO mediaFileCountDTO) {
        try {
            this.applicationJsonPost(ClientUri.URI_MEDIA_PROGRESS, mediaFileCountDTO, jobId);
        } catch (Exception e) {
            log.error("文件上传进度上报出错", e);
        }
    }

    /**
     * Media-file upload callback.
     *
     * @param flightId         flight id
     * @param fileUploadCallbackFile uploaded file information.
     * @param flightTask
     */
    @Async("asyncThreadPool")
    public void dockUploadCallback(String flightId, MediaUploadCallbackRequest fileUploadCallbackFile, FileUploadCallbackFlightTask flightTask) {
        try {
            String saveName = FileNameUtil.getName(fileUploadCallbackFile.getObjectKey());
            MediaFileParam fileParam = MediaFileParam.builder()
                    .sortiesId(flightId)
                    .aircraftSn(fileUploadCallbackFile.getExt().getSn())
                    .type(getFileType(saveName).getCode())
                    .filePath(OssConfiguration.objectDirPrefix + CharPool.SLASH + fileUploadCallbackFile.getPath())
                    .fileName(saveName)
                    .objectKey(fileUploadCallbackFile.getObjectKey())
                    .createTime(fileUploadCallbackFile.getMetadata().getCreatedTime() != null ? 
                            DateUtil.formatLocalDateTime(fileUploadCallbackFile.getMetadata().getCreatedTime()) : 
                            DateUtil.formatLocalDateTime(LocalDateTime.now()))
                    .updateTime(LocalDateTime.now().format(FORMATTER))
                    .uploadStatus(2)
                    .platform(OssConfiguration.provider.getType())
                    .flightType(flightTask.getFlightType().getType())
                    .build();
            this.applicationJsonPost(ClientUri.URI_MEDIA_UPLOAD_CALLBACK, Collections.singleton(fileParam));
        } catch (Exception e) {
            log.error("上传文件上报出错", e);
        }
    }

    /**
     * Media-file upload callback.
     *
     * @param callbackRequest uploaded file information.
     */
    @Async("asyncThreadPool")
    public void rcUploadCallback(MediaUploadCallbackRequest callbackRequest) {
        try {
            String objectKey = callbackRequest.getObjectKey();
            String filename = FileNameUtil.getName(objectKey);
            this.applicationJsonPost(ClientUri.URI_MEDIA_RC_UPLOAD_CALLBACK, MediaFileParam.builder()
                    .sortiesId(callbackRequest.getExt().getFileGroupId())
                    .aircraftSn(callbackRequest.getExt().getSn())
                    .type(getFileType(filename).getCode())
                    .filePath(objectKey.substring(0, objectKey.indexOf(filename)))
                    .fileName(filename)
                    .objectKey(objectKey)
                    .createTime(callbackRequest.getMetadata().getCreatedTime() != null ? 
                            DateUtil.formatLocalDateTime(callbackRequest.getMetadata().getCreatedTime()) : 
                            DateUtil.formatLocalDateTime(LocalDateTime.now()))
                    .updateTime(LocalDateTime.now().format(FORMATTER))
                    .uploadStatus(2)
                    .platform(OssConfiguration.provider.getType())
                    .build());
        } catch (Exception e) {
            log.error("上传文件上报出错", e);
        }
    }

    private MediaFileType getFileType(String filename) {
        if (!StringUtils.hasText(filename)) {
            return MediaFileType.UNKNOWN;
        }
        if (MediaProperties.isImageFile(filename)) {
            return MediaFileType.IMAGE;
        } else if (MediaProperties.isVideoFile(filename)) {
            return MediaFileType.VIDEO;
        } else {
            return MediaFileType.UNKNOWN;
        }
    }

}
