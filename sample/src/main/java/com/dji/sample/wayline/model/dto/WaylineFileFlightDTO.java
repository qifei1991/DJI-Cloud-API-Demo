package com.dji.sample.wayline.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * The wayline file copy which is used by the flight task.
 * @author sean
 * @version 1.0
 * @date 2026/11/20
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WaylineFileFlightDTO {

    private String name;

    private String fileId;

    /**
     * It is the wayline_id in the wayline_file table.
     */
    private String originalFileId;

    private String jobId;

    private String objectKey;

    private String sign;

    private String username;

    private Long createTime;

    private Long updateTime;

}
