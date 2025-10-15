package com.dji.sdk.cloudapi.device;

import com.dji.sdk.cloudapi.interconnection.Speaker;

import java.util.List;

/**
 * @author Qfei
 * @date 2025/8/4 14:32
 */
public class PsdkWidget {

    private Integer psdkIndex;

    private String psdkName;

    private PsdkTypeEnum psdkType;

    private String psdkSn;

    private String psdkVersion;

    private String psdkLibVersion;

    private Speaker speaker;

    private List<Object> values;

    public PsdkWidget() {
    }

    @Override
    public String toString() {
        return "PsdkWidget{" +
                "psdkIndex=" + psdkIndex +
                ", psdkName='" + psdkName + '\'' +
                ", psdkType=" + psdkType +
                ", psdkSn='" + psdkSn + '\'' +
                ", psdkVersion='" + psdkVersion + '\'' +
                ", psdkLibVersion='" + psdkLibVersion + '\'' +
                ", speaker=" + speaker +
                ", values=" + values +
                '}';
    }

    public Integer getPsdkIndex() {
        return psdkIndex;
    }

    public PsdkWidget setPsdkIndex(Integer psdkIndex) {
        this.psdkIndex = psdkIndex;
        return this;
    }

    public String getPsdkName() {
        return psdkName;
    }

    public PsdkWidget setPsdkName(String psdkName) {
        this.psdkName = psdkName;
        return this;
    }

    public PsdkTypeEnum getPsdkType() {
        return psdkType;
    }

    public PsdkWidget setPsdkType(PsdkTypeEnum psdkType) {
        this.psdkType = psdkType;
        return this;
    }

    public String getPsdkSn() {
        return psdkSn;
    }

    public PsdkWidget setPsdkSn(String psdkSn) {
        this.psdkSn = psdkSn;
        return this;
    }

    public String getPsdkVersion() {
        return psdkVersion;
    }

    public PsdkWidget setPsdkVersion(String psdkVersion) {
        this.psdkVersion = psdkVersion;
        return this;
    }

    public String getPsdkLibVersion() {
        return psdkLibVersion;
    }

    public PsdkWidget setPsdkLibVersion(String psdkLibVersion) {
        this.psdkLibVersion = psdkLibVersion;
        return this;
    }

    public Speaker getSpeaker() {
        return speaker;
    }

    public PsdkWidget setSpeaker(Speaker speaker) {
        this.speaker = speaker;
        return this;
    }

    public List<Object> getValues() {
        return values;
    }

    public PsdkWidget setValues(List<Object> values) {
        this.values = values;
        return this;
    }
}
