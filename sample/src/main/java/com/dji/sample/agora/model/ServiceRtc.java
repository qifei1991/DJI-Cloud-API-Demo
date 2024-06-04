package com.dji.sample.agora.model;

/**
 * @author Qfei
 * @date 2024/5/23 15:13
 */
public class ServiceRtc {

    private Boolean enable = false;

    private AgoraRoleEnum role = AgoraRoleEnum.ROLE_SUBSCRIBER;

    @Override
    public String toString() {
        return "ServiceRtc{" +
                "enable=" + enable +
                ", role=" + role +
                '}';
    }

    public Boolean getEnable() {
        return enable;
    }

    public ServiceRtc setEnable(Boolean enable) {
        this.enable = enable;
        return this;
    }

    public AgoraRoleEnum getRole() {
        return role;
    }

    public ServiceRtc setRole(AgoraRoleEnum role) {
        this.role = role;
        return this;
    }
}
