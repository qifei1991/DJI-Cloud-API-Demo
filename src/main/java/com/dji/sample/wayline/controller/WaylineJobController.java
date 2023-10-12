package com.dji.sample.wayline.controller;

import com.dji.sample.common.model.CustomClaim;
import com.dji.sample.common.model.PaginationData;
import com.dji.sample.common.model.ResponseResult;
import com.dji.sample.wayline.model.dto.WaylineJobDTO;
import com.dji.sample.wayline.model.enums.WaylineTaskStatusEnum;
import com.dji.sample.wayline.model.param.CreateJobParam;
import com.dji.sample.wayline.model.param.UpdateJobParam;
import com.dji.sample.wayline.service.IWaylineJobService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.sql.SQLException;
import java.util.List;
import java.util.Set;

import static com.dji.sample.component.AuthInterceptor.TOKEN_CLAIM;

/**
 * @author sean
 * @version 1.1
 * @date 2022/6/1
 */
@RequestMapping("${url.wayline.prefix}${url.wayline.version}/workspaces")
@RestController
public class WaylineJobController {

    @Autowired
    private IWaylineJobService waylineJobService;

    /**
     * Create a wayline task for the Dock.
     * @param request
     * @param param
     * @param workspaceId
     * @return
     * @throws SQLException
     */
    @PostMapping("/{workspace_id}/flight-tasks")
    public ResponseResult createJob(HttpServletRequest request, @Valid @RequestBody CreateJobParam param,
                                    @PathVariable(name = "workspace_id") String workspaceId) throws SQLException {
        CustomClaim customClaim = (CustomClaim)request.getAttribute(TOKEN_CLAIM);
        customClaim.setWorkspaceId(workspaceId);

        return waylineJobService.publishFlightTask(param, customClaim);
    }

    /**
     * Paginate through all jobs in this workspace.
     * @param page
     * @param pageSize
     * @param workspaceId
     * @return
     */
    @GetMapping("/{workspace_id}/jobs")
    public ResponseResult<PaginationData<WaylineJobDTO>> getJobs(@RequestParam(defaultValue = "1") Long page,
            @RequestParam(name = "page_size", defaultValue = "10") Long pageSize,
            @PathVariable(name = "workspace_id") String workspaceId,
            @RequestParam(name = "dock_sn", required = false) String dockSn,
            @RequestParam(name = "name", required = false) String name,
            @RequestParam(name = "task_type", required = false) Integer taskType,
            @RequestParam(name = "status", required = false) List<Integer> status,
            @RequestParam(name = "begin_time", required = false) Long beginTime,
            @RequestParam(name = "end_time", required = false) Long endTime) {
        PaginationData<WaylineJobDTO> data = waylineJobService.getJobsByWorkspaceId(workspaceId, page, pageSize,
                dockSn, name, taskType, status, beginTime, endTime);
        return ResponseResult.success(data);
    }

    /**
     * Send the command to cancel the jobs.
     * @param jobIds
     * @param workspaceId
     * @return
     */
    @DeleteMapping("/{workspace_id}/jobs")
    public ResponseResult publishCancelJob(@RequestParam(name = "job_id") Set<String> jobIds,
                                     @PathVariable(name = "workspace_id") String workspaceId) {
        waylineJobService.cancelFlightTask(workspaceId, jobIds);
        return ResponseResult.success();
    }

    /**
     * Set the media files for this job to upload immediately.
     * @param workspaceId
     * @param jobId
     * @return
     */
    @PostMapping("/{workspace_id}/jobs/{job_id}/media-highest")
    public ResponseResult uploadMediaHighestPriority(@PathVariable(name = "workspace_id") String workspaceId,
                                             @PathVariable(name = "job_id") String jobId) {
        waylineJobService.uploadMediaHighestPriority(workspaceId, jobId);
        return ResponseResult.success();
    }

    @PutMapping("/{workspace_id}/jobs/{job_id}")
    public ResponseResult updateJobStatus(@PathVariable(name = "workspace_id") String workspaceId,
                                          @PathVariable(name = "job_id") String jobId,
                                          @Valid @RequestBody UpdateJobParam param) {

        if (param.getStatus() == WaylineTaskStatusEnum.BREAK_POINT_CONTINUE) {
            return waylineJobService.breakPointContinueFlight(workspaceId, jobId);
        }
        waylineJobService.updateJobStatus(workspaceId, jobId, param);
        return ResponseResult.success();
    }
}
