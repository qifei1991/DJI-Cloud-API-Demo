package com.dji.sample.wayline;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * 阻飞条件
 *
 * @author Qfei
 * @date 2026/4/22 10:28
 */
@Data
@NoArgsConstructor
@Accessors(chain = true)
public class StopFlyingCondition {

    private String sn;

    private Integer windSpeed = 9;

}
