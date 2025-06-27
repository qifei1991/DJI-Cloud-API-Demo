package com.dji.sample.interconnection.service;

import com.dji.sample.cloudapi.model.param.CreateSpeakerContentParam;
import com.dji.sample.interconnection.model.dto.SpeakerContentDTO;
import com.dji.sdk.common.PaginationData;
import org.springframework.web.multipart.MultipartFile;

import java.net.URL;
import java.util.Optional;

/**
 * 互联互通
 *
 * @author Qfei
 * @date 2024/4/23 16:53
 */
public interface ISpeakerContentService {

    /**
     * 根据ID获取互联互通内容
     *
     * @param workspaceId
     * @param contentId
     * @return
     */
    Optional<SpeakerContentDTO> getSpeakerContentById(String workspaceId, String contentId);

    URL getAudioFileUrl(String workspaceId, String contentId);

    /**
     * 分页查询互联互通内容
     * @param workspaceId 工作空间ID
     * @param page 分页索引
     * @param pageSize 分页条数
     * @param key 名称模糊查询关键字
     * @return
     */
    PaginationData<SpeakerContentDTO> getSpeakerContents(String workspaceId, Long page, Long pageSize, String key, String organizationCode);

    /**
     * 编辑名称
     * @param workspaceId 工作空间ID
     * @param contentId 互联互通记录ID
     * @param name 名称
     * @return
     */
    Boolean rename(String workspaceId, String contentId, String name, String updateUser);

    /**
     * 删除互联互通内容
     * @param workspaceId 工作空间ID
     * @param contentId 互通互通记录ID
     * @return
     */
    Boolean delete(String workspaceId, String contentId);

    /**
     * 创建一条互联互通记录
     * @param workspaceId 工作空间ID
     * @param file 内容
     * @param creator 创建人
     * @return
     */
    String create(String workspaceId, MultipartFile file, CreateSpeakerContentParam param);
}
