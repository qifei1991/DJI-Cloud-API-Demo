package com.dji.sample.cloudapi.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * 媒体文件配置文件
 *
 * @author Qfei
 * @date 2025/5/29 14:39
 */
@Data
@Configuration
@ConfigurationProperties("media")
public class MediaProperties {

    public static String[] imageTypes = new String[]{"jpeg", "jpg", "png"};

    public static String[] unusedFileTypes = new String[]{"mrk", "nav", "obs", "rtk", "tif", "tiff"};

    public void setImageTypes(String[] imageTypes) {
        MediaProperties.imageTypes = imageTypes;
    }

    public void setUnusedFileTypes(String[] unusedFileTypes) {
        MediaProperties.unusedFileTypes = unusedFileTypes;
    }
}
