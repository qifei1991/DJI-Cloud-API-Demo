package com.dji.sample.cloudapi.model.enums;

import lombok.Getter;

/**
 * 媒体文件类型
 *
 * @author Qfei
 * @date 2023/3/15 11:28
 */
@Getter
public enum MediaFileType {
    /**
     * 媒体文件分类
     */
    IMAGE(1),VIDEO(2);

    private final int code;

    MediaFileType(int code) {
        this.code = code;
    }
}
