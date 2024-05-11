package com.dji.sdk.cloudapi.device;

import com.dji.sdk.common.BaseModel;

/**
 * @author Qfei
 * @date 2024/5/10 18:37
 */
public class WirelessLinkTopoInfo extends BaseModel {

    private WirelessLinkTopo wirelessLinkTopo;

    public WirelessLinkTopo getWirelessLinkTopo() {
        return wirelessLinkTopo;
    }

    public WirelessLinkTopoInfo setWirelessLinkTopo(WirelessLinkTopo wirelessLinkTopo) {
        this.wirelessLinkTopo = wirelessLinkTopo;
        return this;
    }

    @Override
    public String toString() {
        return "WirelessLinkTopoInfo{" +
                "wirelessLinkTopo=" + wirelessLinkTopo +
                '}';
    }
}
