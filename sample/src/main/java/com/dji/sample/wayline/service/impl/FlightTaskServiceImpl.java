package com.dji.sample.wayline.service.impl;

import com.dji.sample.cloudapi.client.FlightTaskClient;
import com.dji.sample.common.error.CommonErrorEnum;
import com.dji.sample.common.model.CustomClaim;
import com.dji.sample.component.mqtt.model.EventsReceiver;
import com.dji.sample.component.redis.RedisConst;
import com.dji.sample.component.redis.RedisOpsUtils;
import com.dji.sample.component.websocket.service.IWebSocketMessageService;
import com.dji.sample.manage.model.dto.DeviceDTO;
import com.dji.sample.manage.service.IDeviceRedisService;
import com.dji.sample.media.model.MediaFileCountDTO;
import com.dji.sample.media.service.IMediaRedisService;
import com.dji.sample.wayline.model.dto.ConditionalWaylineJobKey;
import com.dji.sample.wayline.model.dto.WaylineJobDTO;
import com.dji.sample.wayline.model.dto.WaylineTaskConditionDTO;
import com.dji.sample.wayline.model.enums.WaylineErrorCodeEnum;
import com.dji.sample.wayline.model.enums.WaylineJobStatusEnum;
import com.dji.sample.wayline.model.param.CreateJobParam;
import com.dji.sample.wayline.model.param.UpdateJobParam;
import com.dji.sample.wayline.service.IFlightTaskService;
import com.dji.sample.wayline.service.IWaylineFileService;
import com.dji.sample.wayline.service.IWaylineJobService;
import com.dji.sample.wayline.service.IWaylineRedisService;
import com.dji.sdk.cloudapi.device.ExitWaylineWhenRcLostEnum;
import com.dji.sdk.cloudapi.media.UploadFlighttaskMediaPrioritize;
import com.dji.sdk.cloudapi.media.api.AbstractMediaService;
import com.dji.sdk.cloudapi.wayline.*;
import com.dji.sdk.cloudapi.wayline.api.AbstractWaylineService;
import com.dji.sdk.common.HttpResultResponse;
import com.dji.sdk.common.SDKManager;
import com.dji.sdk.mqtt.MqttReply;
import com.dji.sdk.mqtt.events.TopicEventsRequest;
import com.dji.sdk.mqtt.events.TopicEventsResponse;
import com.dji.sdk.mqtt.services.ServicesReplyData;
import com.dji.sdk.mqtt.services.TopicServicesResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.messaging.MessageHeaders;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.net.URL;
import java.sql.SQLException;
import java.time.*;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author sean
 * @version 1.1
 * @date 2022/6/9
 */
@Service
@Slf4j
public class FlightTaskServiceImpl extends AbstractWaylineService implements IFlightTaskService {

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private IWebSocketMessageService websocketMessageService;

    @Autowired
    private IWaylineJobService waylineJobService;

    @Autowired
    private IDeviceRedisService deviceRedisService;

    @Autowired
    private IWaylineRedisService waylineRedisService;

    @Autowired
    private IMediaRedisService mediaRedisService;

    @Autowired
    private IWaylineFileService waylineFileService;

    @Autowired
    private SDKWaylineService abstractWaylineService;

    @Autowired
    @Qualifier("mediaServiceImpl")
    private AbstractMediaService abstractMediaService;

    @Autowired
    private FlightTaskClient flightTaskClient;

    @Scheduled(initialDelay = 10, fixedRate = 5, timeUnit = TimeUnit.SECONDS)
    public void checkScheduledJob() {
        Object jobIdValue = RedisOpsUtils.zGetMin(RedisConst.WAYLINE_JOB_TIMED_EXECUTE);
        if (Objects.isNull(jobIdValue)) {
            return;
        }
        log.info("Check the timed tasks of the wayline. {}", jobIdValue);
        // format: {workspace_id}:{dock_sn}:{job_id}
        String[] jobArr = String.valueOf(jobIdValue).split(RedisConst.DELIMITER);
        double time = RedisOpsUtils.zScore(RedisConst.WAYLINE_JOB_TIMED_EXECUTE, jobIdValue);
        long now = System.currentTimeMillis();
        int offset = 30_000;

        // Expired tasks are deleted directly.
        if (time < now - offset) {
            RedisOpsUtils.zRemove(RedisConst.WAYLINE_JOB_TIMED_EXECUTE, jobIdValue);
            waylineJobService.updateJob(WaylineJobDTO.builder()
                    .jobId(jobArr[2])
                    .status(WaylineJobStatusEnum.FAILED.getVal())
                    .executeTime(LocalDateTime.now())
                    .completedTime(LocalDateTime.now())
                    .code(HttpStatus.SC_REQUEST_TIMEOUT).build());
            return;
        }

        if (now <= time && time <= now + offset) {
            try {
                this.executeFlightTask(jobArr[0], jobArr[2]);
            } catch (Exception e) {
                log.info("定时任务交付执行失败, jobId: " + jobArr[2], e);
                waylineJobService.updateJob(WaylineJobDTO.builder()
                        .jobId(jobArr[2])
                        .status(WaylineJobStatusEnum.FAILED.getVal())
                        .executeTime(LocalDateTime.now())
                        .completedTime(LocalDateTime.now())
                        .code(HttpStatus.SC_INTERNAL_SERVER_ERROR).build());
            } finally {
                RedisOpsUtils.zRemove(RedisConst.WAYLINE_JOB_TIMED_EXECUTE, jobIdValue);
            }
        }
    }

    @Scheduled(initialDelay = 10, fixedRate = 5, timeUnit = TimeUnit.SECONDS)
    public void prepareConditionJob() {
        Optional<ConditionalWaylineJobKey> jobKeyOpt = waylineRedisService.getNearestConditionalWaylineJob();
        if (jobKeyOpt.isEmpty()) {
            return;
        }
        ConditionalWaylineJobKey jobKey = jobKeyOpt.get();
        log.info("Check the conditional tasks of the wayline. {}", jobKey);
        // format: {workspace_id}:{dock_sn}:{job_id}
        double time = waylineRedisService.getConditionalWaylineJobTime(jobKey);
        long now = System.currentTimeMillis();
        // prepare the task one day in advance.
        int offset = 86_400_000;

        if (now + offset < time) {
            return;
        }

        WaylineJobDTO job = WaylineJobDTO.builder()
                .jobId(jobKey.getJobId())
                .status(WaylineJobStatusEnum.FAILED.getVal())
                .executeTime(LocalDateTime.now())
                .completedTime(LocalDateTime.now())
                .code(HttpStatus.SC_INTERNAL_SERVER_ERROR).build();
        try {
            Optional<WaylineJobDTO> waylineJobOpt = waylineRedisService.getConditionalWaylineJob(jobKey.getJobId());
            if (waylineJobOpt.isEmpty()) {
                job.setCode(CommonErrorEnum.REDIS_DATA_NOT_FOUND.getCode());
                waylineJobService.updateJob(job);
                waylineRedisService.removePrepareConditionalWaylineJob(jobKey);
                return;
            }
            WaylineJobDTO waylineJob = waylineJobOpt.get();

            HttpResultResponse result = this.publishOneFlightTask(waylineJob);
            waylineRedisService.removePrepareConditionalWaylineJob(jobKey);

            if (HttpResultResponse.CODE_SUCCESS == result.getCode()) {
                return;
            }

            // If the end time is exceeded, no more retries will be made.
            waylineRedisService.delConditionalWaylineJob(jobKey.getJobId());
            if (waylineJob.getEndTime().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli() - RedisConst.WAYLINE_JOB_BLOCK_TIME * 1000 < now) {
                return;
            }

            // Retry if the end time has not been exceeded.
            this.retryPrepareJob(jobKey, waylineJob);

        } catch (Exception e) {
            log.info("条件任务准备失败, jobKey: {}", jobKey, e);
            waylineJobService.updateJob(job);
        }

    }

    /**
     * For immediate tasks, the server time shall prevail.
     * @param param
     */
    private void fillImmediateTime(CreateJobParam param) {
        if (TaskTypeEnum.IMMEDIATE != param.getTaskType()) {
            return;
        }
        long now = System.currentTimeMillis() / 1000;
        param.setTaskDays(List.of(now));
        param.setTaskPeriods(List.of(List.of(now)));
    }


    private void addConditions(WaylineJobDTO waylineJob, CreateJobParam param, Long beginTime, Long endTime) {
        if (TaskTypeEnum.CONDITIONAL != param.getTaskType()) {
            return;
        }

        waylineJob.setConditions(
                WaylineTaskConditionDTO.builder()
                        .executableConditions(Objects.nonNull(param.getMinStorageCapacity()) ?
                                new ExecutableConditions().setStorageCapacity(param.getMinStorageCapacity()) : null)
                        .readyConditions(new ReadyConditions()
                                .setBatteryCapacity(param.getMinBatteryCapacity())
                                .setBeginTime(beginTime)
                                .setEndTime(endTime))
                        .build());

        waylineRedisService.setConditionalWaylineJob(waylineJob);
        // key: wayline_job_condition, value: {workspace_id}:{dock_sn}:{job_id}
        boolean isAdd = waylineRedisService.addPrepareConditionalWaylineJob(waylineJob);
        if (!isAdd) {
            throw new RuntimeException("创建条件任务失败.");
        }
    }

    @Override
    public HttpResultResponse publishFlightTask(CreateJobParam param, CustomClaim customClaim) throws SQLException {

        log.debug(":: Publish flight task: {}", param);

        fillImmediateTime(param);

        for (Long taskDay : param.getTaskDays()) {
            LocalDate date = LocalDate.ofInstant(Instant.ofEpochSecond(taskDay), ZoneId.systemDefault());
            for (List<Long> taskPeriod : param.getTaskPeriods()) {
                long beginTime = LocalDateTime.of(date, LocalTime.ofInstant(Instant.ofEpochSecond(taskPeriod.get(0)), ZoneId.systemDefault()))
                        .atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
                long endTime = taskPeriod.size() > 1 ?
                        LocalDateTime.of(date, LocalTime.ofInstant(Instant.ofEpochSecond(taskPeriod.get(1)), ZoneId.systemDefault()))
                                .atZone(ZoneId.systemDefault()).toInstant().toEpochMilli() : beginTime;
                if (TaskTypeEnum.IMMEDIATE != param.getTaskType() && endTime < System.currentTimeMillis()) {
                    continue;
                }

                Optional<WaylineJobDTO> waylineJobOpt = waylineJobService.createWaylineJob(param, customClaim.getWorkspaceId(),
                        customClaim.getUsername(), beginTime, endTime);
                if (waylineJobOpt.isEmpty()) {
                    throw new SQLException("创建航线飞行计划失败。");
                }

                WaylineJobDTO waylineJob = waylineJobOpt.get();
                waylineJob.setRthMode(param.getRthMode());
                waylineJob.setWaylinePrecisionType(param.getWaylinePrecisionType());
                waylineJob.setSimulateMission(param.getSimulateMission());

                // If it is a conditional task type, add conditions to the job parameters.
                addConditions(waylineJob, param, beginTime, endTime);

                HttpResultResponse response = this.publishOneFlightTask(waylineJob);
                if (HttpResultResponse.CODE_SUCCESS != response.getCode()) {
                    return response;
                }
            }
        }
        return HttpResultResponse.success();
    }

    @Override
    public HttpResultResponse publishOneFlightTask(WaylineJobDTO waylineJob) throws SQLException {

        boolean isOnline = deviceRedisService.checkDeviceOnline(waylineJob.getDockSn());
        if (!isOnline) {
            throw new RuntimeException("机场已离线。");
        }

        boolean isSuccess = this.prepareFlightTask(waylineJob);
        if (!isSuccess) {
            return HttpResultResponse.error("飞行任务下发准备失败。");
        }

        // Issue an immediate task execution command.
        if (TaskTypeEnum.IMMEDIATE == waylineJob.getTaskType()) {
            if (!executeFlightTask(waylineJob.getWorkspaceId(), waylineJob.getJobId())) {
                return HttpResultResponse.error("飞行任务下发执行失败。");
            }
        }

        // Issue a timed task execution command.
        if (TaskTypeEnum.TIMED == waylineJob.getTaskType()) {
            // key: wayline_job_timed, value: {workspace_id}:{dock_sn}:{job_id}
            boolean isAdd = RedisOpsUtils.zAdd(RedisConst.WAYLINE_JOB_TIMED_EXECUTE,
                    waylineJob.getWorkspaceId() + RedisConst.DELIMITER + waylineJob.getDockSn() + RedisConst.DELIMITER + waylineJob.getJobId(),
                    waylineJob.getBeginTime().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli());
            if (!isAdd) {
                return HttpResultResponse.error("创建定时任务失败。");
            }
        }

        return HttpResultResponse.success();
    }

    private Boolean prepareFlightTask(WaylineJobDTO waylineJob) throws SQLException {
        // get wayline file
        Optional<GetWaylineListResponse> waylineFile = waylineFileService.getWaylineByWaylineId(waylineJob.getWorkspaceId(), waylineJob.getFileId());
        if (waylineFile.isEmpty()) {
            throw new SQLException("无法获取飞行任务的航线文件，请查证。");
        }

        // get file url
        URL url = waylineFileService.getObjectUrl(waylineJob.getWorkspaceId(), waylineFile.get().getId());

        FlighttaskPrepareRequest flightTask = new FlighttaskPrepareRequest()
                .setFlightId(waylineJob.getJobId())
                .setExecuteTime(waylineJob.getBeginTime().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli())
                .setTaskType(waylineJob.getTaskType())
                .setWaylineType(waylineJob.getWaylineType())
                .setRthAltitude(waylineJob.getRthAltitude())
                .setOutOfControlAction(waylineJob.getOutOfControlAction())
                .setExitWaylineWhenRcLost(ExitWaylineWhenRcLostEnum.EXECUTE_RC_LOST_ACTION)
                .setFile(new FlighttaskFile()
                        .setUrl(url.toString())
                        .setFingerprint(waylineFile.get().getSign()))
                .setSimulateMission(waylineJob.getSimulateMission())
                .setWaylinePrecisionType(waylineJob.getWaylinePrecisionType());
        if (Objects.nonNull(waylineJob.getRthMode())) {
            flightTask.setRthMode(waylineJob.getRthMode());
        }

        if (TaskTypeEnum.CONDITIONAL == waylineJob.getTaskType()) {
            if (Objects.isNull(waylineJob.getConditions())) {
                throw new IllegalArgumentException("无法获取当前飞行任务的可执行条件。");
            }
            flightTask.setReadyConditions(waylineJob.getConditions().getReadyConditions());
            flightTask.setExecutableConditions(waylineJob.getConditions().getExecutableConditions());
        }

        // modify by Qfei, 2023-10-11 10:31:44
        if (waylineJob.getContinuable() && StringUtils.hasText(waylineJob.getParentId())) {
            flightTask.setBreakPoint(waylineJob.getBreakPoint());
        }

        log.debug(":: Prepare task: " + flightTask);

        TopicServicesResponse<ServicesReplyData> serviceReply = abstractWaylineService.flighttaskPrepare(
                SDKManager.getDeviceSDK(waylineJob.getDockSn()), flightTask);
        if (!serviceReply.getData().getResult().isSuccess()) {
            log.error("Prepare task ====> Error code: {}", serviceReply.getData().getResult());
            waylineJobService.updateJob(WaylineJobDTO.builder()
                    .workspaceId(waylineJob.getWorkspaceId())
                    .jobId(waylineJob.getJobId())
                    .executeTime(LocalDateTime.now())
                    .status(WaylineJobStatusEnum.FAILED.getVal())
                    .completedTime(LocalDateTime.now())
                    .code(serviceReply.getData().getResult().getCode()).build());
            return false;
        }
        return true;
    }


    @Override
    public Boolean executeFlightTask(String workspaceId, String jobId) {
        // get job
        Optional<WaylineJobDTO> waylineJob = waylineJobService.getJobByJobId(workspaceId, jobId);
        if (waylineJob.isEmpty()) {
            throw new IllegalArgumentException("飞行计划不存在。");
        }

        boolean isOnline = deviceRedisService.checkDeviceOnline(waylineJob.get().getDockSn());
        if (!isOnline) {
            throw new RuntimeException("机场离线状态，无法执行。");
        }

        WaylineJobDTO job = waylineJob.get();

        TopicServicesResponse<ServicesReplyData> serviceReply = abstractWaylineService.flighttaskExecute(
                SDKManager.getDeviceSDK(job.getDockSn()), new FlighttaskExecuteRequest().setFlightId(jobId));
        if (!serviceReply.getData().getResult().isSuccess()) {
            log.info("Execute job ====> Error: {}", serviceReply.getData().getResult());
            waylineJobService.updateJob(WaylineJobDTO.builder()
                    .jobId(jobId)
                    .executeTime(LocalDateTime.now())
                    .status(WaylineJobStatusEnum.FAILED.getVal())
                    .completedTime(LocalDateTime.now())
                    .code(serviceReply.getData().getResult().getCode()).build());
            // The conditional task fails and enters the blocking status.
            if (TaskTypeEnum.CONDITIONAL == job.getTaskType()
                    && WaylineErrorCodeEnum.find(serviceReply.getData().getResult().getCode()).isBlock()) {
                waylineRedisService.setBlockedWaylineJob(job.getDockSn(), jobId);
            }
            return false;
        }

        waylineJobService.updateJob(WaylineJobDTO.builder()
                .jobId(jobId)
                .executeTime(LocalDateTime.now())
                .status(WaylineJobStatusEnum.IN_PROGRESS.getVal())
                .build());
        waylineRedisService.setRunningWaylineJob(job.getDockSn(), EventsReceiver.<FlighttaskProgress>builder().bid(jobId).sn(job.getDockSn()).build());

        // add by Qfei, report start a wayline job.
        this.flightTaskClient.startFlightTask(job);

        return true;
    }

    @Override
    public void cancelFlightTask(String workspaceId, Collection<String> jobIds) {
        List<WaylineJobDTO> waylineJobs = waylineJobService.getJobsByConditions(workspaceId, jobIds, WaylineJobStatusEnum.PENDING);

        Set<String> waylineJobIds = waylineJobs.stream().map(WaylineJobDTO::getJobId).collect(Collectors.toSet());
        // Check if the task status is correct.
        boolean isErr = !jobIds.removeAll(waylineJobIds) || !jobIds.isEmpty() ;
        if (isErr) {
            List<WaylineJobDTO> cannotCancelJobs = waylineJobService.getJobsByConditions(workspaceId, jobIds, null);
            throw new IllegalArgumentException("操作失败，以下任务的状态不支持取消: "
                    + Arrays.toString(cannotCancelJobs.stream().map(WaylineJobDTO::getJobName).toArray()));
        }

        // Group job id by dock sn.
        Map<String, List<String>> dockJobs = waylineJobs.stream()
                .collect(Collectors.groupingBy(WaylineJobDTO::getDockSn,
                        Collectors.mapping(WaylineJobDTO::getJobId, Collectors.toList())));
        dockJobs.forEach((dockSn, idList) -> this.publishCancelTask(workspaceId, dockSn, idList));

    }

    @Override
    public void publishCancelTask(String workspaceId, String dockSn, List<String> jobIds) {
        boolean isOnline = deviceRedisService.checkDeviceOnline(dockSn);
        if (!isOnline) {
            throw new RuntimeException("机场已离线，请稍候重试！");
        }

        TopicServicesResponse<ServicesReplyData> serviceReply = abstractWaylineService.flighttaskUndo(SDKManager.getDeviceSDK(dockSn),
                new FlighttaskUndoRequest().setFlightIds(jobIds));
        if (!serviceReply.getData().getResult().isSuccess()) {
            log.error("Cancel job ====> Error: {}", serviceReply.getData().getResult());
            throw new RuntimeException("取消飞行计划失败，机场SN: " + dockSn);
        }

        for (String jobId : jobIds) {
            waylineJobService.updateJob(WaylineJobDTO.builder()
                    .workspaceId(workspaceId)
                    .jobId(jobId)
                    .status(WaylineJobStatusEnum.CANCEL.getVal())
                    .completedTime(LocalDateTime.now())
                    .build());
            RedisOpsUtils.zRemove(RedisConst.WAYLINE_JOB_TIMED_EXECUTE, workspaceId + RedisConst.DELIMITER + dockSn + RedisConst.DELIMITER + jobId);
        }

    }

    @Override
    public void uploadMediaHighestPriority(String workspaceId, String jobId) {
        Optional<WaylineJobDTO> jobOpt = waylineJobService.getJobByJobId(workspaceId, jobId);
        if (jobOpt.isEmpty()) {
            throw new RuntimeException(CommonErrorEnum.ILLEGAL_ARGUMENT.getMessage());
        }

        String dockSn = jobOpt.get().getDockSn();
        String key = RedisConst.MEDIA_HIGHEST_PRIORITY_PREFIX + dockSn;
        if (RedisOpsUtils.checkExist(key) && jobId.equals(((MediaFileCountDTO) RedisOpsUtils.get(key)).getJobId())) {
            return;
        }

        TopicServicesResponse<ServicesReplyData> reply = abstractMediaService.uploadFlighttaskMediaPrioritize(
                SDKManager.getDeviceSDK(dockSn), new UploadFlighttaskMediaPrioritize().setFlightId(jobId));
        if (!reply.getData().getResult().isSuccess()) {
            throw new RuntimeException("设置任务媒体文件优先上传失败. 错误码: " + reply.getData().getResult());
        }
    }

    @Override
    public void updateJobStatus(String workspaceId, String jobId, UpdateJobParam param) {
        Optional<WaylineJobDTO> waylineJobOpt = waylineJobService.getJobByJobId(workspaceId, jobId);
        if (waylineJobOpt.isEmpty()) {
            throw new RuntimeException("操作失败，飞行计划不存在。");
        }
        WaylineJobDTO waylineJob = waylineJobOpt.get();
        WaylineJobStatusEnum statusEnum = waylineJobService.getWaylineState(waylineJob.getDockSn());
        if (statusEnum.getEnd() || WaylineJobStatusEnum.PENDING == statusEnum) {
            throw new RuntimeException("当前飞行计划已结束或处于未执行状态，不能执行当前操作。");
        }

        switch (param.getStatus()) {
            case PAUSE:
                pauseJob(workspaceId, waylineJob.getDockSn(), jobId, statusEnum);
                break;
            case RESUME:
                resumeJob(workspaceId, waylineJob.getDockSn(), jobId, statusEnum);
                break;
            default:
                break;
        }

    }
    @Override
    public HttpResultResponse breakPointContinueFlight(String workspaceId, String jobId) throws SQLException {

        Optional<ProgressExtBreakPoint> breakPointReceiver = waylineRedisService.getProgressExtBreakPoint(jobId);
        if (breakPointReceiver.isEmpty()) {
            return HttpResultResponse.error("无法获取航线断点信息，无法继续飞行。");
        }
        Optional<WaylineJobDTO> waylineJob = waylineJobService.createWaylineJobByParent(workspaceId, jobId, true);
        if (waylineJob.isEmpty()) {
            return HttpResultResponse.error("创建断点飞行任务失败。");
        }

        if (!this.prepareFlightTask(waylineJob.get())) {
            waylineJobService.deleteJob(workspaceId, jobId);
            return HttpResultResponse.error("飞行任务下发失败。");
        }
        // Issue an immediate task execution command.
        if (!executeFlightTask(waylineJob.get().getWorkspaceId(), waylineJob.get().getJobId())) {
            // 断点续飞任务如果失败,删除重新从父节点下发继续飞行的任务
            waylineJobService.deleteJob(workspaceId, jobId);
            return HttpResultResponse.error("飞行任务执行失败。");
        }
        // 执行成功，需要将父节点任务执行状态修改为ok
        waylineJobService.updateJob(WaylineJobDTO.builder()
                .workspaceId(workspaceId)
                .jobId(waylineJob.get().getParentId())
                .status(WaylineJobStatusEnum.SUCCESS.getVal())
                .build());

        return HttpResultResponse.success();
    }

    private void pauseJob(String workspaceId, String dockSn, String jobId, WaylineJobStatusEnum statusEnum) {
        if (WaylineJobStatusEnum.PAUSED == statusEnum && jobId.equals(waylineRedisService.getPausedWaylineJobId(dockSn))) {
            waylineRedisService.setPausedWaylineJob(dockSn, jobId);
            return;
        }

        TopicServicesResponse<ServicesReplyData> reply = abstractWaylineService.flighttaskPause(SDKManager.getDeviceSDK(dockSn));
        if (!reply.getData().getResult().isSuccess()) {
            throw new RuntimeException("Failed to pause wayline job. Error: " + reply.getData().getResult());
        }
        waylineRedisService.delRunningWaylineJob(dockSn);
        waylineRedisService.setPausedWaylineJob(dockSn, jobId);
    }

    private void resumeJob(String workspaceId, String dockSn, String jobId, WaylineJobStatusEnum statusEnum) {
        Optional<EventsReceiver<FlighttaskProgress>> runningDataOpt = waylineRedisService.getRunningWaylineJob(dockSn);
        if (WaylineJobStatusEnum.IN_PROGRESS == statusEnum && jobId.equals(runningDataOpt.map(EventsReceiver::getSn).get())) {
            waylineRedisService.setRunningWaylineJob(dockSn, runningDataOpt.get());
            return;
        }
        TopicServicesResponse<ServicesReplyData> reply = abstractWaylineService.flighttaskRecovery(SDKManager.getDeviceSDK(dockSn));
        if (!reply.getData().getResult().isSuccess()) {
            throw new RuntimeException("Failed to resume wayline job. Error: " + reply.getData().getResult());
        }

        runningDataOpt.ifPresent(runningData -> waylineRedisService.setRunningWaylineJob(dockSn, runningData));
        waylineRedisService.delPausedWaylineJob(dockSn);
    }

    @Override
    public void retryPrepareJob(ConditionalWaylineJobKey jobKey, WaylineJobDTO waylineJob) {
        Optional<WaylineJobDTO> childJobOpt = waylineJobService.createWaylineJobByParent(jobKey.getWorkspaceId(), jobKey.getJobId(), false);
        if (childJobOpt.isEmpty()) {
            log.error("条件任务重新创建失败.");
            return;
        }

        WaylineJobDTO newJob = childJobOpt.get();
        newJob.setBeginTime(LocalDateTime.now().plusSeconds(RedisConst.WAYLINE_JOB_BLOCK_TIME));
        boolean isAdd = waylineRedisService.addPrepareConditionalWaylineJob(newJob);
        if (!isAdd) {
            log.error("创建飞行计划条件任务失败. {}", newJob.getJobId());
            return;
        }

        waylineJob.setJobId(newJob.getJobId());
        waylineRedisService.setConditionalWaylineJob(waylineJob);
    }


    @Override
    public TopicEventsResponse<MqttReply> flighttaskReady(TopicEventsRequest<FlighttaskReady> response, MessageHeaders headers) {
        List<String> flightIds = response.getData().getFlightIds();

        log.info("- Ready task list：{}", Arrays.toString(flightIds.toArray()) );
        // Check conditional task blocking status.
        String blockedId = waylineRedisService.getBlockedWaylineJobId(response.getGateway());
        if (!StringUtils.hasText(blockedId)) {
            return null;
        }

        Optional<DeviceDTO> deviceOpt = deviceRedisService.getDeviceOnline(response.getGateway());
        if (deviceOpt.isEmpty()) {
            return null;
        }
        DeviceDTO device = deviceOpt.get();

        try {
            for (String jobId : flightIds) {
                boolean isExecute = this.executeFlightTask(device.getWorkspaceId(), jobId);
                if (!isExecute) {
                    return null;
                }
                Optional<WaylineJobDTO> waylineJobOpt = waylineRedisService.getConditionalWaylineJob(jobId);
                if (waylineJobOpt.isEmpty()) {
                    log.info("条件任务已超时，将不再执行，计划ID: " + jobId);
                    return new TopicEventsResponse<>();
                }
                WaylineJobDTO waylineJob = waylineJobOpt.get();
                this.retryPrepareJob(new ConditionalWaylineJobKey(device.getWorkspaceId(), response.getGateway(), jobId), waylineJob);
                return new TopicEventsResponse<>();
            }
        } catch (Exception e) {
            log.error("飞行计划条件任务执行失败。", e);
        }
        return new TopicEventsResponse<>();
    }

}
