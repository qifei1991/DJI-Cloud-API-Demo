package com.dji.sample.cloudapi.controller;

import com.dji.sample.common.model.PaginationData;
import com.dji.sample.common.model.ResponseResult;
import com.dji.sample.wayline.model.dto.WaylineFileDTO;
import com.dji.sample.wayline.model.dto.WaylineFileUploadDTO;
import com.dji.sample.wayline.model.param.WaylineQueryParam;
import com.dji.sample.wayline.service.IWaylineFileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Objects;

/**
 * The Controller of Wayline managerment.
 *
 * @author Qfei
 * @date 2022/12/21 14:45
 */
@RestController
@RequestMapping("${url.cloud-api.prefix}${url.cloud-api.version}/wayline/workspaces")
public class WaylineController {

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
    public ResponseResult<PaginationData<WaylineFileDTO>> getWaylinesPagination(
            @RequestParam(name = "order_by") String orderBy, @RequestParam(required = false) boolean favorited,
            @RequestParam(required = false, defaultValue = "1") Integer page,
            @RequestParam(name = "page_size", defaultValue = "10") Integer pageSize,
            @RequestParam(name = "template_type", required = false) Integer[] templateType,
            @PathVariable(name = "workspace_id") String workspaceId) {
        WaylineQueryParam param = WaylineQueryParam.builder()
                .favorited(favorited)
                .page(page)
                .pageSize(pageSize)
                .orderBy(orderBy)
                .templateType(templateType)
                .build();
        PaginationData<WaylineFileDTO> data = waylineFileService.getWaylinesByParam(workspaceId, param);
        return ResponseResult.success(data);
    }

    /**
     * Import kmz wayline files.
     *
     * @param file 航线文件 kmz
     */
    @PostMapping("/{workspace_id}/waylines/file/upload")
    public ResponseResult importKmzFile(MultipartFile file,
            @PathVariable(name = "workspace_id") String workspaceId,
            @RequestParam(name = "creator", defaultValue = "manager-server") String creator) {

        if (Objects.isNull(file)) {
            return ResponseResult.error("No file received.");
        }
        this.waylineFileService.importKmzFile(file, workspaceId, creator);
        return ResponseResult.success();
    }

    @PostMapping("/{workspace_id}/upload-callback")
    public ResponseResult uploadCallBack(@RequestBody WaylineFileUploadDTO uploadFile,
            @PathVariable(name = "workspace_id") String workspaceId) {

        WaylineFileDTO metadata = uploadFile.getMetadata();
        metadata.setUsername("manager-service");
        metadata.setObjectKey(uploadFile.getObjectKey());
        metadata.setName(uploadFile.getName());

        int id = waylineFileService.saveWaylineFile(workspaceId, metadata);
        return id <= 0 ? ResponseResult.error() : ResponseResult.success();
    }

    /**
     * Favorite the wayline file according to the wayline file id.
     * @param workspaceId
     * @param ids   wayline file id
     * @return
     */
    @PostMapping("/{workspace_id}/favorites")
    public ResponseResult markFavorite(@PathVariable(name = "workspace_id") String workspaceId,
            @RequestParam(name = "id") List<String> ids) {
        boolean isMark = waylineFileService.markFavorite(workspaceId, ids, true);
        return isMark ? ResponseResult.success(true) : ResponseResult.error("Failed to mark favorite wayline.");
    }

    /**
     * Delete the favorites of this wayline file based on the wayline file id.
     * @param workspaceId
     * @param ids wayline file id
     * @return
     */
    @DeleteMapping("/{workspace_id}/favorites")
    public ResponseResult unmarkFavorite(@PathVariable(name = "workspace_id") String workspaceId,
            @RequestParam(name = "id") List<String> ids) {
        boolean isMark = waylineFileService.markFavorite(workspaceId, ids, false);
        return isMark ? ResponseResult.success(true) : ResponseResult.error("Failed to unmark favorite wayline.");
    }

    /**
     * Checking whether the name already exists according to the wayline name must ensure the uniqueness of the wayline name.
     * This interface will be called when uploading waylines and must be available.
     * @param workspaceId
     * @param names
     * @return
     */
    @GetMapping("/{workspace_id}/waylines/duplicate-names")
    public ResponseResult checkDuplicateNames(@PathVariable(name = "workspace_id") String workspaceId,
            @RequestParam(name = "name") List<String> names) {
        List<String> existNamesList = waylineFileService.getDuplicateNames(workspaceId, names);
        return ResponseResult.success(existNamesList);
    }

    /**
     * Delete the wayline file in the workspace according to the wayline id.
     * @param workspaceId
     * @param waylineId
     * @return
     */
    @DeleteMapping("/{workspace_id}/waylines/{wayline_id}")
    public ResponseResult deleteWayline(@PathVariable(name = "workspace_id") String workspaceId,
            @PathVariable(name = "wayline_id") String waylineId) {
        boolean isDel = waylineFileService.deleteByWaylineId(workspaceId, waylineId);
        return isDel ? ResponseResult.success(true) : ResponseResult.error("Failed to delete wayline.");
    }
}
