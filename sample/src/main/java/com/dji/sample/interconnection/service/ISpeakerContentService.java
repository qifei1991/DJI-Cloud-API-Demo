package com.dji.sample.interconnection.service;

import com.dji.sample.interconnection.model.dto.SpeakerContentDTO;

import java.net.URL;

/**
 * 互联互通
 *
 * @author Qfei
 * @date 2024/4/23 16:53
 */
public interface ISpeakerContentService {

    String saveSpeakerContent(String workspaceId, SpeakerContentDTO fileDTO);

    SpeakerContentDTO getSpeakerContentById(String workspaceId, String interconnectionId);

    URL getAudioFileUrl(String workspaceId, String interconnectionId);
}
