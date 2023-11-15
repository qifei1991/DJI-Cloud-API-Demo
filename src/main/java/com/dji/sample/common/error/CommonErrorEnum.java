package com.dji.sample.common.error;

/**
 * @author sean.zhou
 * @version 0.1
 * @date 2021/11/25
 */
public enum CommonErrorEnum implements IErrorInfo {

    ILLEGAL_ARGUMENT(200001, "参数错误"),

    REDIS_DATA_NOT_FOUND(201404, "Redis data does not exist."),

    DEVICE_OFFLINE(212015, "设备离线."),

    GET_ORGANIZATION_FAILED(210230, "获取组织失败."),

    DEVICE_BINDING_FAILED(210231, "设备绑定失败."),

    NON_REPEATABLE_BINDING(210232, "设备已绑定其它组织，不能重复绑定."),

    GET_DEVICE_BINDING_STATUS_FAILED(210233, "获取设备绑定状态失败."),

    SYSTEM_ERROR(600500, "系统错误"),

    SECRET_INVALID(600100, "secret invalid"),

    NO_TOKEN(600101, "令牌为空"),

    TOKEN_EXPIRED(600102, "令牌失效"),

    TOKEN_INVALID(600103, "令牌无效"),

    SIGN_INVALID(600104, "签名无效");

    private String msg;

    private int code;

    CommonErrorEnum(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    @Override
    public String getErrorMsg() {
        return this.msg;
    }

    @Override
    public Integer getErrorCode() {
        return this.code;
    }
}
