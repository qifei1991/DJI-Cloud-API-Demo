package com.dji.sdk.cloudapi.device;

import java.util.List;

/**
 * AiBoxes
 *
 * @author Qfei
 * @date 2026/1/14 18:45
 */
public class AiBoxList {

    private List<Object> aiBoxes;

    @Override
    public String toString() {
        return "AiBoxList{" +
                "aiBoxes=" + aiBoxes +
                '}';
    }

    public List<Object> getAiBoxes() {
        return aiBoxes;
    }

    public AiBoxList setAiBoxes(List<Object> aiBoxes) {
        this.aiBoxes = aiBoxes;
        return this;
    }
}
