package com.dji.sdk.mqtt.state;

import com.dji.sdk.cloudapi.device.*;
import com.dji.sdk.cloudapi.livestream.RcLivestreamAbilityUpdate;
import com.dji.sdk.cloudapi.property.DockDroneCommanderFlightHeight;
import com.dji.sdk.cloudapi.property.DockDroneCommanderModeLostAction;
import com.dji.sdk.cloudapi.property.DockDroneRthMode;
import com.dji.sdk.exception.CloudSDKException;

import java.util.Arrays;
import java.util.Collections;
import java.util.Set;

/**
 *
 * @author sean.zhou
 * @date 2021/11/18
 * @version 0.1
 */
public enum RcStateDataKeyEnum {

    FIRMWARE_VERSION(Set.of("firmware_version"), FirmwareVersion.class),

    LIVE_CAPACITY(Set.of("live_capacity"), RcLivestreamAbilityUpdate.class),

    CONTROL_SOURCE(Set.of("control_source"), RcDroneControlSource.class),

    LIVE_STATUS(Set.of("live_status"), RcLiveStatus.class),

    WPMZ_VERSION(Set.of("wpmz_version"), DockDroneWpmzVersion.class),

    PAYLOAD_FIRMWARE(PayloadModelConst.getAllModelWithPosition(), PayloadFirmwareVersion.class),

    RTH_MODE(Set.of("rth_mode"), DockDroneRthMode.class),

    CURRENT_RTH_MODE(Set.of("current_rth_mode"), DockDroneCurrentRthMode.class),

    COMMANDER_MODE_LOST_ACTION(Set.of("commander_mode_lost_action"), DockDroneCommanderModeLostAction.class),

    CURRENT_COMMANDER_FLIGHT_MODE(Set.of("current_commander_flight_mode", "commander_flight_mode"), DockDroneCurrentCommanderFlightMode.class),

    COMMANDER_FLIGHT_HEIGHT(Set.of("commander_flight_height"), DockDroneCommanderFlightHeight.class),

    DONGLE_INFOS(Set.of("dongle_infos"), DongleInfos.class),

    PSDK_UI_RESOURCE(Set.of("psdk_ui_resource"), PsdkUiResource.class),

    PSDK_WIDGET_VALUES(Set.of("psdk_widget_values"), PsdkWidgetValues.class),

    CAPABILITY_SET(Set.of("capability_set"), RcCapabilitySet.class),

    IS_CLOUD_CONTROL_AUTH(Set.of("is_cloud_control_auth"), RcIsCloudControlAuth.class)
    ;

    private final Set<String> keys;

    private final Class classType;


    RcStateDataKeyEnum(Set<String> keys, Class classType) {
        this.keys = keys;
        this.classType = classType;
    }

    public Class getClassType() {
        return classType;
    }

    public Set<String> getKeys() {
        return keys;
    }

    public static RcStateDataKeyEnum find(Set<String> keys) {
        return Arrays.stream(values()).filter(keyEnum -> !Collections.disjoint(keys, keyEnum.keys)).findAny()
                .orElseThrow(() -> new CloudSDKException(RcStateDataKeyEnum.class, keys));
    }

}
