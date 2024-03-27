package com.dji.sample.cloudapi.service;

import com.dji.sample.cloudapi.model.param.UserParam;
import com.dji.sample.common.model.CustomClaim;
import com.dji.sample.component.mqtt.config.MqttPropertyConfiguration;
import com.dji.sdk.common.HttpResultResponse;
import com.dji.sample.common.util.JwtUtil;
import com.dji.sample.manage.model.dto.UserDTO;
import com.dji.sample.manage.model.dto.WorkspaceDTO;
import com.dji.sample.manage.service.IWorkspaceService;
import com.dji.sdk.mqtt.MqttConfiguration;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * @author Qfei
 * @date 2022/12/22 14:23
 */
@Slf4j
@Service
public class TokenService {

    @Value("${aircraft-manager-server.workspace-id}")
    String defaultWorkspaceId;

    @Autowired
    private MqttPropertyConfiguration mqttPropertyConfiguration;

    @Autowired
    private IWorkspaceService workspaceService;

    public HttpResultResponse getToken(UserParam userParam) {

        Optional<WorkspaceDTO> workspaceOpt = workspaceService.getWorkspaceByWorkspaceId(defaultWorkspaceId);
        if (workspaceOpt.isEmpty()) {
            return HttpResultResponse.error(HttpStatus.UNAUTHORIZED.value(), "invalid workspace id");
        }

        CustomClaim customClaim = new CustomClaim();
        customClaim.setUsername(userParam.getUsername());
        customClaim.setUserType(userParam.getUserType());
        customClaim.setWorkspaceId(workspaceOpt.get().getWorkspaceId());

        // create token
        String token = JwtUtil.createToken(customClaim.convertToMap());
        UserDTO userDTO = UserDTO.builder()
                .username(userParam.getUsername())
                .userType(userParam.getUserType())
                .mqttAddr(MqttPropertyConfiguration.getBasicMqttAddress())
                .accessToken(token)
                .workspaceId(workspaceOpt.get().getWorkspaceId())
                .build();
        return HttpResultResponse.success(userDTO);
    }
}
