package com.dji.sample.cloudapi.service;

import com.dji.sample.cloudapi.model.param.UserParam;
import com.dji.sample.common.model.CustomClaim;
import com.dji.sample.common.model.ResponseResult;
import com.dji.sample.common.util.JwtUtil;
import com.dji.sample.component.mqtt.config.MqttConfiguration;
import com.dji.sample.manage.model.dto.UserDTO;
import com.dji.sample.manage.model.dto.WorkspaceDTO;
import com.dji.sample.manage.service.IWorkspaceService;
import lombok.extern.slf4j.Slf4j;
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

    @Value("${workspace.default-id}")
    String defaultWorkspaceId;

    @Autowired
    private MqttConfiguration mqttConfiguration;

    @Autowired
    private IWorkspaceService workspaceService;

    public ResponseResult getToken(UserParam userParam) {

        Optional<WorkspaceDTO> workspaceOpt = workspaceService.getWorkspaceByWorkspaceId(defaultWorkspaceId);
        if (workspaceOpt.isEmpty()) {
            return ResponseResult.builder()
                    .code(HttpStatus.UNAUTHORIZED.value())
                    .message("invalid workspace id")
                    .build();
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
                .mqttUsername(mqttConfiguration.getUsername())
                .mqttPassword(mqttConfiguration.getPassword())
                .mqttAddr(new StringBuilder().append(mqttConfiguration.getProtocol().trim())
                        .append("://")
                        .append(mqttConfiguration.getHost().trim())
                        .append(":")
                        .append(mqttConfiguration.getPort())
                        .toString())
                .accessToken(token)
                .workspaceId(workspaceOpt.get().getWorkspaceId())
                .build();
        return ResponseResult.success(userDTO);
    }
}
