package com.dji.sdk.cloudapi.device;

import java.util.List;

/**
 * @author Qfei
 * @date 2024/5/10 18:02
 */
public class WirelessLinkTopo {

    private CenterNode centerNode;

    private List<LeftNode> leftNodes;

    private List<Integer> secretCode;

    @Override
    public String toString() {
        return "WirelessLinkTopo{" +
                "centerNode=" + centerNode +
                ", leftNodes=" + leftNodes +
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

    public List<LeftNode> getLeftNodes() {
        return leftNodes;
    }

    public WirelessLinkTopo setLeftNodes(List<LeftNode> leftNodes) {
        this.leftNodes = leftNodes;
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
