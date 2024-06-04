package com.dji.sample.agora.model;

import com.dji.sample.agora.AgoraProperties;

import javax.validation.constraints.NotBlank;

/**
 * @author Qfei
 * @date 2024/5/23 15:12
 */
public class GenerateTokenDTO {

    @NotBlank(message = "频道名称不能为空")
    private String channelName;

    @NotBlank(message = "用户uid不能为空")
    private String uid;

    private Integer tokenExpireTs = AgoraProperties.tokenExpireTime;

    private Integer privilegeExpireTs = AgoraProperties.privilegeExpireTime;

    private ServiceRtc serviceRtc;

    @Override
    public String toString() {
        return "GenerateTokenDTO{" +
                "channelName='" + channelName + '\'' +
                ", uid='" + uid + '\'' +
                ", tokenExpireTs=" + tokenExpireTs +
                ", privilegeExpireTs=" + privilegeExpireTs +
                ", serviceRtc=" + serviceRtc +
                '}';
    }

    public @NotBlank(message = "频道名称不能为空") String getChannelName() {
        return channelName;
    }

    public GenerateTokenDTO setChannelName(@NotBlank(message = "频道名称不能为空") String channelName) {
        this.channelName = channelName;
        return this;
    }

    public @NotBlank(message = "用户uid不能为空") String getUid() {
        return uid;
    }

    public GenerateTokenDTO setUid(@NotBlank(message = "用户uid不能为空") String uid) {
        this.uid = uid;
        return this;
    }

    public Integer getTokenExpireTs() {
        return tokenExpireTs;
    }

    public GenerateTokenDTO setTokenExpireTs(Integer tokenExpireTs) {
        this.tokenExpireTs = tokenExpireTs;
        return this;
    }

    public Integer getPrivilegeExpireTs() {
        return privilegeExpireTs;
    }

    public GenerateTokenDTO setPrivilegeExpireTs(Integer privilegeExpireTs) {
        this.privilegeExpireTs = privilegeExpireTs;
        return this;
    }

    public ServiceRtc getServiceRtc() {
        return serviceRtc;
    }

    public GenerateTokenDTO setServiceRtc(ServiceRtc serviceRtc) {
        this.serviceRtc = serviceRtc;
        return this;
    }
}
