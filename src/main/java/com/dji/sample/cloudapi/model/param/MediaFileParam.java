package com.dji.sample.cloudapi.model.param;

import lombok.Builder;
import lombok.Data;

/**
 * 媒体文件数据对象
 *
 * @author Qfei
 * @date 2022/11/23 18:04
 */
@Data
@Builder
public class MediaFileParam {
    private String sortiesId;
    /**
     * 无人机SN码
     */
    private String aircraftSn;
    /**
     * 上传到文件服务的状态
     */
    private Integer uploadStatus;
    /**
     * 文件类型，1图片，2视频
     */
    @Builder.Default
    private Integer type = 1;
    /**
     * 文件保存相对路径
     */
    private String filePath;
    /**
     * 文件保存名称
     */
    private String fileName;

    private String createTime;
    private String updateTime;

}
