package com.dji.sdk.cloudapi.control;

import com.dji.sdk.exception.CloudSDKException;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Arrays;

/**
 * 红外照片格式枚举
 *
 * @author Qfei
 * @date 2026/3/6 14:02
 */
public enum IrPhotoFormatEnum {

    RJPEG(7),

    DLT664(16),
    ;

    private final int format;

    IrPhotoFormatEnum(int format) {
        this.format = format;
    }

    @JsonValue
    public int getFormat() {
        return format;
    }

    @JsonCreator
    public static IrPhotoFormatEnum find(int format) {
        return Arrays.stream(values()).filter(reasonEnum -> reasonEnum.format == format).findAny()
                .orElseThrow(() -> new CloudSDKException(IrPhotoFormatEnum.class, format));
    }
}
