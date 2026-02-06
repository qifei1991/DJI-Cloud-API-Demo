package com.dji.sdk.cloudapi.media;

/**
 * @author Qfei
 * @date 2026/1/26 14:28
 */
public class MediaUploadTrack {

    private String sn;

    private Long timestamp;

    private String type;

    private MediaUploadTrackProps properties;

    @Override
    public String toString() {
        return "MediaUploadTrack{" +
                "sn='" + sn + '\'' +
                ", timestamp=" + timestamp +
                ", type='" + type + '\'' +
                ", properties=" + properties +
                '}';
    }

    public String getSn() {
        return sn;
    }

    public MediaUploadTrack setSn(String sn) {
        this.sn = sn;
        return this;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public MediaUploadTrack setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
        return this;
    }

    public String getType() {
        return type;
    }

    public MediaUploadTrack setType(String type) {
        this.type = type;
        return this;
    }

    public MediaUploadTrackProps getProperties() {
        return properties;
    }

    public MediaUploadTrack setProperties(MediaUploadTrackProps properties) {
        this.properties = properties;
        return this;
    }
}
