package com.dji.sample.cloudapi.client;

import cn.hutool.core.date.DateUtil;
import com.dji.sample.cloudapi.model.param.MediaFileParam;
import com.dji.sample.cloudapi.util.ClientUri;
import com.dji.sample.media.model.FileUploadDTO;
import com.dji.sample.media.model.MediaFileCountDTO;
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
public class MediaClient extends AbstractClient{

    /**
     * Report media-file upload progress.
     * @param jobId flight id
     * @param mediaFileCountDTO media file count information.
     */
    @Async("asyncThreadPool")
    public void reportMediaUploadProgress(String jobId, MediaFileCountDTO mediaFileCountDTO) {
        this.applicationJsonPost(ClientUri.URI_MEDIA_PROGRESS, mediaFileCountDTO, jobId);
    }

    /**
     * Media-file upload callback.
     * @param jobId flight id
     * @param fileUploadDTO uploaded file information.
     */
    @Async("asyncThreadPool")
    public void uploadCallback(String jobId, FileUploadDTO fileUploadDTO) {
        String saveName = fileUploadDTO.getObjectKey().substring(fileUploadDTO.getObjectKey().lastIndexOf("/") + 1);
        MediaFileParam fileParam = MediaFileParam.builder()
                .sortiesId(jobId)
                .aircraftSn(fileUploadDTO.getExt().getSn())
                .filePath(fileUploadDTO.getPath())
                .fileName(saveName)
                .createTime(DateUtil.formatDateTime(fileUploadDTO.getMetadata().getCreatedTime()))
                .updateTime(LocalDateTime.now().format(FORMATTER))
                .uploadStatus(2)
                .build();
        this.applicationJsonPost(ClientUri.URI_MEDIA_UPLOAD_CALLBACK, Collections.singleton(fileParam));
    }
}
