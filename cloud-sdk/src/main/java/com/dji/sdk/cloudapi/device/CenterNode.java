package com.dji.sdk.cloudapi.device;

/**
 * @author Qfei
 * @date 2024/5/10 18:03
 */
public class CenterNode {

    private Integer sdrId;

    private String sn;

    @Override
    public String toString() {
        return "CenterNode{" +
                "sdrId=" + sdrId +
                ", sn='" + sn + '\'' +
                '}';
    }

    public Integer getSdrId() {
        return sdrId;
    }

    public CenterNode setSdrId(Integer sdrId) {
        this.sdrId = sdrId;
        return this;
    }

    public String getSn() {
        return sn;
    }

    public CenterNode setSn(String sn) {
        this.sn = sn;
        return this;
    }
}
