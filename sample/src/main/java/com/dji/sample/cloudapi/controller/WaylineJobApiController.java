package com.dji.sample.cloudapi.controller;

import com.dji.sample.common.model.CustomClaim;
import com.dji.sample.wayline.service.IFlightTaskService;
import com.dji.sdk.common.HttpResultResponse;
import com.dji.sample.wayline.model.dto.WaylineJobDTO;
import com.dji.sample.wayline.model.enums.WaylineTaskStatusEnum;
import com.dji.sample.wayline.model.param.CreateJobParam;
import com.dji.sample.wayline.model.param.UpdateJobParam;
import com.dji.sample.wayline.service.IWaylineJobService;
import com.dji.sdk.common.PaginationData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.sql.SQLException;
import java.util.List;

/**
 * The Controller of Flight-task control.
 *
 * @author Qfei
 * @date 2022/12/21 14:42
 */
@RestController
@RequestMapping("${url.cloud-api.prefix}${url.cloud-api.version}/wayline/workspaces")
public class WaylineJobApiController {

    @Autowired
    private IWaylineJobService waylineJobService;
    @Autowired
    private IFlightTaskService flightTaskService;

    /**
     * Paginate through all jobs in this workspace.
     * @param page
     * @param pageSize
     * @param workspaceId
     * @return
     */
    @GetMapping("/{workspace_id}/jobs")
    public HttpResultResponse<PaginationData<WaylineJobDTO>> getJobs(@RequestParam(defaultValue = "1") Long page,
            @RequestParam(name = "page_size", defaultValue = "10") Long pageSize,
            @PathVariable(name = "workspace_id") String workspaceId,
            @RequestParam(name = "dock_sn", required = false) String dockSn,
            @RequestParam(name = "name", required = false) String name,
            @RequestParam(name = "task_type", required = false) Integer taskType,
            @RequestParam(name = "status", required = false) List<Integer> status,
            @RequestParam(name = "begin_time", required = false) Long beginTime,
            @RequestParam(name = "end_time", required = false) Long endTime,
            @RequestParam(name = "order_field", required = false) String orderField,
            @RequestParam(name = "is_asc", required = false) String isAsc) {
        PaginationData<WaylineJobDTO> data = waylineJobService.getJobsByWorkspaceId(workspaceId, page, pageSize,
                dockSn, name, taskType, status, beginTime, endTime, orderField, isAsc);
        return HttpResultResponse.success(data);
    }

    /**
     * Create a wayline task for the Dock.
     * @param param
     * @param workspaceId
     * @return
     */
    @PostMapping("/{workspace_id}/jobs")
    public HttpResultResponse publishCreateJob(@Valid @RequestBody CreateJobParam param,
            @PathVariable(name = "workspace_id") String workspaceId) throws SQLException {
        CustomClaim customClaim = new CustomClaim();
        customClaim.setWorkspaceId(workspaceId);
        customClaim.setUsername(param.getUsername());
        return flightTaskService.publishFlightTask(param, customClaim);
    }

    /**
     * Send the command to cancel the jobs.
     * @param jobIds
     * @param workspaceId
     * @return
     */
    @DeleteMapping("/{workspace_id}/jobs")
    public HttpResultResponse publishCancelJob(@RequestParam(name = "job_id") List<String> jobIds,
            @PathVariable(name = "workspace_id") String workspaceId) {
        flightTaskService.cancelFlightTask(workspaceId, jobIds);
        return HttpResultResponse.success();
    }

    /**
     * Set the media files for this job to upload immediately.
     * @param workspaceId workspace id
     * @param jobId flight id
     * @return HttpResultResponse
     */
    @PostMapping("/{workspace_id}/jobs/{job_id}/media-highest")
    public HttpResultResponse uploadMediaHighestPriority(@PathVariable(name = "workspace_id") String workspaceId,
            @PathVariable(name = "job_id") String jobId) {
        flightTaskService.uploadMediaHighestPriority(workspaceId, jobId);
        return HttpResultResponse.success();
    }

    @GetMapping("/{workspace_id}/jobs/remaining")
    public HttpResultResponse<List<WaylineJobDTO>> getRemainingJobs(@PathVariable(name = "workspace_id") String workspaceId) {
        return HttpResultResponse.success(this.waylineJobService.getRemainingJobs(workspaceId));
    }

    @GetMapping("/{workspace_id}/jobs/executing/{dock_sn}")
    public HttpResultResponse<WaylineJobDTO> getDockExecutingJob(@PathVariable("workspace_id") String workspaceId,
            @PathVariable("dock_sn") String dockSn) {
        return HttpResultResponse.success(this.waylineJobService.getDockExecutingJob(workspaceId, dockSn).orElse(null));
    }

    @PutMapping("/{workspace_id}/jobs/{job_id}")
    public HttpResultResponse updateJobStatus(@PathVariable(name = "workspace_id") String workspaceId,
            @PathVariable(name = "job_id") String jobId,
            @Valid @RequestBody UpdateJobParam param) throws SQLException {

        if (param.getStatus() == WaylineTaskStatusEnum.BREAK_POINT_CONTINUE) {
            return flightTaskService.breakPointContinueFlight(workspaceId, jobId);
        }
        flightTaskService.updateJobStatus(workspaceId, jobId, param);
        return HttpResultResponse.success();
    }
}
