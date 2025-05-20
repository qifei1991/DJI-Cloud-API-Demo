package com.dji.sample.manage.model.receiver;

import com.dji.sample.manage.annotation.PropertyParamPosition;
import com.dji.sample.manage.model.enums.PropertyParamEnum;
import com.dji.sdk.cloudapi.device.OsdDockDrone;
import com.dji.sdk.cloudapi.device.PayloadIndex;
import com.dji.sdk.cloudapi.device.ThermalPaletteStyleEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

/**
 * 调色盘样式设置
 *
 * @author Qfei
 * @date 2025/5/7 10:29
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@PropertyParamPosition(PropertyParamEnum.CHILD)
public class ThermalCurrentPaletteStyleReceiver extends BasicDeviceProperty {

    @NotNull
    private PayloadIndex payloadIndex;

    @NotNull
    private ThermalPaletteStyleEnum thermalCurrentPaletteStyle;

    @Override
    public boolean valid() {
        return thermalCurrentPaletteStyle != null;
    }

    @Override
    public boolean canPublish(OsdDockDrone osd) {
        return true;
    }
}
