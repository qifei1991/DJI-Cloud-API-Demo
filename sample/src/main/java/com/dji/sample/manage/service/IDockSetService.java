package com.dji.sample.manage.service;

import com.dji.sample.manage.model.dto.DockSetDTO;

import java.util.List;

/**
 * 机场设置服务类
 *
 * @author Qfei
 * @date 2026/5/21 11:05
 */
public interface IDockSetService {

    List<DockSetDTO> getDockSettings(String workspaceId);

    DockSetDTO getDockSettings(String workspaceId, String deviceSn);

    void saveDockSettings(String workspaceId, List<DockSetDTO> dockSetDTO);
}
