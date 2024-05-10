package com.dji.sdk.cloudapi.interconnection;

/**
 * @author Qfei
 * @date 2024/4/23 18:10
 */
public class PlayTtsFile {

    private String name;

    private String text;

    private String md5;

    @Override
    public String toString() {
        return "PlayTtsFile{" +
                "name='" + name + '\'' +
                ", text='" + text + '\'' +
                ", md5='" + md5 + '\'' +
                '}';
    }

    public String getName() {
        return name;
    }

    public PlayTtsFile setName(String name) {
        this.name = name;
        return this;
    }

    public String getText() {
        return text;
    }

    public PlayTtsFile setText(String text) {
        this.text = text;
        return this;
    }

    public String getMd5() {
        return md5;
    }

    public PlayTtsFile setMd5(String md5) {
        this.md5 = md5;
        return this;
    }
}
