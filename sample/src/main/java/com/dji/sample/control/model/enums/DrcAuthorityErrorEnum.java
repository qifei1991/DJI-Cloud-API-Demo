package com.dji.sample.control.model.enums;

import com.dji.sdk.common.IErrorInfo;
import com.fasterxml.jackson.annotation.JsonCreator;

import java.util.Arrays;

/**
 * 控制权错误码
 *
 * @author Administrator
 * @date 2023/10/20 13:48
 */
public enum DrcAuthorityErrorEnum implements IErrorInfo {

    /**
     * DRC控制常见功能错误
     */
    DOCK_DISCONNECTED(916101, "机场离线，请检查机场状态。"),
    PAYLOAD_MISSING(916102, "无法获取设备负载列表，请稍候重试。"),
    PAYLOAD_CANNOT_GET(916103, "无法获取负载详情，请稍候重试。"),

    /**
     * DRC控制常见功能警告提示错误
     */
    DRONE_CONTROL_B(916201, "设备处于遥控器控制（B控）模式。"),
    DRONE_CONTROLLING(916202, "设备正在被其它飞手控制，请谨慎操作。"),
    DRONE_CONTROLLED_RECENT(916203, "设备当前或最近被其它用户控制，请关注飞行器当前工作状态且谨慎操作。"),
    PAYLOAD_CONTROL_B(916204, "设备负载处于遥控器控制（B控）模式。"),
    PAYLOAD_CONTROLLING(916205, "负载相机正在被其它飞手控制，请谨慎操作。"),
    PAYLOAD_CONTROLLED_RECENT(916206, "负载相机当前或最近被其它用户控制，请谨慎操作。"),

    /**
     * 未知错误
     */
    UNKNOWN(916000, "未知服务异常");

    final int code;

    final String msg;

    DrcAuthorityErrorEnum(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    public static DrcAuthorityErrorEnum find(int code) {
        return Arrays.stream(values()).filter(error -> error.code == code).findAny().orElse(UNKNOWN);
    }

    /**
     * Get error message.
     *
     * @return error message
     */
    @Override
    public String getMessage() {
        return msg;
    }

    /**
     * Get error code.
     *
     * @return error code
     */
    @Override
    public Integer getCode() {
        return code;
    }
}
