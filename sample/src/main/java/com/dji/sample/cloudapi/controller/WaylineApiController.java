package com.dji.sample.cloudapi.controller;

import com.dji.sdk.cloudapi.device.DeviceEnum;
import com.dji.sdk.cloudapi.wayline.*;
import com.dji.sdk.common.HttpResultResponse;
import com.dji.sample.wayline.model.dto.WaylineFileDTO;
import com.dji.sample.wayline.service.IWaylineFileService;
import com.dji.sdk.common.PaginationData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * The Controller of Wayline managerment.
 *
 * @author Qfei
 * @date 2022/12/21 14:45
 */
@RestController
@RequestMapping("${url.cloud-api.prefix}${url.cloud-api.version}/wayline/workspaces")
public class WaylineApiController {

    @Value("${aircraft-manager-server.workspace-id}")
    String defaultWorkspaceId;
    @Autowired
    private IWaylineFileService waylineFileService;

    /**
     * Query the basic data of the wayline file according to the query conditions.
     * The query condition field in pilot is fixed.
     * @param orderBy   Sorted fields. Spliced at the end of the sql statement.
     * @param favorited Whether the wayline file is favorited or not.
     * @param page 索引
     * @param pageSize 每页多少条
     * @param templateType 模板类型
     */
    @GetMapping("/{workspace_id}/waylines")
    public HttpResultResponse<PaginationData<GetWaylineListResponse>> getWaylinesPagination(@Valid GetWaylineListRequest request,
            @PathVariable(name = "workspace_id") String workspaceId) {
        PaginationData<GetWaylineListResponse> data = waylineFileService.getWaylinesByParam(workspaceId, request);
        return HttpResultResponse.success(data);
    }

    /**
     * Import kmz wayline files.
     *
     * @param file 航线文件 kmz
     */
    @PostMapping("/{workspace_id}/waylines/file/upload")
    public HttpResultResponse importKmzFile(MultipartFile file,
            @PathVariable(name = "workspace_id") String workspaceId,
            @RequestParam(name = "creator", defaultValue = "manager-server") String creator) {

        if (Objects.isNull(file)) {
            return HttpResultResponse.error("No file received.");
        }
        this.waylineFileService.importKmzFile(file, workspaceId, creator);
        return HttpResultResponse.success();
    }

    @PostMapping("/{workspace_id}/upload-callback")
    public HttpResultResponse uploadCallBack(@RequestBody WaylineUploadCallbackRequest uploadCallbackRequest,
            @PathVariable(name = "workspace_id") String workspaceId) {

        WaylineUploadCallbackMetadata metadata = uploadCallbackRequest.getMetadata();

        WaylineFileDTO file = WaylineFileDTO.builder()
                .username("manager-service")
                .objectKey(uploadCallbackRequest.getObjectKey())
                .name(uploadCallbackRequest.getName())
                .templateTypes(metadata.getTemplateTypes().stream().map(WaylineTypeEnum::getValue).collect(Collectors.toList()))
                .payloadModelKeys(metadata.getPayloadModelKeys().stream().map(DeviceEnum::getDevice).collect(Collectors.toList()))
                .droneModelKey(metadata.getDroneModelKey().getDevice())
                .build();

        int id = waylineFileService.saveWaylineFile(workspaceId, file);
        return id <= 0 ? HttpResultResponse.error() : HttpResultResponse.success();
    }

    /**
     * Favorite the wayline file according to the wayline file id.
     * @param workspaceId
     * @param ids   wayline file id
     * @return
     */
    @PostMapping("/{workspace_id}/favorites")
    public HttpResultResponse markFavorite(@PathVariable(name = "workspace_id") String workspaceId,
            @RequestParam(name = "id") List<String> ids) {
        boolean isMark = waylineFileService.markFavorite(workspaceId, ids, true);
        return isMark ? HttpResultResponse.success(true) : HttpResultResponse.error("航线收藏失败.");
    }

    /**
     * Delete the favorites of this wayline file based on the wayline file id.
     * @param workspaceId
     * @param ids wayline file id
     * @return
     */
    @DeleteMapping("/{workspace_id}/favorites")
    public HttpResultResponse unmarkFavorite(@PathVariable(name = "workspace_id") String workspaceId,
            @RequestParam(name = "id") List<String> ids) {
        boolean isMark = waylineFileService.markFavorite(workspaceId, ids, false);
        return isMark ? HttpResultResponse.success(true) : HttpResultResponse.error("航线取消收藏失败.");
    }

    /**
     * Checking whether the name already exists according to the wayline name must ensure the uniqueness of the wayline name.
     * This interface will be called when uploading waylines and must be available.
     * @param workspaceId
     * @param names
     * @return
     */
    @GetMapping("/{workspace_id}/waylines/duplicate-names")
    public HttpResultResponse checkDuplicateNames(@PathVariable(name = "workspace_id") String workspaceId,
            @RequestParam(name = "name") List<String> names) {
        List<String> existNamesList = waylineFileService.getDuplicateNames(workspaceId, names);
        return HttpResultResponse.success(existNamesList);
    }

    /**
     * Delete the wayline file in the workspace according to the wayline id.
     * @param workspaceId
     * @param waylineId
     * @return
     */
    @DeleteMapping("/{workspace_id}/waylines/{wayline_id}")
    public HttpResultResponse deleteWayline(@PathVariable(name = "workspace_id") String workspaceId,
            @PathVariable(name = "wayline_id") String waylineId) {
        boolean isDel = waylineFileService.deleteByWaylineId(workspaceId, waylineId);
        return isDel ? HttpResultResponse.success(true) : HttpResultResponse.error("航线删除失败.");
    }

    @PutMapping("/{workspace_id}/waylines/{wayline_id}")
    public HttpResultResponse editWayline(@PathVariable(name = "workspace_id") String workspaceId,
            @PathVariable(name = "wayline_id") String waylineId, @RequestBody WaylineFileDTO file) {
        return HttpResultResponse.success(this.waylineFileService.updateWaylineFile(workspaceId, waylineId, file));
    }
}
