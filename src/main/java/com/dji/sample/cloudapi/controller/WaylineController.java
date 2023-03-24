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

import java.util.Objects;

/**
 * The Controller of Wayline managerment.
 *
 * @author Qfei
 * @date 2022/12/21 14:45
 */
@RestController
@RequestMapping("${url.cloud-api.prefix}${url.cloud-api.version}/wayline")
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
            @RequestParam(name = "order_by") String orderBy,
            @RequestParam(required = false) boolean favorited, @RequestParam(required = false, defaultValue = "1") Integer page,
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
        PaginationData<WaylineFileDTO> data = waylineFileService.getWaylinesByParam(defaultWorkspaceId, param);
        return ResponseResult.success(data);
    }

    /**
     * Import kmz wayline files.
     * @param file 航线文件 kmz
     */
    @PostMapping("/file/upload")
    public ResponseResult importKmzFile(MultipartFile file) {

        if (Objects.isNull(file)) {
            return ResponseResult.error("No file received.");
        }
        this.waylineFileService.importKmzFile(file, defaultWorkspaceId, "manager-service");
        return ResponseResult.success();
    }

    @PostMapping("/upload-callback")
    public ResponseResult uploadCallBack(@RequestBody WaylineFileUploadDTO uploadFile) {

        WaylineFileDTO metadata = uploadFile.getMetadata();
        metadata.setUsername("manager-service");
        metadata.setObjectKey(uploadFile.getObjectKey());
        metadata.setName(uploadFile.getName());

        int id = waylineFileService.saveWaylineFile(defaultWorkspaceId, metadata);
        return id <= 0 ? ResponseResult.error() : ResponseResult.success();
    }
}
