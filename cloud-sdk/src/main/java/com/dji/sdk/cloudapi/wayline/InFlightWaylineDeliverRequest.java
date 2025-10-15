package com.dji.sdk.cloudapi.wayline;

import com.dji.sdk.annotations.CloudSDKVersion;
import com.dji.sdk.cloudapi.device.ExitWaylineWhenRcLostEnum;
import com.dji.sdk.common.BaseModel;
import com.dji.sdk.config.version.CloudSDKVersionEnum;
import com.dji.sdk.config.version.GatewayTypeEnum;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

/**
 * 空中航线下发参数
 *
 * @author Qfei
 * @date 2025/8/16 16:52
 */
public class InFlightWaylineDeliverRequest extends BaseModel {

    /**
     * Wayline file object
     */
    @NotNull
    @Valid
    private FlighttaskFile file;

    @NotNull
    @Pattern(regexp = "^[^<>:\"/|?*._\\\\]+$")
    private String inFlightWaylineId;

    /**
     * Height for RTH
     */
    @NotNull
    @Min(20)
    @Max(1500)
    private Integer rthAltitude;

    /**
     * Remote controller out of control action
     * Out of control action: the current fixed transmitted value is 0, meaning Return-to-Home (RTH).
     * Note that this enumeration value definition is inconsistent with the flight control and dock definitions,
     * and a conversion exists at the dock end.
     */
    @NotNull
    private OutOfControlActionEnum outOfControlAction;

    /**
     * wayline out of control action
     * consistent with the KMZ file
     */
    @NotNull
    private ExitWaylineWhenRcLostEnum exitWaylineWhenRcLost;

    @CloudSDKVersion(since = CloudSDKVersionEnum.V1_0_0)
    private RthModeEnum rthMode = RthModeEnum.PRESET_HEIGHT;

    @NotNull
    @CloudSDKVersion(since = CloudSDKVersionEnum.V1_0_1, include = GatewayTypeEnum.DOCK2)
    private WaylinePrecisionTypeEnum waylinePrecisionType;

    public InFlightWaylineDeliverRequest() {
    }

    @Override
    public String toString() {
        return "InFlightWaylineDeliverRequest{" +
                "file=" + file +
                ", inFlightWaylineId='" + inFlightWaylineId + '\'' +
                ", rthAltitude=" + rthAltitude +
                ", outOfControlAction=" + outOfControlAction +
                ", exitWaylineWhenRcLost=" + exitWaylineWhenRcLost +
                ", rthMode=" + rthMode +
                ", waylinePrecisionType=" + waylinePrecisionType +
                '}';
    }

    public FlighttaskFile getFile() {
        return file;
    }

    public InFlightWaylineDeliverRequest setFile(FlighttaskFile file) {
        this.file = file;
        return this;
    }

    public String getInFlightWaylineId() {
        return inFlightWaylineId;
    }

    public InFlightWaylineDeliverRequest setInFlightWaylineId(String inFlightWaylineId) {
        this.inFlightWaylineId = inFlightWaylineId;
        return this;
    }

    public Integer getRthAltitude() {
        return rthAltitude;
    }

    public InFlightWaylineDeliverRequest setRthAltitude(Integer rthAltitude) {
        this.rthAltitude = rthAltitude;
        return this;
    }

    public OutOfControlActionEnum getOutOfControlAction() {
        return outOfControlAction;
    }

    public InFlightWaylineDeliverRequest setOutOfControlAction(OutOfControlActionEnum outOfControlAction) {
        this.outOfControlAction = outOfControlAction;
        return this;
    }

    public ExitWaylineWhenRcLostEnum getExitWaylineWhenRcLost() {
        return exitWaylineWhenRcLost;
    }

    public InFlightWaylineDeliverRequest setExitWaylineWhenRcLost(ExitWaylineWhenRcLostEnum exitWaylineWhenRcLost) {
        this.exitWaylineWhenRcLost = exitWaylineWhenRcLost;
        return this;
    }

    public RthModeEnum getRthMode() {
        return rthMode;
    }

    public InFlightWaylineDeliverRequest setRthMode(RthModeEnum rthMode) {
        this.rthMode = rthMode;
        return this;
    }

    public WaylinePrecisionTypeEnum getWaylinePrecisionType() {
        return waylinePrecisionType;
    }

    public InFlightWaylineDeliverRequest setWaylinePrecisionType(WaylinePrecisionTypeEnum waylinePrecisionType) {
        this.waylinePrecisionType = waylinePrecisionType;
        return this;
    }
}
