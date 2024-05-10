package com.dji.sample.interconnection.service;

import com.dji.sdk.common.HttpResultResponse;
import org.springframework.web.multipart.MultipartFile;

/**
 * PSDK
 *
 * @author Qfei
 * @date 2024/4/23 16:49
 */
public interface ISpeakerJobService {

    /**
     * @param workspaceId
     * @param deviceSn
     * @param file
     * @param creator
     * @return
     */
    HttpResultResponse issueCreateAudioJob(String workspaceId, String deviceSn, MultipartFile file, String creator);

    /**
     * 订阅
     * @param deviceSn
     */
    void subscribe(String deviceSn);
}
