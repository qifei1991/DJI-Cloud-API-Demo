package com.dji.sample.cloudapi.client;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.file.FileNameUtil;
import cn.hutool.core.text.CharPool;
import com.dji.sample.cloudapi.model.enums.MediaFileType;
import com.dji.sample.cloudapi.model.param.MediaFileParam;
import com.dji.sample.cloudapi.util.ClientUri;
import com.dji.sample.component.oss.model.OssConfiguration;
import com.dji.sample.media.model.MediaFileCountDTO;
import com.dji.sdk.cloudapi.media.MediaUploadCallbackRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

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
     * @param jobId flight id
     * @param fileUploadDTO uploaded file information.
     */
    @Async("asyncThreadPool")
    public void uploadCallback(String flightId, MediaUploadCallbackRequest fileUploadCallbackFile) {
        try {
            String saveName = fileUploadCallbackFile.getObjectKey()
                    .substring(fileUploadCallbackFile.getObjectKey().lastIndexOf("/") + 1);
            MediaFileParam.MediaFileParamBuilder builder = MediaFileParam.builder();
            if (!isImageFile(saveName)) {
                builder.type(MediaFileType.VIDEO.getCode());
            }
            MediaFileParam fileParam = builder
                    .sortiesId(flightId)
                    .aircraftSn(fileUploadCallbackFile.getExt().getSn())
                    .filePath(OssConfiguration.objectDirPrefix + CharPool.SLASH + fileUploadCallbackFile.getPath())
                    .fileName(saveName)
                    .createTime(DateUtil.formatLocalDateTime(fileUploadCallbackFile.getMetadata().getCreatedTime()))
                    .updateTime(LocalDateTime.now().format(FORMATTER))
                    .uploadStatus(2)
                    .platform(OssConfiguration.provider.getType())
                    .build();
            this.applicationJsonPost(ClientUri.URI_MEDIA_UPLOAD_CALLBACK, Collections.singleton(fileParam));
        } catch (Exception e) {
            log.error("上传文件上报出错", e);
        }
    }


    /**
     * 是否是图片文件
     *
     * @param fileName 文件名
     * @return java.lang.Boolean
     */
    public static boolean isImageFile(String fileName) {
        return FileNameUtil.isType(fileName, "jpeg", "jpg", "png");
    }
}
