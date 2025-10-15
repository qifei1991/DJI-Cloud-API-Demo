package com.dji.sdk.cloudapi.wayline;

import com.dji.sdk.exception.CloudSDKException;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Arrays;

/**
 * @author Qfei
 * @date 2025/8/18 17:30
 */
public enum InFlightWaylineStatusEnum {

    /**
     * 状态码说明： status:
     * 1: 上传文件中 (wayline_uploading)
     * 2: 文件上传成功 (wayline_uploaded)
     * 3: 执行任务中 (wayline_progress)
     * 4: 任务暂停 (wayline_paused)
     * 5: 任务取消 (wayline_cancel)
     * 6: 任务成功 (wayline_ok)
     * 7: 任务失败 (wayline_failed)任务失败时的错误码
     * 8: 任务超时
     */
    WAYLINE_UPLOADING(1, "wayline_uploading", false),

    WAYLINE_UPLOADED(2, "wayline_uploaded", false),

    WAYLINE_PROGRESS(3, "wayline_progress", false),

    WAYLINE_PAUSED(4, "wayline_paused", false),

    WAYLINE_CANCEL(5, "wayline_cancel", true),

    WAYLINE_OK(6, "wayline_ok", true),

    WAYLINE_FAILED(7, "wayline_failed", true),

    WAYLINE_TIMEOUT(8, "wayline_timeout",  true);

    private final Integer code;

    private final String status;

    private final boolean end;

    InFlightWaylineStatusEnum(Integer code, String status, boolean end) {
        this.code = code;
        this.status = status;
        this.end = end;
    }

    @JsonValue
    public Integer getCode() {
        return code;
    }

    public String getStatus() {
        return status;
    }

    public boolean getEnd() {
        return end;
    }

    @JsonCreator
    public static InFlightWaylineStatusEnum find(Integer code) {
        return Arrays.stream(values())
                .filter(x -> x.code.equals(code))
                .findAny()
                .orElseThrow(() -> new CloudSDKException(InFlightWaylineStatusEnum.class, code));
    }
}
