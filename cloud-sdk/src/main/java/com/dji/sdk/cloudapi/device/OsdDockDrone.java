package com.dji.sdk.cloudapi.device;

import com.dji.sdk.annotations.CloudSDKVersion;
import com.dji.sdk.cloudapi.control.CommanderFlightModeEnum;
import com.dji.sdk.cloudapi.control.CommanderModeLostActionEnum;
import com.dji.sdk.config.version.CloudSDKVersionEnum;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * @author sean.zhou
 * @version 0.1
 * @date 2021/11/23
 */
public class OsdDockDrone {

    private Float attitudeHead;

    private Double attitudePitch;

    private Double attitudeRoll;

    private Float elevation;

    private DroneBattery battery;

    private String firmwareVersion;

    private GearEnum gear;

    private Float height;

    private Float homeDistance;

    private Float homeLatitude;

    private Float homeLongitude;

    private Float horizontalSpeed;

    private Float latitude;

    private Float longitude;

    private DroneModeCodeEnum modeCode;

    private Double totalFlightDistance;

    private Float totalFlightTime;

    private Float verticalSpeed;

    private WindDirectionEnum windDirection;

    private Float windSpeed;

    private DronePositionState positionState;

    @JsonProperty(PayloadModelConst.PAYLOAD_KEY)
    private List<DockDronePayload> payloads;

    private Storage storage;

    private SwitchActionEnum nightLightsState;

    private Integer heightLimit;

    private DockDistanceLimitStatus distanceLimitStatus;

    private ObstacleAvoidance obstacleAvoidance;

    private Long activationTime;

    private List<OsdCamera> cameras;

    private RcLostActionEnum rcLostAction;

    private Integer rthAltitude;

    private Integer totalFlightSorties;

    @CloudSDKVersion(deprecated = CloudSDKVersionEnum.V1_0_0)
    private ExitWaylineWhenRcLostEnum exitWaylineWhenRcLost;

    private String country;

    private Boolean ridState;

    @JsonProperty("is_near_area_limit")
    private Boolean nearAreaLimit;

    @JsonProperty("is_near_height_limit")
    private Boolean nearHeightLimit;

    private OsdDroneMaintainStatus maintainStatus;

    private String trackId;

    private String bestLinkGateway;

    private WirelessLinkTopo wirelessLinkTopo;

    /**
     * 4G Dongle信息
     */
    private List<DongleInfo> dongleInfos;

    /**
     * 固件一致性升级
     */
    private CompatibleStatusEnum compatibleStatus;

    /**
     * 飞行器控制模式
     */
    private CommanderFlightModeEnum commanderFlightMode;

    /**
     * 指点飞行高度	float	{"max":3000,"min":2,"step":0.1,"unit_name":"米 / m"}
     */
    private Float commanderFlightHeight;

    /**
     * 指点飞行失控动作
     */
    private CommanderModeLostActionEnum commanderModeLostAction;

    /**
     * 相机水印设置
     */
    private CameraWatermarkSettings cameraWatermarkSettings;

    /**
     * 飞行器进入当前状态的原因
     */
    private ModeCodeReasonEnum modeCodeReason;

    /**
     * 当前控制源
     */
    private String controlSource;

    /**
     * 低电量警告阈值
     */
    private Integer lowBatteryWarningThreshold;

    /**
     * 严重低电量警告阈值
     */
    private Integer seriousLowBatteryWarningThreshold;

    /**
     * 返航预留电量	int	{"max":100,"min":0}
     */
    private Integer remainingPowerForReturnHome;

    public OsdDockDrone() {
    }

    @Override
    public String toString() {
        return "OsdDockDrone{" +
                "attitudeHead=" + attitudeHead +
                ", attitudePitch=" + attitudePitch +
                ", attitudeRoll=" + attitudeRoll +
                ", elevation=" + elevation +
                ", battery=" + battery +
                ", firmwareVersion='" + firmwareVersion + '\'' +
                ", gear=" + gear +
                ", height=" + height +
                ", homeDistance=" + homeDistance +
                ", homeLatitude=" + homeLatitude +
                ", homeLongitude=" + homeLongitude +
                ", horizontalSpeed=" + horizontalSpeed +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", modeCode=" + modeCode +
                ", totalFlightDistance=" + totalFlightDistance +
                ", totalFlightTime=" + totalFlightTime +
                ", verticalSpeed=" + verticalSpeed +
                ", windDirection=" + windDirection +
                ", windSpeed=" + windSpeed +
                ", positionState=" + positionState +
                ", payloads=" + payloads +
                ", storage=" + storage +
                ", nightLightsState=" + nightLightsState +
                ", heightLimit=" + heightLimit +
                ", distanceLimitStatus=" + distanceLimitStatus +
                ", obstacleAvoidance=" + obstacleAvoidance +
                ", activationTime=" + activationTime +
                ", cameras=" + cameras +
                ", rcLostAction=" + rcLostAction +
                ", rthAltitude=" + rthAltitude +
                ", totalFlightSorties=" + totalFlightSorties +
                ", exitWaylineWhenRcLost=" + exitWaylineWhenRcLost +
                ", country='" + country + '\'' +
                ", ridState=" + ridState +
                ", nearAreaLimit=" + nearAreaLimit +
                ", nearHeightLimit=" + nearHeightLimit +
                ", maintainStatus=" + maintainStatus +
                ", trackId='" + trackId + '\'' +
                ", bestLinkGateway='" + bestLinkGateway + '\'' +
                ", wirelessLinkTopo=" + wirelessLinkTopo +
                ", dongleInfos=" + dongleInfos +
                ", compatibleStatus=" + compatibleStatus +
                ", commanderFlightMode=" + commanderFlightMode +
                ", commanderFlightHeight=" + commanderFlightHeight +
                ", commanderModeLostAction=" + commanderModeLostAction +
                ", cameraWatermarkSettings=" + cameraWatermarkSettings +
                ", modeCodeReason=" + modeCodeReason +
                ", controlSource='" + controlSource + '\'' +
                ", lowBatteryWarningThreshold=" + lowBatteryWarningThreshold +
                ", seriousLowBatteryWarningThreshold=" + seriousLowBatteryWarningThreshold +
                ", remainingPowerForReturnHome=" + remainingPowerForReturnHome +
                '}';
    }

    public Float getAttitudeHead() {
        return attitudeHead;
    }

    public OsdDockDrone setAttitudeHead(Float attitudeHead) {
        this.attitudeHead = attitudeHead;
        return this;
    }

    public Double getAttitudePitch() {
        return attitudePitch;
    }

    public OsdDockDrone setAttitudePitch(Double attitudePitch) {
        this.attitudePitch = attitudePitch;
        return this;
    }

    public Double getAttitudeRoll() {
        return attitudeRoll;
    }

    public OsdDockDrone setAttitudeRoll(Double attitudeRoll) {
        this.attitudeRoll = attitudeRoll;
        return this;
    }

    public Float getElevation() {
        return elevation;
    }

    public OsdDockDrone setElevation(Float elevation) {
        this.elevation = elevation;
        return this;
    }

    public DroneBattery getBattery() {
        return battery;
    }

    public OsdDockDrone setBattery(DroneBattery battery) {
        this.battery = battery;
        return this;
    }

    public String getFirmwareVersion() {
        return firmwareVersion;
    }

    public OsdDockDrone setFirmwareVersion(String firmwareVersion) {
        this.firmwareVersion = firmwareVersion;
        return this;
    }

    public GearEnum getGear() {
        return gear;
    }

    public OsdDockDrone setGear(GearEnum gear) {
        this.gear = gear;
        return this;
    }

    public Float getHeight() {
        return height;
    }

    public OsdDockDrone setHeight(Float height) {
        this.height = height;
        return this;
    }

    public Float getHomeDistance() {
        return homeDistance;
    }

    public OsdDockDrone setHomeDistance(Float homeDistance) {
        this.homeDistance = homeDistance;
        return this;
    }

    public Float getHorizontalSpeed() {
        return horizontalSpeed;
    }

    public OsdDockDrone setHorizontalSpeed(Float horizontalSpeed) {
        this.horizontalSpeed = horizontalSpeed;
        return this;
    }

    public Float getLatitude() {
        return latitude;
    }

    public OsdDockDrone setLatitude(Float latitude) {
        this.latitude = latitude;
        return this;
    }

    public Float getLongitude() {
        return longitude;
    }

    public OsdDockDrone setLongitude(Float longitude) {
        this.longitude = longitude;
        return this;
    }

    public DroneModeCodeEnum getModeCode() {
        return modeCode;
    }

    public OsdDockDrone setModeCode(DroneModeCodeEnum modeCode) {
        this.modeCode = modeCode;
        return this;
    }

    public Double getTotalFlightDistance() {
        return totalFlightDistance;
    }

    public OsdDockDrone setTotalFlightDistance(Double totalFlightDistance) {
        this.totalFlightDistance = totalFlightDistance;
        return this;
    }

    public Float getTotalFlightTime() {
        return totalFlightTime;
    }

    public OsdDockDrone setTotalFlightTime(Float totalFlightTime) {
        this.totalFlightTime = totalFlightTime;
        return this;
    }

    public Float getVerticalSpeed() {
        return verticalSpeed;
    }

    public OsdDockDrone setVerticalSpeed(Float verticalSpeed) {
        this.verticalSpeed = verticalSpeed;
        return this;
    }

    public WindDirectionEnum getWindDirection() {
        return windDirection;
    }

    public OsdDockDrone setWindDirection(WindDirectionEnum windDirection) {
        this.windDirection = windDirection;
        return this;
    }

    public Float getWindSpeed() {
        return windSpeed;
    }

    public OsdDockDrone setWindSpeed(Float windSpeed) {
        this.windSpeed = windSpeed;
        return this;
    }

    public DronePositionState getPositionState() {
        return positionState;
    }

    public OsdDockDrone setPositionState(DronePositionState positionState) {
        this.positionState = positionState;
        return this;
    }

    public List<DockDronePayload> getPayloads() {
        return payloads;
    }

    public OsdDockDrone setPayloads(List<DockDronePayload> payloads) {
        this.payloads = payloads;
        return this;
    }

    public Storage getStorage() {
        return storage;
    }

    public OsdDockDrone setStorage(Storage storage) {
        this.storage = storage;
        return this;
    }

    public SwitchActionEnum getNightLightsState() {
        return nightLightsState;
    }

    public OsdDockDrone setNightLightsState(SwitchActionEnum nightLightsState) {
        this.nightLightsState = nightLightsState;
        return this;
    }

    public Integer getHeightLimit() {
        return heightLimit;
    }

    public OsdDockDrone setHeightLimit(Integer heightLimit) {
        this.heightLimit = heightLimit;
        return this;
    }

    public DockDistanceLimitStatus getDistanceLimitStatus() {
        return distanceLimitStatus;
    }

    public OsdDockDrone setDistanceLimitStatus(DockDistanceLimitStatus distanceLimitStatus) {
        this.distanceLimitStatus = distanceLimitStatus;
        return this;
    }

    public ObstacleAvoidance getObstacleAvoidance() {
        return obstacleAvoidance;
    }

    public OsdDockDrone setObstacleAvoidance(ObstacleAvoidance obstacleAvoidance) {
        this.obstacleAvoidance = obstacleAvoidance;
        return this;
    }

    public Long getActivationTime() {
        return activationTime;
    }

    public OsdDockDrone setActivationTime(Long activationTime) {
        this.activationTime = activationTime;
        return this;
    }

    public List<OsdCamera> getCameras() {
        return cameras;
    }

    public OsdDockDrone setCameras(List<OsdCamera> cameras) {
        this.cameras = cameras;
        return this;
    }

    public RcLostActionEnum getRcLostAction() {
        return rcLostAction;
    }

    public OsdDockDrone setRcLostAction(RcLostActionEnum rcLostAction) {
        this.rcLostAction = rcLostAction;
        return this;
    }

    public Integer getRthAltitude() {
        return rthAltitude;
    }

    public OsdDockDrone setRthAltitude(Integer rthAltitude) {
        this.rthAltitude = rthAltitude;
        return this;
    }

    public Integer getTotalFlightSorties() {
        return totalFlightSorties;
    }

    public OsdDockDrone setTotalFlightSorties(Integer totalFlightSorties) {
        this.totalFlightSorties = totalFlightSorties;
        return this;
    }

    public ExitWaylineWhenRcLostEnum getExitWaylineWhenRcLost() {
        return exitWaylineWhenRcLost;
    }

    public OsdDockDrone setExitWaylineWhenRcLost(ExitWaylineWhenRcLostEnum exitWaylineWhenRcLost) {
        this.exitWaylineWhenRcLost = exitWaylineWhenRcLost;
        return this;
    }

    public String getCountry() {
        return country;
    }

    public OsdDockDrone setCountry(String country) {
        this.country = country;
        return this;
    }

    public Boolean getRidState() {
        return ridState;
    }

    public OsdDockDrone setRidState(Boolean ridState) {
        this.ridState = ridState;
        return this;
    }

    public Boolean getNearAreaLimit() {
        return nearAreaLimit;
    }

    public OsdDockDrone setNearAreaLimit(Boolean nearAreaLimit) {
        this.nearAreaLimit = nearAreaLimit;
        return this;
    }

    public Boolean getNearHeightLimit() {
        return nearHeightLimit;
    }

    public OsdDockDrone setNearHeightLimit(Boolean nearHeightLimit) {
        this.nearHeightLimit = nearHeightLimit;
        return this;
    }

    public OsdDroneMaintainStatus getMaintainStatus() {
        return maintainStatus;
    }

    public OsdDockDrone setMaintainStatus(OsdDroneMaintainStatus maintainStatus) {
        this.maintainStatus = maintainStatus;
        return this;
    }

    public String getTrackId() {
        return trackId;
    }

    public OsdDockDrone setTrackId(String trackId) {
        this.trackId = trackId;
        return this;
    }

    public String getBestLinkGateway() {
        return bestLinkGateway;
    }

    public OsdDockDrone setBestLinkGateway(String bestLinkGateway) {
        this.bestLinkGateway = bestLinkGateway;
        return this;
    }

    public WirelessLinkTopo getWirelessLinkTopo() {
        return wirelessLinkTopo;
    }

    public OsdDockDrone setWirelessLinkTopo(WirelessLinkTopo wirelessLinkTopo) {
        this.wirelessLinkTopo = wirelessLinkTopo;
        return this;
    }

    public Float getHomeLatitude() {
        return homeLatitude;
    }

    public OsdDockDrone setHomeLatitude(Float homeLatitude) {
        this.homeLatitude = homeLatitude;
        return this;
    }

    public Float getHomeLongitude() {
        return homeLongitude;
    }

    public OsdDockDrone setHomeLongitude(Float homeLongitude) {
        this.homeLongitude = homeLongitude;
        return this;
    }

    public List<DongleInfo> getDongleInfos() {
        return dongleInfos;
    }

    public OsdDockDrone setDongleInfos(List<DongleInfo> dongleInfos) {
        this.dongleInfos = dongleInfos;
        return this;
    }

    public CompatibleStatusEnum getCompatibleStatus() {
        return compatibleStatus;
    }

    public OsdDockDrone setCompatibleStatus(CompatibleStatusEnum compatibleStatus) {
        this.compatibleStatus = compatibleStatus;
        return this;
    }

    public CommanderFlightModeEnum getCommanderFlightMode() {
        return commanderFlightMode;
    }

    public OsdDockDrone setCommanderFlightMode(CommanderFlightModeEnum commanderFlightMode) {
        this.commanderFlightMode = commanderFlightMode;
        return this;
    }

    public Float getCommanderFlightHeight() {
        return commanderFlightHeight;
    }

    public OsdDockDrone setCommanderFlightHeight(Float commanderFlightHeight) {
        this.commanderFlightHeight = commanderFlightHeight;
        return this;
    }

    public CommanderModeLostActionEnum getCommanderModeLostAction() {
        return commanderModeLostAction;
    }

    public OsdDockDrone setCommanderModeLostAction(CommanderModeLostActionEnum commanderModeLostAction) {
        this.commanderModeLostAction = commanderModeLostAction;
        return this;
    }

    public CameraWatermarkSettings getCameraWatermarkSettings() {
        return cameraWatermarkSettings;
    }

    public OsdDockDrone setCameraWatermarkSettings(CameraWatermarkSettings cameraWatermarkSettings) {
        this.cameraWatermarkSettings = cameraWatermarkSettings;
        return this;
    }

    public ModeCodeReasonEnum getModeCodeReason() {
        return modeCodeReason;
    }

    public OsdDockDrone setModeCodeReason(ModeCodeReasonEnum modeCodeReason) {
        this.modeCodeReason = modeCodeReason;
        return this;
    }

    public String getControlSource() {
        return controlSource;
    }

    public OsdDockDrone setControlSource(String controlSource) {
        this.controlSource = controlSource;
        return this;
    }

    public Integer getLowBatteryWarningThreshold() {
        return lowBatteryWarningThreshold;
    }

    public OsdDockDrone setLowBatteryWarningThreshold(Integer lowBatteryWarningThreshold) {
        this.lowBatteryWarningThreshold = lowBatteryWarningThreshold;
        return this;
    }

    public Integer getSeriousLowBatteryWarningThreshold() {
        return seriousLowBatteryWarningThreshold;
    }

    public OsdDockDrone setSeriousLowBatteryWarningThreshold(Integer seriousLowBatteryWarningThreshold) {
        this.seriousLowBatteryWarningThreshold = seriousLowBatteryWarningThreshold;
        return this;
    }

    public Integer getRemainingPowerForReturnHome() {
        return remainingPowerForReturnHome;
    }

    public OsdDockDrone setRemainingPowerForReturnHome(Integer remainingPowerForReturnHome) {
        this.remainingPowerForReturnHome = remainingPowerForReturnHome;
        return this;
    }
}
