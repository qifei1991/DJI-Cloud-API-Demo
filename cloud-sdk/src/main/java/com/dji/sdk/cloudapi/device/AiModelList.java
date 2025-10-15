package com.dji.sdk.cloudapi.device;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * @author Qfei
 * @date 2025/9/3 16:03
 */
public class AiModelList {

    @JsonProperty("ai_model_list")
    private List<Object> aiModelList;
}
