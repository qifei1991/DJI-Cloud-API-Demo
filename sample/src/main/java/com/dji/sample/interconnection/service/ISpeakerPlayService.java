package com.dji.sample.interconnection.service;

import com.dji.sample.interconnection.model.param.SpeakerPlaySetParam;
import com.dji.sdk.common.HttpResultResponse;

/**
 * 喊话器服务类
 *
 * @author Qfei
 * @date 2024/8/12 18:01
 */
public interface ISpeakerPlayService {

    HttpResultResponse setPlayMode(String workspaceId, SpeakerPlaySetParam setParam);

    HttpResultResponse setPlayVolume(String workspaceId, SpeakerPlaySetParam setParam);
}
