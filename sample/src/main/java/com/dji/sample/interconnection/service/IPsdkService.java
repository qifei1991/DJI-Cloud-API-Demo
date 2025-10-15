package com.dji.sample.interconnection.service;

import com.dji.sample.interconnection.model.dto.PsdkWidgetValuesDTO;

import java.util.List;

/**
 * psdk服务
 *
 * @author Qfei
 * @date 2025/8/8 17:41
 */
public interface IPsdkService {

    List<PsdkWidgetValuesDTO> getPsdkWidgetValues(String workspaceId);
}
