package com.dji.sdk.cloudapi.media;

/**
 * 媒体文件上传飞行任务信息
 *
 * @author Qfei
 * @date 2025/5/8 14:02
 */
public class FileUploadCallbackFlightTask {

    private Integer uploadedFileCount;

    private Integer expectedFileCount;

    private FlightTypeEnum flightType;

    public FileUploadCallbackFlightTask() {
    }

    @Override
    public String toString() {
        return "FileUploadCallbackFlightTask{" +
                "uploadedFileCount=" + uploadedFileCount +
                ", expectedFileCount=" + expectedFileCount +
                ", flightType=" + flightType +
                '}';
    }

    public Integer getUploadedFileCount() {
        return uploadedFileCount;
    }

    public FileUploadCallbackFlightTask setUploadedFileCount(Integer uploadedFileCount) {
        this.uploadedFileCount = uploadedFileCount;
        return this;
    }

    public Integer getExpectedFileCount() {
        return expectedFileCount;
    }

    public FileUploadCallbackFlightTask setExpectedFileCount(Integer expectedFileCount) {
        this.expectedFileCount = expectedFileCount;
        return this;
    }

    public FlightTypeEnum getFlightType() {
        return flightType;
    }

    public FileUploadCallbackFlightTask setFlightType(FlightTypeEnum flightType) {
        this.flightType = flightType;
        return this;
    }
}
