package com.dji.sdk.cloudapi.device;

import javax.validation.constraints.Size;
import java.util.List;

/**
 * wireless_link_topo	图传连接拓扑	struct
 *
 * @author Qfei
 * @date 2024/5/10 18:02
 */
public class WirelessLinkTopo {

    /**
     * 飞行器对频信息 struct 从飞行器的设备属性中获取
     */
    private CenterNode centerNode;

    /**
     * 机场或遥控器对频信息	array	{"size": -, "item_type": struct}
     */
    private List<LeafNode> leafNodes;

    /**
     * 加密编码	array	{"size": 28, "item_type": int}	从飞行器的设备属性中获取
     */
    @Size(min = 28, max = 28)
    private List<Integer> secretCode;

    @Override
    public String toString() {
        return "WirelessLinkTopo{" +
                "centerNode=" + centerNode +
                ", leafNodes=" + leafNodes +
                ", secretCode=" + secretCode +
                '}';
    }

    public CenterNode getCenterNode() {
        return centerNode;
    }

    public WirelessLinkTopo setCenterNode(CenterNode centerNode) {
        this.centerNode = centerNode;
        return this;
    }

    public List<LeafNode> getLeafNodes() {
        return leafNodes;
    }

    public WirelessLinkTopo setLeafNodes(List<LeafNode> leafNodes) {
        this.leafNodes = leafNodes;
        return this;
    }

    public List<Integer> getSecretCode() {
        return secretCode;
    }

    public WirelessLinkTopo setSecretCode(List<Integer> secretCode) {
        this.secretCode = secretCode;
        return this;
    }
}
