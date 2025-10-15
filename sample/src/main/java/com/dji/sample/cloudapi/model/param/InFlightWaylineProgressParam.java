package com.dji.sample.cloudapi.model.param;

import com.dji.sdk.cloudapi.wayline.InFlightWaylineProgressData;
import com.dji.sdk.cloudapi.wayline.InFlightWaylineStatusEnum;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * 空中航线执行进度
 *
 * @author Qfei
 * @date 2025/8/23 15:29
 */
@Data
@NoArgsConstructor
@Accessors(chain = true)
public class InFlightWaylineProgressParam {

    private String tid;

    private String bid;

    private String dockSn;

    private String droneSn;

    private String inFlightWaylineId;

    private InFlightWaylineProgressData progress;

    private InFlightWaylineStatusEnum status;

    private Integer result; // 错误码

    private Integer wayPointIndex;
}
