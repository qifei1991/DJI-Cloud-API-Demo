package com.dji.sdk.cloudapi.media;

/**
 * @author Qfei
 * @date 2026/1/26 14:30
 */
public class MediaUploadTrackProps {

    private String flightId;

    private Long mediaStartTime;

    private Long mediaEndTime;

    /**
     * The name of the media in storage.
     * eg: /ef5c24bc-0ed4-4913-905f-3bf0ef5ab392/DJI_202601261150_005_ef5c24bc-0ed4-4913-905f-3bf0ef5ab392/DJI_20260126120345_0002_D.RTK
     */
    private String name;

    private Long size;

    @Override
    public String toString() {
        return "MediaUploadTrackProps{" +
                "flightId='" + flightId + '\'' +
                ", mediaStartTime=" + mediaStartTime +
                ", mediaEndTime=" + mediaEndTime +
                ", name='" + name + '\'' +
                ", size=" + size +
                '}';
    }

    public String getFlightId() {
        return flightId;
    }

    public MediaUploadTrackProps setFlightId(String flightId) {
        this.flightId = flightId;
        return this;
    }

    public Long getMediaStartTime() {
        return mediaStartTime;
    }

    public MediaUploadTrackProps setMediaStartTime(Long mediaStartTime) {
        this.mediaStartTime = mediaStartTime;
        return this;
    }

    public Long getMediaEndTime() {
        return mediaEndTime;
    }

    public MediaUploadTrackProps setMediaEndTime(Long mediaEndTime) {
        this.mediaEndTime = mediaEndTime;
        return this;
    }

    public String getName() {
        return name;
    }

    public MediaUploadTrackProps setName(String name) {
        this.name = name;
        return this;
    }

    public Long getSize() {
        return size;
    }

    public MediaUploadTrackProps setSize(Long size) {
        this.size = size;
        return this;
    }
}
