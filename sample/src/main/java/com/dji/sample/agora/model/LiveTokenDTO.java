package com.dji.sample.agora.model;

/**
 * @author Qfei
 * @date 2024/5/23 16:59
 */
public class LiveTokenDTO {
    private String appId;
    private String channelName;
    private String uid;
    private String token;

    @Override
    public String toString() {
        return "LiveTokenDTO{" +
                "appId='" + appId + '\'' +
                ", channelName='" + channelName + '\'' +
                ", uid='" + uid + '\'' +
                ", token='" + token + '\'' +
                '}';
    }

    public String getAppId() {
        return appId;
    }

    public LiveTokenDTO setAppId(String appId) {
        this.appId = appId;
        return this;
    }

    public String getChannelName() {
        return channelName;
    }

    public LiveTokenDTO setChannelName(String channelName) {
        this.channelName = channelName;
        return this;
    }

    public String getUid() {
        return uid;
    }

    public LiveTokenDTO setUid(String uid) {
        this.uid = uid;
        return this;
    }

    public String getToken() {
        return token;
    }

    public LiveTokenDTO setToken(String token) {
        this.token = token;
        return this;
    }
}
