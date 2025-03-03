package com.dji.sample.cloudapi.client;

import com.dji.sample.cloudapi.util.ClientUri;
import com.dji.sample.manage.model.dto.WorkspaceDTO;
import com.dji.sample.manage.service.IWorkspaceService;
import com.dji.sample.wayline.model.dto.WaylineFileDTO;
import com.dji.sdk.cloudapi.device.DeviceEnum;
import com.dji.sdk.cloudapi.wayline.GetWaylineListResponse;
import com.dji.sdk.cloudapi.wayline.WaylineTypeEnum;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 航线文件客户端
 *
 * @author Qfei
 * @date 2023/7/7 15:54
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class WaylineFileClient extends AbstractClient {

    private final IWorkspaceService workspaceService;

    @Async("asyncThreadPool")
    public void reportWaylineImport(Optional<GetWaylineListResponse> waylineOpt, String workspaceId) {
        Optional<WorkspaceDTO> workspace = workspaceService.getWorkspaceByWorkspaceId(workspaceId);
        log.debug("Report Upload wayline file, workspace: {}, wayline: {}", workspace, waylineOpt);
        waylineOpt.ifPresent(x ->
                this.applicationJsonPost(ClientUri.URI_WAYLINE_REPORT,
                        WaylineFileDTO.builder()
                                .waylineId(x.getId())
                                .name(x.getName())
                                .objectKey(x.getObjectKey())
                                .sign(x.getSign())
                                .payloadModelKeys(x.getPayloadModelKeys().stream().map(DeviceEnum::getDevice).collect(Collectors.toList()))
                                .droneModelKey(x.getDroneModelKey().getDevice())
                                .templateTypes(x.getTemplateTypes().stream().map(WaylineTypeEnum::getValue).collect(Collectors.toList()))
                                .username(x.getUsername())
                                .favorited(x.getFavorited())
                                .createTime(x.getCreateTime())
                                .updateTime(x.getUpdateTime())
                                .bindCode(workspace.map(WorkspaceDTO::getBindCode).orElse(null))
                                .build()));
    }
}
