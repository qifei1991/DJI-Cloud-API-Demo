package com.dji.sample.cloudapi.model.param;

import lombok.Data;

/**
 * @author Qfei
 * @date 2022/12/22 14:24
 */
@Data
public class UserParam {
    private Integer userId;
    private String username;
    private Integer userType = 1;
}
