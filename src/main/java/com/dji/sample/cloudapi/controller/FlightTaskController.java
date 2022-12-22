package com.dji.sample.cloudapi.controller;

import com.dji.sample.common.model.CustomClaim;
import com.dji.sample.common.model.ResponseResult;
import com.dji.sample.wayline.model.param.CreateJobParam;
import com.dji.sample.wayline.service.IWaylineJobService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
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
@RequestMapping("${url.cloud-api.prefix}${url.cloud-api.version}/flight-tasks/workspaces")
public class FlightTaskController {

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
    @PostMapping("/{workspace_id}/jobs")
    public ResponseResult publishCreateJob(HttpServletRequest request, @Valid @RequestBody CreateJobParam param,
            @PathVariable(name = "workspace_id") String workspaceId) throws SQLException {
        CustomClaim customClaim = new CustomClaim();
        customClaim.setWorkspaceId(workspaceId);
        return waylineJobService.publishFlightTask(param, customClaim);
    }

    /**
     * Send the command to cancel the jobs.
     * @param jobIds
     * @param workspaceId
     * @return
     */
    @DeleteMapping("/{workspace_id}/jobs")
    public ResponseResult publishCancelJob(@RequestParam(name = "job_id") List<String> jobIds,
            @PathVariable(name = "workspace_id") String workspaceId) {
        waylineJobService.cancelFlightTask(workspaceId, jobIds);
        return ResponseResult.success();
    }

    /**
     * Set the media files for this job to upload immediately.
     * @param workspaceId workspace id
     * @param jobId flight id
     * @return ResponseResult
     */
    @PostMapping("/{workspace_id}/jobs/{job_id}/media-highest")
    public ResponseResult uploadMediaHighestPriority(@PathVariable(name = "workspace_id") String workspaceId,
            @PathVariable(name = "job_id") String jobId) {
        waylineJobService.uploadMediaHighestPriority(workspaceId, jobId);
        return ResponseResult.success();
    }
}
