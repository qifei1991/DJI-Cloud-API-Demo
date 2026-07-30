package com.dji.sdk.cloudapi.wayline;

import com.dji.sdk.cloudapi.HomePositionIsValidEnum;
import com.dji.sdk.cloudapi.device.AlternateLandPoint;
import com.dji.sdk.cloudapi.device.DockTypeEnum;
import com.dji.sdk.cloudapi.device.Rtcm;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 蛙跳任务机场信息
 * <pre>
 * »dock_infos	机场信息	array {"size": 2, "item_type": struct}	执行本次蛙跳任务的机场信息，除了dock_type需要指定外，其他字段均可从设备属性上报中直接获取
 *   »»dock_type 机场在蛙跳任务中的角色 enum_string	{"landing":"降落","takeoff":"起飞机场"}
 *   »»latitude	纬度	double	{"max":"90","min":"-90","step":"0.01"}	机场纬度，可从机场设备属性中获取
 *   »»longitude 经度	double	{"max":"180","min":"-180","step":"0.01"}	机场经度，可从机场设备属性中获取
 *   »»height	椭球高度	double	{"unit_name":"米 / m"}	机场高度，可从机场设备属性中获取
 *   »»heading	机场朝向角	double	{"max":"180","min":"-180","step":"","unit_name":"度 / °"}	机场朝向角，可从机场设备属性中获取
 *   »»home_position_is_valid	机场home点有效性	enum_int	{"0":"无效","1":"有效"}	可从机场设备属性中获取
 *   »»index	机场任务唯一性标识	int	{"max":"31","min":"1"}	给机场分配的序号，单次任务中保持唯一且最好长期固定使用
 *   »»sn	机场SN	text	{"length":""}
 *   »»rtcm_info	机场RTK标定源	struct
 *    »»»mount_point	网络RTK挂载点信息	text	{"length":""}
 *    »»»port	网络端口信息	text	{"length":""}
 *    »»»host	网络host信息	text	{"length":""}
 *    »»»rtcm_device_type	设备类型	enum_int	{"1":"机场"}
 *    »»»source_type	标定类型	enum_int	{"0":"未标定","1":"自收敛标定","2":"手动标定","3":"网络RTK标定"}
 *   »»alternate_land_point	备降点	struct
 *    »»»longitude	经度	float	{}
 *    »»»latitude	纬度	float	{}
 *    »»»safe_land_height	安全高度(备降转移高)	float	{}
 *    »»»is_configured	是否设置备降点	enum_int	{"0":"未设置","1":"已设置"}
 * </pre>
 * @author Qfei
 * @date 2026/7/22 18:29
 */
public class FlightTaskDockInfo {

    @NotNull
    private DockTypeEnum dockType;

    @Min(-90)
    @Max(90)
    private Float latitude;

    @Min(-180)
    @Max(180)
    private Float longitude;

    @Min(0)
    @Max(10000)
    private Float height;

    @Min(-180)
    @Max(180)
    private Float heading;

    @Min(0)
    @Max(1)
    private HomePositionIsValidEnum homePositionIsValid;

    @Min(0)
    @Max(31)
    private Integer index;

    @NotBlank
    private String sn;

    private Rtcm rtcmInfo;

    private AlternateLandPoint alternateLandPoint;

    public FlightTaskDockInfo() {
    }

    @Override
    public String toString() {
        return "FlightTaskDockInfo{" +
                "dockType=" + dockType +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", height=" + height +
                ", heading=" + heading +
                ", homePositionIsValid=" + homePositionIsValid +
                ", index=" + index +
                ", sn='" + sn + '\'' +
                ", rtcmInfo=" + rtcmInfo +
                ", alternateLandPoint=" + alternateLandPoint +
                '}';
    }

    public DockTypeEnum getDockType() {
        return dockType;
    }

    public FlightTaskDockInfo setDockType(DockTypeEnum dockType) {
        this.dockType = dockType;
        return this;
    }

    public Float getLatitude() {
        return latitude;
    }

    public FlightTaskDockInfo setLatitude(Float latitude) {
        this.latitude = latitude;
        return this;
    }

    public Float getLongitude() {
        return longitude;
    }

    public FlightTaskDockInfo setLongitude(Float longitude) {
        this.longitude = longitude;
        return this;
    }

    public Float getHeight() {
        return height;
    }

    public FlightTaskDockInfo setHeight(Float height) {
        this.height = height;
        return this;
    }

    public Float getHeading() {
        return heading;
    }

    public FlightTaskDockInfo setHeading(Float heading) {
        this.heading = heading;
        return this;
    }

    public HomePositionIsValidEnum getHomePositionIsValid() {
        return homePositionIsValid;
    }

    public FlightTaskDockInfo setHomePositionIsValid(HomePositionIsValidEnum homePositionIsValid) {
        this.homePositionIsValid = homePositionIsValid;
        return this;
    }

    public Integer getIndex() {
        return index;
    }

    public FlightTaskDockInfo setIndex(Integer index) {
        this.index = index;
        return this;
    }

    public String getSn() {
        return sn;
    }

    public FlightTaskDockInfo setSn(String sn) {
        this.sn = sn;
        return this;
    }

    public Rtcm getRtcmInfo() {
        return rtcmInfo;
    }

    public FlightTaskDockInfo setRtcmInfo(Rtcm rtcmInfo) {
        this.rtcmInfo = rtcmInfo;
        return this;
    }

    public AlternateLandPoint getAlternateLandPoint() {
        return alternateLandPoint;
    }

    public FlightTaskDockInfo setAlternateLandPoint(AlternateLandPoint alternateLandPoint) {
        this.alternateLandPoint = alternateLandPoint;
        return this;
    }
}
