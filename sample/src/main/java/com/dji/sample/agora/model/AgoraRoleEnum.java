package com.dji.sample.agora.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Arrays;

/**
 * @author Qfei
 * @date 2024/5/23 15:16
 */
public enum AgoraRoleEnum {

    ROLE_PUBLISHER(1),

    ROLE_SUBSCRIBER(2),

    ;

    public final int initValue;

    AgoraRoleEnum(int initValue) {
        this.initValue = initValue;
    }

    @JsonValue
    public int getInitValue() {
        return initValue;
    }

    @JsonCreator
    public AgoraRoleEnum find(int initValue) {
        return Arrays.stream(AgoraRoleEnum.values())
                .filter(x -> x.initValue == initValue)
                .findAny()
                .orElse(ROLE_SUBSCRIBER);
    }

}
