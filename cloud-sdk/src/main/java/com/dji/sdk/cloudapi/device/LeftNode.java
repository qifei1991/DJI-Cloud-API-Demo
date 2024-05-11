package com.dji.sdk.cloudapi.device;

/**
 * @author Qfei
 * @date 2024/5/10 18:06
 */
public class LeftNode {

    private Integer controlSourceIndex;

    private Integer sdrId;

    private String sn;

    private Boolean valid;

    @Override
    public String toString() {
        return "LeftNode{" +
                "controlSourceIndex=" + controlSourceIndex +
                ", sdrId=" + sdrId +
                ", sn='" + sn + '\'' +
                ", valid=" + valid +
                '}';
    }

    public Integer getControlSourceIndex() {
        return controlSourceIndex;
    }

    public LeftNode setControlSourceIndex(Integer controlSourceIndex) {
        this.controlSourceIndex = controlSourceIndex;
        return this;
    }

    public Integer getSdrId() {
        return sdrId;
    }

    public LeftNode setSdrId(Integer sdrId) {
        this.sdrId = sdrId;
        return this;
    }

    public String getSn() {
        return sn;
    }

    public LeftNode setSn(String sn) {
        this.sn = sn;
        return this;
    }

    public Boolean getValid() {
        return valid;
    }

    public LeftNode setValid(Boolean valid) {
        this.valid = valid;
        return this;
    }
}
