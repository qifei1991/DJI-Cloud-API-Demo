package com.dji.sample.manage.model.receiver;

import com.dji.sample.manage.annotation.PropertyParamPosition;
import com.dji.sample.manage.model.enums.PropertyParamEnum;
import com.dji.sdk.cloudapi.device.CameraWatermarkSettings;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.Objects;

/**
 * 水印设置参数
 *
 * @author Qfei
 * @date 2026/3/6 16:32
 */
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@PropertyParamPosition(PropertyParamEnum.CHILD)
public class CameraWatermarkSettingsReceiver extends BasicDeviceProperty {

    private CameraWatermarkSettings cameraWatermarkSettings;

    @Override
    public boolean valid() {
        return Objects.nonNull(cameraWatermarkSettings);
    }
}
