package com.dji.sdk.cloudapi.device;

/**
 * <pre>
 *   »»center_node	飞行器对频信息	struct		从飞行器的设备属性中获取
 *    »»»sdr_id	扰码信息	int	{"max":"","min":"","step":"","unit_name":null}
 *    »»»sn	设备sn	text	{"length":""}
 * </pre>
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
