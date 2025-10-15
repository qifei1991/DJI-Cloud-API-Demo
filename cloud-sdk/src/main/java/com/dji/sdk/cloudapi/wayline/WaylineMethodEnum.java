package com.dji.sdk.cloudapi.wayline;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * @author sean
 * @version 1.3
 * @date 2022/11/14
 */
public enum WaylineMethodEnum {

    FLIGHTTASK_CREATE("flighttask_create"),

    FLIGHTTASK_PREPARE("flighttask_prepare"),

    FLIGHTTASK_EXECUTE("flighttask_execute"),

    FLIGHTTASK_UNDO("flighttask_undo"),

    FLIGHTTASK_PAUSE("flighttask_pause"),

    FLIGHTTASK_RECOVERY("flighttask_recovery"),

    RETURN_HOME("return_home"),

    RETURN_HOME_CANCEL("return_home_cancel"),

    // 空中航线
    IN_FLIGHT_WAYLINE_DELIVER("in_flight_wayline_deliver"),

    IN_FLIGHT_WAYLINE_STOP("in_flight_wayline_stop"),

    IN_FLIGHT_WAYLINE_RECOVER("in_flight_wayline_recover"),

    IN_FLIGHT_WAYLINE_CANCEL("in_flight_wayline_cancel")
    ;

    private final String method;

    WaylineMethodEnum(String method) {
        this.method = method;
    }

    @JsonValue
    public String getMethod() {
        return this.method;
    }

}
