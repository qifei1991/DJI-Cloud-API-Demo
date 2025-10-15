package com.dji.sdk.cloudapi.wayline;

/**
 * 空中下发航线状态上报
 *
 * @author Qfei
 * @date 2025/8/18 17:25
 */
public class InFlightWaylineProgress {

    private String inFlightWaylineId;

    private InFlightWaylineProgressData progress;

    private InFlightWaylineStatusEnum status;

    private Integer result; // 错误码

    private Integer wayPointIndex;

    public InFlightWaylineProgress() {
    }

    @Override
    public String toString() {
        return "InFlightWaylineProgress{" +
                "inFlightWaylineId='" + inFlightWaylineId + '\'' +
                ", progress=" + progress +
                ", status=" + status +
                ", result=" + result +
                ", wayPointIndex=" + wayPointIndex +
                '}';
    }

    public String getInFlightWaylineId() {
        return inFlightWaylineId;
    }

    public InFlightWaylineProgress setInFlightWaylineId(String inFlightWaylineId) {
        this.inFlightWaylineId = inFlightWaylineId;
        return this;
    }

    public InFlightWaylineProgressData getProgress() {
        return progress;
    }

    public InFlightWaylineProgress setProgress(InFlightWaylineProgressData progress) {
        this.progress = progress;
        return this;
    }

    public InFlightWaylineStatusEnum getStatus() {
        return status;
    }

    public InFlightWaylineProgress setStatus(InFlightWaylineStatusEnum status) {
        this.status = status;
        return this;
    }

    public Integer getResult() {
        return result;
    }

    public InFlightWaylineProgress setResult(Integer result) {
        this.result = result;
        return this;
    }

    public Integer getWayPointIndex() {
        return wayPointIndex;
    }

    public InFlightWaylineProgress setWayPointIndex(Integer wayPointIndex) {
        this.wayPointIndex = wayPointIndex;
        return this;
    }
}
