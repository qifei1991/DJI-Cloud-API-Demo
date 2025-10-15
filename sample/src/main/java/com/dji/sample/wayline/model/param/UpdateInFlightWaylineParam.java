package com.dji.sample.wayline.model.param;

import com.dji.sample.wayline.model.enums.InFlightWaylineStatusEnum;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

/**
 * 更新空中航线任务状态
 *
 * @author Qfei
 * @date 2025/8/16 17:35
 */
@Data
@NoArgsConstructor
public class UpdateInFlightWaylineParam {

    @NotNull
    private String dockSn;

    @NotNull
    private InFlightWaylineStatusEnum status;
}
