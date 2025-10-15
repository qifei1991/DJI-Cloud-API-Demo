package com.dji.sample.cloudapi.config;

import cn.hutool.core.io.file.FileNameUtil;
import cn.hutool.core.util.ArrayUtil;
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

    public static String[] videoTypes = new String[]{"mp4", "avi", "m4v"};

    public static String[] unusedFileTypes = new String[]{"mrk", "nav", "obs", "rtk", "tif", "tiff"};

    public void setImageTypes(String[] imageTypes) {
        MediaProperties.imageTypes = ArrayUtil.addAll(MediaProperties.imageTypes, imageTypes);
    }

    public void setVideoTypes(String[] videoTypes) {
        MediaProperties.videoTypes = ArrayUtil.addAll(MediaProperties.videoTypes, videoTypes);
    }

    public void setUnusedFileTypes(String[] unusedFileTypes) {
        MediaProperties.unusedFileTypes = ArrayUtil.addAll(MediaProperties.unusedFileTypes, unusedFileTypes);
    }

    /**
     * 是否是图片文件
     *
     * @param fileName 文件名
     * @return java.lang.Boolean
     */
    public static boolean isImageFile(String fileName) {
        return FileNameUtil.isType(fileName, MediaProperties.imageTypes);
    }

    /**
     * 是否是视频文件
     *
     * @param fileName 文件名
     * @return java.lang.Boolean
     */
    public static boolean isVideoFile(String fileName) {
        return FileNameUtil.isType(fileName, MediaProperties.videoTypes);
    }

    /**
     * 判断是否是无用的文件
     * 1. 机场3开始上传一些无用的文件：mrk、nav、obs、rtk
     * 2. Pilot上传的无用文件：tif、tiff
     * @param fileName
     * @return
     */
    public static boolean isUnusedFile(String fileName) {
        return FileNameUtil.isType(fileName, MediaProperties.unusedFileTypes);
    }
}
