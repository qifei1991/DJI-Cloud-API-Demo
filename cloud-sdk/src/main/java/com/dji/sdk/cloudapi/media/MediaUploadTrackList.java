package com.dji.sdk.cloudapi.media;

import java.util.List;

/**
 * @author Qfei
 * @date 2026/1/26 14:27
 */
public class MediaUploadTrackList {

    private List<MediaUploadTrack> list;

    @Override
    public String toString() {
        return "MediaUploadTrackList{" +
                "list=" + list +
                '}';
    }

    public List<MediaUploadTrack> getList() {
        return list;
    }

    public MediaUploadTrackList setList(List<MediaUploadTrack> list) {
        this.list = list;
        return this;
    }
}
