package com.dji.sample.psdk.service;

import com.dji.sample.psdk.model.dto.PsdkWidgetValuesDTO;
import com.dji.sample.psdk.model.param.BasePlaySetParam;

import java.util.List;

/**
 * psdk服务
 *
 * @author Qfei
 * @date 2025/8/8 17:41
 */
public interface IPsdkService {

    List<PsdkWidgetValuesDTO> getPsdkWidgetValues(String workspaceId);

    Integer getSetParamPsdkIndex(BasePlaySetParam setParam);
}
