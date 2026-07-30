package com.dji.sdk.cloudapi.wayline;

import com.dji.sdk.cloudapi.device.WirelessLinkTopo;

import java.util.List;

/**
 * 蛙跳任务执行参数
 *
 * @author Qfei
 * @date 2026/7/22 18:17
 */
public class FlightTaskMultiDockTask {

    /**
     * 图传连接拓扑
     */
    private WirelessLinkTopo wirelessLinkTopo;

    /**
     * 机场信息 array
     */
    private List<FlightTaskDockInfo> dockInfos;

    public FlightTaskMultiDockTask() {
    }

    @Override
    public String toString() {
        return "FlightTaskMultiDockTask{" +
                "wirelessLinkTopo=" + wirelessLinkTopo +
                ", dockInfos=" + dockInfos +
                '}';
    }

    public WirelessLinkTopo getWirelessLinkTopo() {
        return wirelessLinkTopo;
    }

    public FlightTaskMultiDockTask setWirelessLinkTopo(WirelessLinkTopo wirelessLinkTopo) {
        this.wirelessLinkTopo = wirelessLinkTopo;
        return this;
    }

    public List<FlightTaskDockInfo> getDockInfos() {
        return dockInfos;
    }

    public FlightTaskMultiDockTask setDockInfos(List<FlightTaskDockInfo> dockInfos) {
        this.dockInfos = dockInfos;
        return this;
    }
}
