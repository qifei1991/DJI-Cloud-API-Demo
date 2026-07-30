package com.dji.sdk.cloudapi.device;

/**
 * <pre>
 *   »»leaf_nodes	机场或遥控器对频信息	array	{"size": -, "item_type": struct}
 *    »»»sdr_id	扰码信息	int	{"max":"","min":"","step":"","unit_name":null}
 *    »»»sn	设备sn	text	{"length":""}
 *    »»»control_source_index	控制源序号	int	{"max":"2","min":"1","step":"1","unit_name":"无 / "}
 * </pre>
 * @author Qfei
 * @date 2024/5/10 18:06
 */
public class LeafNode {

    private Integer controlSourceIndex;

    private Long sdrId;

    private String sn;

    private Boolean valid;

    @Override
    public String toString() {
        return "LeafNode{" +
                "controlSourceIndex=" + controlSourceIndex +
                ", sdrId=" + sdrId +
                ", sn='" + sn + '\'' +
                ", valid=" + valid +
                '}';
    }

    public Integer getControlSourceIndex() {
        return controlSourceIndex;
    }

    public LeafNode setControlSourceIndex(Integer controlSourceIndex) {
        this.controlSourceIndex = controlSourceIndex;
        return this;
    }

    public Long getSdrId() {
        return sdrId;
    }

    public LeafNode setSdrId(Long sdrId) {
        this.sdrId = sdrId;
        return this;
    }

    public String getSn() {
        return sn;
    }

    public LeafNode setSn(String sn) {
        this.sn = sn;
        return this;
    }

    public Boolean getValid() {
        return valid;
    }

    public LeafNode setValid(Boolean valid) {
        this.valid = valid;
        return this;
    }
}
