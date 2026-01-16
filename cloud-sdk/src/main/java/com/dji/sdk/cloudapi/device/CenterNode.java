package com.dji.sdk.cloudapi.device;

/**
 * @author Qfei
 * @date 2024/5/10 18:03
 */
public class CenterNode {

    private Long sdrId;

    private String sn;

    @Override
    public String toString() {
        return "CenterNode{" +
                "sdrId=" + sdrId +
                ", sn='" + sn + '\'' +
                '}';
    }

    public Long getSdrId() {
        return sdrId;
    }

    public CenterNode setSdrId(Long sdrId) {
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
