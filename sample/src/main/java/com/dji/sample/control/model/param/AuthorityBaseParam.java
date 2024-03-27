package com.dji.sample.control.model.param;

import lombok.Data;

/**
 * 控制权参数基类
 *
 * @author Administrator
 * @date 2023/10/19 15:31
 */
@Data
public class AuthorityBaseParam {
    /**
     * 用户ID
     */
    private String id;
    private String username;
}
