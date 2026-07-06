package com.dji.sample.psdk.service;

import com.dji.sample.psdk.model.param.SpeakerPlayParam;
import com.dji.sample.psdk.model.param.SpeakerTtsPlayParam;
import com.dji.sdk.cloudapi.psdk.SpeakerJobStatusEnum;
import com.dji.sdk.common.HttpResultResponse;
import org.springframework.web.multipart.MultipartFile;

/**
 * PSDK
 *
 * @author Qfei
 * @date 2024/4/23 16:49
 */
public interface ISpeakerJobService {

    /** 保存并下发音频文件
     * @param workspaceId
     * @param deviceSn
     * @param file
     * @param creator
     * @return
     */
    HttpResultResponse issueCreateAudioJob(String workspaceId, String deviceSn, MultipartFile file, String creator, String organizationCode);

    int updateJobStatus(String jobId, SpeakerJobStatusEnum statusEnum);

    // HttpResultResponse issueSpeakerJob(String workspaceId, IssueSpeakerJobParam issueSpeakerJobParam);

    HttpResultResponse speakerPlayStart(String workspaceId, SpeakerPlayParam issueJobParam);

    HttpResultResponse speakerPlayStop(String workspaceId, SpeakerPlayParam issueJobParam);

    HttpResultResponse speakerTtsPlay(String workspaceId, SpeakerTtsPlayParam speakerPlayParam);
}
