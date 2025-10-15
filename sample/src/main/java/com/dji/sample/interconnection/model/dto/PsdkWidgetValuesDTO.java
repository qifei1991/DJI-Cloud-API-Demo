package com.dji.sample.interconnection.model.dto;

import com.dji.sdk.cloudapi.device.PsdkWidget;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * @author Qfei
 * @date 2025/8/8 17:53
 */
@Data
@NoArgsConstructor
@Accessors(chain = true)
public class PsdkWidgetValuesDTO {

    private String sn;

    private String name;

    private List<PsdkWidget> psdkWidgetValues;
}
