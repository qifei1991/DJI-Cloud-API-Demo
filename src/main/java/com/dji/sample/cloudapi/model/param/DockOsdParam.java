package com.dji.sample.cloudapi.model.param;

import com.dji.sample.manage.model.enums.DockModeCodeEnum;
import lombok.Builder;
import lombok.Data;

/**
 * 机场状态信息对象
 *
 * @author Qfei
 * @date 2022/11/23 13:59
 */
@Data
@Builder
public class DockOsdParam {
    /**
     * 飞行记录ID（架次ID）
     */
    private String sortiesId;
    private String sn;
    private String firmwareVersion;
    private Double longitude;
    private Double latitude;
    /**
     * 机场状态，{"0":"空闲中","1":"现场调试","2":"远程调试","3":"固件升级中","4":"作业中"}
     */
    private DockModeCodeEnum modelCode;
    /**
     * 舱盖状态，{"0":"关闭","1":"打开","2":"半开","3":"舱盖状态异常"}
     */
    private Integer coverState;
    /**
     * 推杆状态，{"0":"关闭","1":"打开","2":"半开","3":"推杆状态异常"}
     */
    private Integer putterState;
    /**
     * 补光灯状态，{"0":"关闭","1":"打开"}
     */
    private Integer supplementLightState;
    /**
     * 网络类型，{"1":"4G","2":"以太网"}
     */
    private Integer networkType;
    /**
     * 网络质量，{"0":"差","1":"中","2":"好"}
     */
    private Integer networkQuality;
    /**
     * 网络速率kb/s
     */
    private Float networkRate;
    /**
     * 飞机是否在舱
     */
    private Integer droneInDock;
    /**
     * 机场激活时间戳
     */
    private Long activationTime;
    /**
     * 电池存储（保养）模式
     */
    private Integer batteryStoreMode;
    /**
     * 机场声光报警状态，{"0":"声光报警关闭","1":"声光报警开启"}
     */
    private Integer alarmState;
    /**
     * 飞机电量百分比
     */
    private Integer droneBatteryPercent;
    /**
     * 飞机充电状态，{"0":"空闲","1":"充电中"}
     */
    private Integer droneBatteryState;
    /**
     * 飞行器电池保养状态，{"0":"无需保养","1":"待保养","2":"正在保养"}
     */
    private Integer droneBatteryMaintenanceState;
    /**
     * 飞行器电池保养剩余时间，单位：h
     */
    private Long droneBatteryMaintenanceTimeLeft;
    /**
     * 备用电池开关，{"0":"备用电池关闭","1":"备用电池开启"}
     */
    private Integer backupBatterySwitch;
    /**
     * 备用电池电压，{"unit":"毫伏","min":"0","max":"30000","step":"1","desc":"备用电池关闭时电压为0"}
     */
    private Integer backupBatteryVoltage;
    /**
     * 紧急停止按钮状态，{"0":"关闭","1":"开启"}
     */
    private Integer emergencyStopState;

    private Long time;

    /**
     * 降雨量, {"0":"无雨","1":"小雨","2":"中雨","3":"大雨"}
     */
    private Integer rainfall;
    /**
     * 风速, {"min":"-1.4E-45","max":"3.4028235E38","unit":"m/s","unitName":"米每秒","step":"0.1"}
     */
    private Float windSpeed;
    /**
     * 环境温度, {"min":"-1.4E-45","max":"3.4028235E38","unit":"°C","unitName":"摄氏度","step":"0.1"}
     */
    private Float environmentTemperature;
    /**
     * 舱内温度, {"min":"-1.4E-45","max":"3.4028235E38","unit":"°C","unitName":"摄氏度","step":"0.1"}
     */
    private Float temperature;
    /**
     * 舱内湿度, {"min":"0","max":"100","unit":"%RH","unitName":"相对湿度","step":"0.1"}
     */
    private Integer humidity;
    /**
     * 机场累计作业次数
     */
    private Integer jobNumber;
    /**
     * 存储
     */
    private Long storageTotal;
    private Long storageUsed;
    /**
     * {"0":"未连接","1":"连接中","2":"已连接"}
     */
    private Integer drcState;
    /**
     * 媒体文件，待上传数量
     */
    private Integer remainUpload;
    /**
     * 市电电压, {"min":"-2147483648","max":"2147483647","unit":"V","unitName":"伏特","step":"1"}
     */
    private Integer electricSupplyVoltage;
    /**
     * 工作电压, {"min":"-2147483648","max":"2147483647","unit":"mV","unitName":"毫伏","step":"1"}
     */
    private Integer workingVoltage;
    /**
     * 工作电流, {"min":"-1.4E-45","max":"3.4028235E38","unit":"mA","unitName":"毫安","step":"0.1"}
     */
    private Integer workingCurrent;

    /**
     * 机场空调状态, 机场空调工作状态信息，一个时刻仅存在一种工作模式，不能同时存在多个模式;
     * <pre>
     * {
     *  "0":"空闲模式（无制冷、制热、除湿等）","1":"制冷模式","2":"制热模式","3":"除湿模式","4":"制冷退出模式",
     *  "5":"制热退出模式","6":"除湿退出模式","7":"制冷准备模式","8":"制热准备模式","9":"除湿准备模式"
     *  }
     *  </pre>
     */
    private Integer airConditionerMode;
    /**
     * 剩余需要等待的可切换时间, {"unit":"秒"};
     * <br/>
     * 模式切换顺序为准备模式、工作（制冷、制热、除湿）模式、退出模式、空闲模式。本属性将上报，可以切换下一个模式指令还需要等待的时间。
     * 例如当前机场空调在进入制冷模式后，必须制冷5分钟后才可以退出制冷模式，本属性将上报还需要多久退出制冷模式，为0表示随时可退出
     */
//    private Integer switchTime;
}
