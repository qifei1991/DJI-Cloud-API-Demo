package com.dji.sdk.cloudapi.interconnection;

/**
 * @author Qfei
 * @date 2024/4/23 18:00
 */
public class PlayAudioFile {

    private String name;

    private String url;

    private String md5;

    private PlayAudioFormatEnum format;

    @Override
    public String toString() {
        return "PlayAudioFile{" +
                "name='" + name + '\'' +
                ", url='" + url + '\'' +
                ", md5='" + md5 + '\'' +
                ", format=" + format +
                '}';
    }

    public String getName() {
        return name;
    }

    public PlayAudioFile setName(String name) {
        this.name = name;
        return this;
    }

    public String getUrl() {
        return url;
    }

    public PlayAudioFile setUrl(String url) {
        this.url = url;
        return this;
    }

    public String getMd5() {
        return md5;
    }

    public PlayAudioFile setMd5(String md5) {
        this.md5 = md5;
        return this;
    }

    public PlayAudioFormatEnum getFormat() {
        return format;
    }

    public PlayAudioFile setFormat(PlayAudioFormatEnum format) {
        this.format = format;
        return this;
    }
}
