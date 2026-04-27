package com.dji.sample.wayline.service.impl;

import cn.hutool.cron.CronUtil;
import com.dji.sample.cloudapi.client.FlightTaskClient;
import com.dji.sample.common.error.CommonErrorEnum;
import com.dji.sample.common.model.CustomClaim;
import com.dji.sample.component.mqtt.model.EventsReceiver;
import com.dji.sample.component.redis.RedisConst;
import com.dji.sample.component.redis.RedisOpsUtils;
import com.dji.sample.component.websocket.model.BizCodeEnum;
import com.dji.sample.component.websocket.service.IWebSocketMessageService;
import com.dji.sample.manage.model.dto.DeviceDTO;
import com.dji.sample.manage.model.enums.UserTypeEnum;
import com.dji.sample.manage.service.IDeviceRedisService;
import com.dji.sample.manage.service.IDeviceService;
import com.dji.sample.media.model.MediaFileCountDTO;
import com.dji.sample.media.service.IFileService;
import com.dji.sample.media.service.IMediaRedisService;
import com.dji.sample.wayline.FlightTaskProperties;
import com.dji.sample.wayline.StopFlyingCondition;
import com.dji.sample.wayline.model.dto.ConditionalWaylineJobKey;
import com.dji.sample.wayline.model.dto.DroneReturnHomeMonitor;
import com.dji.sample.wayline.model.dto.WaylineJobDTO;
import com.dji.sample.wayline.model.dto.WaylineTaskConditionDTO;
import com.dji.sample.wayline.model.enums.WaylineErrorCodeEnum;
import com.dji.sample.wayline.model.enums.WaylineJobStatusEnum;
import com.dji.sample.wayline.model.param.CreateInFlightWaylineTask;
import com.dji.sample.wayline.model.param.CreateJobParam;
import com.dji.sample.wayline.model.param.UpdateInFlightWaylineParam;
import com.dji.sample.wayline.model.param.UpdateJobParam;
import com.dji.sample.wayline.service.IFlightTaskService;
import com.dji.sample.wayline.service.IWaylineFileService;
import com.dji.sample.wayline.service.IWaylineJobService;
import com.dji.sample.wayline.service.IWaylineRedisService;
import com.dji.sdk.cloudapi.device.*;
import com.dji.sdk.cloudapi.media.UploadFlighttaskMediaPrioritize;
import com.dji.sdk.cloudapi.media.api.AbstractMediaService;
import com.dji.sdk.cloudapi.wayline.*;
import com.dji.sdk.cloudapi.wayline.api.AbstractWaylineService;
import com.dji.sdk.common.HttpResultResponse;
import com.dji.sdk.common.SDKManager;
import com.dji.sdk.mqtt.MqttReply;
import com.dji.sdk.mqtt.events.EventsDataRequest;
import com.dji.sdk.mqtt.events.TopicEventsRequest;
import com.dji.sdk.mqtt.events.TopicEventsResponse;
import com.dji.sdk.mqtt.requests.TopicRequestsRequest;
import com.dji.sdk.mqtt.requests.TopicRequestsResponse;
import com.dji.sdk.mqtt.services.ServicesReplyData;
import com.dji.sdk.mqtt.services.TopicServicesResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.messaging.MessageHeaders;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
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

    @Autowired
    private IWebSocketMessageService webSocketMessageService;

    @Autowired
    private IFileService fileService;
    @Autowired
    private FlightTaskProperties flightTaskProperties;
    @Autowired
    private IDeviceService deviceService;


    @Scheduled(initialDelay = 10, fixedRate = 5, timeUnit = TimeUnit.SECONDS)
    public void checkScheduledJob() {

        int range = 5_000;
        long now = System.currentTimeMillis();

        Set<Object> timedJobSet = RedisOpsUtils.zRangeByScore(RedisConst.WAYLINE_JOB_TIMED_EXECUTE, now, now + range);
        if (CollectionUtils.isEmpty(timedJobSet)) {
            return;
        }
        log.info("Check the timed tasks of the wayline. size: {}", timedJobSet.size());
        timedJobSet.parallelStream().forEach(jobIdValue -> {
            // Object jobIdValue = RedisOpsUtils.zGetMin(RedisConst.WAYLINE_JOB_TIMED_EXECUTE);
            // if (Objects.isNull(jobIdValue)) {
            //     return;
            // }
            log.info("Find timed job. {}", jobIdValue);
            // format: {workspace_id}:{dock_sn}:{job_id}
            String[] jobArr = String.valueOf(jobIdValue).split(RedisConst.DELIMITER);
            double time = RedisOpsUtils.zScore(RedisConst.WAYLINE_JOB_TIMED_EXECUTE, jobIdValue);
            int offset = 120_000;

            // Expired tasks are deleted directly.
            if (time < now - offset) {
                log.info("The timed task has expired. {}", jobIdValue);
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
                    log.error("定时任务交付执行失败, jobId: {}", jobArr[2], e);
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
        });
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

    // @Scheduled(initialDelay = 10, fixedRate = 60, timeUnit = TimeUnit.SECONDS)
    public void checkBreakPointJob() {
        if (!flightTaskProperties.getBreakPointCondition().getEnabled()) {
            return;
        }

        int start = RedisConst.WAYLINE_JOB_BREAKPOINT_PREFIX.length();
        RedisOpsUtils.getAllKeys(RedisConst.WAYLINE_JOB_BREAKPOINT_PREFIX + "*").forEach(key -> {
            String jobId = key.substring(start);
            Optional<WaylineJobDTO> jobOpt = waylineJobService.getJobByJobId(null, jobId);
            if (jobOpt.isEmpty() || WaylineJobStatusEnum.CANCEL.getVal() == jobOpt.get().getStatus()) {
                return;
            }
            WaylineJobDTO breakJob = jobOpt.get();
            log.info("Check the breakpoint task. JobName: {}, JobId: {}", breakJob.getJobName(), jobId);
            // 判断航线是否处于成功状态，成功状态说明已经飞过了
            if (WaylineJobStatusEnum.SUCCESS.getVal() == breakJob.getStatus()) {
                waylineRedisService.delProgressExtBreakPoint(jobId);
                return;
            }
            // 判断机场状态 && 判断飞机电量
            String dockSn = breakJob.getDockSn();
            Optional<OsdDock> dockOsd = deviceRedisService.getDeviceOsd(dockSn, OsdDock.class);
            if (!deviceRedisService.checkDeviceOnline(dockSn)
                    || dockOsd.isEmpty()
                    || DockModeCodeEnum.IDLE != dockOsd.get().getModeCode()
                    || dockOsd.get().getDroneChargeState().getCapacityPercent() < flightTaskProperties.getBreakPointCondition().getBatteryCapacity()
                    || dockOsd.get().getWindSpeed() > flightTaskProperties.getBreakPointCondition().getWindSpeed()) {
                return;
            }
            try {
                breakPointContinueFlight(breakJob.getWorkspaceId(), jobId);
            } catch (SQLException e) {
                log.error("下发断点续飞任务异常", e);
            }
        });
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
    public HttpResultResponse<List<String>> publishFlightTask(CreateJobParam param, CustomClaim customClaim) throws SQLException {

        log.debug(":: Publish flight task: {}", param);

        fillImmediateTime(param);

        List<String> addSuccessJobIdList = new ArrayList<>();
        for (Long taskDay : param.getTaskDays()) {
            LocalDate date = LocalDate.ofInstant(Instant.ofEpochSecond(taskDay), ZoneId.systemDefault());
            for (List<Long> taskPeriod : param.getTaskPeriods()) {
                long beginTime = LocalDateTime.of(date, LocalTime.ofInstant(Instant.ofEpochSecond(taskPeriod.get(0)), ZoneId.systemDefault()))
                        .atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
                long endTime = taskPeriod.size() > 1 ?
                        LocalDateTime.of(date, LocalTime.ofInstant(Instant.ofEpochSecond(taskPeriod.get(1)), ZoneId.systemDefault()))
                                .atZone(ZoneId.systemDefault()).toInstant().toEpochMilli() : beginTime;
                if (TaskTypeEnum.IMMEDIATE != param.getTaskType() && endTime < System.currentTimeMillis()) {
                    log.warn("定时计划飞行[结束时间]错误，TaskType: {}, EndTime: {}", param.getTaskType(), endTime);
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
                addSuccessJobIdList.add(waylineJob.getJobId());
            }
        }
        return HttpResultResponse.success(addSuccessJobIdList);
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

        log.debug(":: Prepare task: {}", flightTask);

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

        String dockSn = waylineJob.get().getDockSn();
        boolean isOnline = deviceRedisService.checkDeviceOnline(dockSn);
        if (!isOnline) {
            throw new RuntimeException("机场离线状态，无法执行。");
        }

        // 自定义阻飞检查
        if (flightTaskProperties.getStopFlyingCondition().getEnabled() && !checkFlyingCondition(jobId, dockSn)) {
            log.error("机场当前状态或当前环境条件不满足安全飞行，已取消当前飞行计划。");
            return false;
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
        this.flightTaskClient.startWaylineTask(job);

        return true;
    }

    /**
     * 飞行任务阻飞检查
     * @param jobId
     * @param dockSn
     * @return
     */
    private Boolean checkFlyingCondition(String jobId, String dockSn) {
        Optional<OsdDock> osdDockOpt = deviceRedisService.getDeviceOsd(dockSn, OsdDock.class);
        if (osdDockOpt.isEmpty()) {
            throw new RuntimeException("机场离线，请稍候重试");
        }
        OsdDock osdDock = osdDockOpt.get();
        boolean canFlying = true;
        WaylineErrorCodeEnum errorCode = WaylineErrorCodeEnum.UNKNOWN;
        StopFlyingCondition stopFlyingCondition = flightTaskProperties.getStopFlyingCondition().getDeviceCondition(dockSn);
        if (osdDock.getWindSpeed() >= stopFlyingCondition.getWindSpeed()) {
            log.warn("[Flying-Check] ===> False, SN: {}, ErrorCode: {}", dockSn, errorCode);
            canFlying = false;
            errorCode = WaylineErrorCodeEnum.STRONG_WIND;
        }
        if (!canFlying) {
            waylineJobService.updateJob(WaylineJobDTO.builder()
                    .jobId(jobId)
                    .executeTime(LocalDateTime.now())
                    .status(WaylineJobStatusEnum.CANCEL.getVal())
                    .completedTime(LocalDateTime.now())
                    .code(errorCode.getCode()).build());
            TopicServicesResponse<ServicesReplyData> serviceReply = abstractWaylineService.flighttaskUndo(
                    SDKManager.getDeviceSDK(dockSn),
                    new FlighttaskUndoRequest().setFlightIds(Collections.singletonList(jobId)));
            if (!serviceReply.getData().getResult().isSuccess()) {
                log.error("Cancel job ====> Error: {}", serviceReply.getData().getResult());
            }
        }
        return canFlying;
    }

    @Override
    public void cancelFlightTask(String workspaceId, Collection<String> jobIds) {
        // 输入参数校验
        if (Objects.isNull(jobIds) || jobIds.isEmpty()) {
            throw new IllegalArgumentException("操作失败，任务 ID 列表不能为空");
        }

        // 查询所有处于 PENDING 状态的任务（只有该状态的任务可以取消）
        List<WaylineJobDTO> waylineJobs = waylineJobService.getJobsByConditions(workspaceId, jobIds, WaylineJobStatusEnum.PENDING.getVal());
        Set<String> pendingJobIds = waylineJobs.stream()
                .map(WaylineJobDTO::getJobId)
                .collect(Collectors.toSet());

        // 找出不可取消的任务（不在 PENDING 状态的任务）
        List<String> invalidJobIds = jobIds.stream()
                .filter(jobId -> !pendingJobIds.contains(jobId))
                .collect(Collectors.toList());

        // 如果存在不可取消的任务，抛出异常
        if (!CollectionUtils.isEmpty(invalidJobIds)) {
            log.warn("Cannot cancel job ====> Invalid job ids: {}", invalidJobIds);
            List<WaylineJobDTO> cannotCancelJobs = waylineJobService.getJobsByConditions(workspaceId, invalidJobIds, null);
            String jobNames = cannotCancelJobs.stream()
                    .map(WaylineJobDTO::getJobName)
                    .collect(Collectors.joining(", "));
            throw new IllegalArgumentException("操作失败，以下任务的状态不支持取消：" + jobNames);
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
        log.info("下发断点续飞任务：{}", jobId);
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
    public TopicEventsResponse<MqttReply> deviceExitHomingNotify(TopicEventsRequest<DeviceExitHomingNotify> request, MessageHeaders headers) {
            log.error("*************** deviceExitHomingNotify not implemented! ***************");
        log.info("- Device exit Homing notify: gateway: {}, data: {}", request.getGateway(), request.getData());
        return new TopicEventsResponse<MqttReply>();
    }

    @Override
    public TopicEventsResponse<MqttReply> flighttaskProgress(TopicEventsRequest<EventsDataRequest<FlighttaskProgress>> response, MessageHeaders headers) {
        EventsReceiver<FlighttaskProgress> eventsReceiver = new EventsReceiver<>();
        eventsReceiver.setResult(response.getData().getResult());
        eventsReceiver.setOutput(response.getData().getOutput());
        eventsReceiver.setBid(response.getBid());
        eventsReceiver.setSn(response.getGateway());

        FlighttaskProgress output = eventsReceiver.getOutput();
        log.info("Task progress: {}:{}, {}", response.getGateway(), response.getBid(), output.getProgress().toString());
        if (!eventsReceiver.getResult().isSuccess()) {
            log.error("Task progress ===> Error: {}", eventsReceiver.getResult());
        }

        Optional<DeviceDTO> deviceOpt = deviceRedisService.getDeviceOnline(response.getGateway());
        if (deviceOpt.isEmpty()) {
            return new TopicEventsResponse<>();
        }

        FlighttaskStatusEnum statusEnum = output.getStatus();
        waylineRedisService.setRunningWaylineJob(response.getGateway(), eventsReceiver);

        if (statusEnum.isEnd()) {
            String droneSn = deviceOpt.get().getChildDeviceSn();
            Optional<OsdDock> dockOsdOpt = deviceRedisService.getDeviceOsd(response.getGateway(), OsdDock.class);
            DroneModeCodeEnum droneModeCodeEnum = deviceService.getDeviceMode(droneSn);
            Boolean droneInDock = dockOsdOpt.map(OsdDock::getDroneInDock).orElse(false);
            log.info("Task completed. SN: {}, FlightId: {}, DroneModeCode: {}, DroneInDock: {}",
                    response.getGateway(), response.getBid(), droneModeCodeEnum, droneInDock);
            try {
                returnHomeMonitor(response.getGateway(), droneSn, response.getBid(), droneModeCodeEnum, droneInDock);
            } catch (Exception e) {
                log.error("Add Return home monitor error.", e);
            }

            WaylineJobDTO job = WaylineJobDTO.builder()
                    .jobId(response.getBid())
                    .status(WaylineJobStatusEnum.SUCCESS.getVal())
                    .completedTime(LocalDateTime.now())
                    .mediaCount(output.getExt().getMediaCount())
                    .build();

            MediaFileCountDTO mediaCount = mediaRedisService.getMediaCount(response.getGateway(), response.getBid());
            int uploadedSize = fileService.getFilesByWorkspaceAndJobId(deviceOpt.get().getWorkspaceId(), response.getBid()).size();
            if (Objects.nonNull(mediaCount)) {
                mediaCount.setUploadedCount(uploadedSize);
                mediaCount.setMediaCount(job.getMediaCount());
                mediaRedisService.setMediaCount(response.getGateway(), job.getJobId(), mediaCount);
            }
            // 如果文件上传完成，则删除mediaCount
            if (uploadedSize >= job.getMediaCount()) {
                mediaRedisService.delMediaCount(response.getGateway(), response.getBid());
                mediaRedisService.delMediaHighestPriority(response.getGateway());
            }

            Optional<WaylineJobDTO> jobDTO = waylineJobService.getJobByJobId(deviceOpt.get().getWorkspaceId(), response.getBid());

            if (FlighttaskStatusEnum.OK != statusEnum) {
                job.setCode(eventsReceiver.getResult().getCode());
                job.setStatus(WaylineJobStatusEnum.FAILED.getVal());

                ProgressExtBreakPoint breakPoint = output.getExt().getBreakPoint();
                log.info("Job status: {}, break point: {}", statusEnum.getStatus(), breakPoint);
                /*
                 * add by Qfei, 2025-3-27 17:28:22
                 * 判断断点信息是否为空
                 * 1. 如果为空，说明飞行任务还没有飞出机场就失败了
                 *   a. 如果执行的断点续飞任务，将之前断点信息赋值给当前航线任务
                 *   b. 如果执行的是新建任务，此种情况当前任务不能执行续飞操作，需要重新创建新的飞行任务
                 * 2. 如果不为空，保存当前任务的断点信息，可以执行续飞操作
                 */
                if (Objects.isNull(breakPoint)) {
                    jobDTO.ifPresent(x -> {
                        if (x.getContinuable() && StringUtils.hasText(x.getParentId())) {
                            waylineRedisService.getProgressExtBreakPoint(x.getParentId())
                                    .ifPresentOrElse(parBreakPoint ->
                                                    waylineRedisService.setProgressExtBreakPoint(response.getBid(), parBreakPoint),
                                    () -> job.setContinuable(false));
                        }
                    });
                } else {
                    waylineRedisService.setProgressExtBreakPoint(response.getBid(), breakPoint);
                }
            }
            waylineJobService.updateJob(job);
            waylineRedisService.delRunningWaylineJob(response.getGateway());
            waylineRedisService.delPausedWaylineJob(response.getBid());

            // add by Qfei, report flight task end.
            jobDTO.ifPresent(x -> {
                // 如果执行的断点续飞任务，将Redis中存储的之前飞行的断点信息删除
                if (x.getContinuable() && StringUtils.hasText(x.getParentId())) {
                    waylineRedisService.delProgressExtBreakPoint(x.getParentId());
                }
                job.setGroupId(x.getGroupId());
            });
            this.flightTaskClient.waylineTaskCompleted(job);
        }

        webSocketMessageService.sendBatch(deviceOpt.get().getWorkspaceId(), UserTypeEnum.WEB.getVal(),
                BizCodeEnum.FLIGHT_TASK_PROGRESS.getCode(), eventsReceiver);

        // add by Qfei, report flight task progress.
        this.flightTaskClient.flightTaskProgress(response.getBid(), output);

        return new TopicEventsResponse<>();
    }

    /**
     * 航线任务结束后添加飞机返航监控
     *
     * @param dockSn
     * @param droneSn
     * @param jobId
     * @param modeCodeEnum
     * @param droneInDock
     */
    private void returnHomeMonitor(String dockSn, String droneSn, String jobId, DroneModeCodeEnum modeCodeEnum, Boolean droneInDock) {
        if (droneInDock) {
            log.info("飞机已入舱，无需添加飞机入舱监控任务。SN: {}", dockSn);
            return;
        }

        // 如果是返航，就添加返航监控，否则暂时不加监控（如果是手动打断进行别的操作，不进行监控）
        if (DroneModeCodeEnum.DISCONNECTED != modeCodeEnum && DroneModeCodeEnum.RETURN_AUTO != modeCodeEnum
                && DroneModeCodeEnum.LANDING_AUTO != modeCodeEnum && DroneModeCodeEnum.LANDING_FORCED != modeCodeEnum) {
            log.info("航线任务结束后，飞行器不是返航状态、降落状态或未连接状态（信号导致未连接），不添加返航监控任务。");
            return;
        }

        Optional<DroneReturnHomeMonitor> returnHomeMonitorOpt = waylineRedisService.getReturnHomeMonitor(dockSn);
        if (returnHomeMonitorOpt.isPresent() && jobId.equals(returnHomeMonitorOpt.get().getJobId())) {
            log.warn("航线任务返航监听已存在, SN: {}, jobId: {}", dockSn, jobId);
            return;
        }

        String taskId = UUID.randomUUID().toString();
        CronUtil.schedule(taskId, flightTaskProperties.getReturnHomeCron(), () -> {
            /*
             * 根据 机场状态、飞机状态、飞机在舱内状态 判断飞机返航结果：
             * 1. 机场作业中 && 飞机在舱内：返航成功
             * 2. 机场作业中 && 飞机关机状态 && 飞机不在舱内：无法判断，有可能信号不好(继续监控）
             * 3. 机场作业中 && 飞机降落状态 && 飞机和机场相对距离大于预设距离：返航失败，通知用户
             * 4. 机场作业中 && 飞机空闲状态 && 不在舱内：返航失败，通知用户
             * 5. 机场空闲中 && 飞机不在舱内：返航失败，通知用户
             * 6. 其它情况：继续监控（如果一直监控不到，最终状态为：机场空闲状态，飞机未连接状态，此时会走判断条件5）
             */
            Optional<OsdDock> dockOsdOpt = deviceRedisService.getDeviceOsd(dockSn, OsdDock.class);
            if (dockOsdOpt.isEmpty()) {
                returnHomeMonitorOpt.ifPresent(x -> CronUtil.remove(x.getTaskId()));
                waylineRedisService.delReturnHomeMonitor(dockSn);
                return;
            }
            OsdDock osdDock = dockOsdOpt.get();
            DroneModeCodeEnum deviceModeCodeEnum = deviceService.getDeviceMode(droneSn);
            Optional<OsdDockDrone> droneOsdOpt = deviceRedisService.getDeviceOsd(droneSn, OsdDockDrone.class);

            DockModeCodeEnum dockModeEnum = deviceService.getDockMode(dockSn);
            if (DockModeCodeEnum.WORKING == dockModeEnum) {
                if (osdDock.getDroneInDock()) {
                    log.info("机场作业中，飞机在舱内，返航成功。");
                    returnHomeSuccess(dockSn);
                    return;
                }
                // 判断飞机状态
                if ((DroneModeCodeEnum.LANDING_AUTO == deviceModeCodeEnum || DroneModeCodeEnum.LANDING_FORCED == deviceModeCodeEnum)
                        && droneOsdOpt.isPresent() && droneOsdOpt.get().getHomeDistance() > flightTaskProperties.getHomeDistanceMonitor()) {
                    log.error("飞机降落状态，并且飞行器和机场水平距离大于设定值，飞机落在舱外。");
                    returnHomeFailAndNotifyUser(dockSn);
                } else if (DroneModeCodeEnum.IDLE == deviceModeCodeEnum && !osdDock.getDroneInDock()) {
                    log.error("飞机空闲状态，飞机不在舱内，返航失败。");
                    returnHomeFailAndNotifyUser(dockSn);
                }
            } else if (DockModeCodeEnum.IDLE == dockModeEnum && !osdDock.getDroneInDock()) {
                log.error("机场空闲中，飞机不在舱内，返航失败。");
                returnHomeFailAndNotifyUser(dockSn);
            }
            returnHomeMonitorOpt.ifPresent(x -> {
                droneOsdOpt.ifPresent(y ->
                        x.setDroneModeCodeEnum(x.getDroneModeCodeEnum())
                                .setLongitude(y.getLongitude())
                                .setLatitude(y.getLatitude())
                                .setBatteryCapacityPercent(y.getBattery().getCapacityPercent()));
                waylineRedisService.setReturnHomeMonitor(dockSn, x);
            });
        });
        waylineRedisService.setReturnHomeMonitor(dockSn,
                new DroneReturnHomeMonitor()
                        .setTaskId(taskId)
                        .setDockSn(dockSn)
                        .setDroneSn(droneSn)
                        .setJobId(jobId)
                        .setDroneModeCodeEnum(modeCodeEnum));
    }

    private void returnHomeSuccess(String dockSn) {
        waylineRedisService.getReturnHomeMonitor(dockSn).ifPresent(x -> CronUtil.remove(x.getTaskId()));
        waylineRedisService.delReturnHomeMonitor(dockSn);
    }

    private void returnHomeFailAndNotifyUser(String dockSn) {
        waylineRedisService.getReturnHomeMonitor(dockSn).ifPresent(x -> {
            flightTaskClient.returnHomeFailReport(x);

            CronUtil.remove(x.getTaskId());
            waylineRedisService.delReturnHomeMonitor(dockSn);
        });
    }

    @Transactional(isolation = Isolation.READ_UNCOMMITTED)
    @Override
    public TopicRequestsResponse<MqttReply<FlighttaskResourceGetResponse>> flighttaskResourceGet(TopicRequestsRequest<FlighttaskResourceGetRequest> response, MessageHeaders headers) {
        String jobId = response.getData().getFlightId();

        Optional<DeviceDTO> deviceOpt = deviceRedisService.getDeviceOnline(response.getGateway());
        if (deviceOpt.isEmpty()) {
            log.error("机场已离线，请稍候重试。");
            return new TopicRequestsResponse().setData(MqttReply.error(CommonErrorEnum.DEVICE_OFFLINE));
        }
        Optional<WaylineJobDTO> waylineJobOpt = waylineJobService.getJobByJobId(deviceOpt.get().getWorkspaceId(), jobId);
        if (waylineJobOpt.isEmpty()) {
            log.error("航线飞行计划不存在。");
            return new TopicRequestsResponse().setData(MqttReply.error(CommonErrorEnum.ILLEGAL_ARGUMENT));
        }

        WaylineJobDTO waylineJob = waylineJobOpt.get();

        // get wayline file
        Optional<GetWaylineListResponse> waylineFile = waylineFileService.getWaylineByWaylineId(waylineJob.getWorkspaceId(), waylineJob.getFileId());
        if (waylineFile.isEmpty()) {
            log.error("获取航线文件失败.");
            return new TopicRequestsResponse().setData(MqttReply.error(CommonErrorEnum.ILLEGAL_ARGUMENT));
        }
        // get file url
        try {
            URL url = waylineFileService.getObjectUrl(waylineJob.getWorkspaceId(), waylineFile.get().getId());
            return new TopicRequestsResponse<MqttReply<FlighttaskResourceGetResponse>>().setData(
                    MqttReply.success(new FlighttaskResourceGetResponse()
                            .setFile(new FlighttaskFile()
                                    .setUrl(url.toString())
                                    .setFingerprint(waylineFile.get().getSign()))));
        } catch (SQLException | NullPointerException e) {
            log.error("Failed to get wayline file URL.", e);
            return new TopicRequestsResponse().setData(MqttReply.error(CommonErrorEnum.SYSTEM_ERROR));
        }
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

    /**
     * Return-to-home information
     *
     * @param request data
     * @param headers The headers for a Message.
     * @return events_reply
     */
    @Override
    public TopicEventsResponse<MqttReply> returnHomeInfo(TopicEventsRequest<ReturnHomeInfo> request, MessageHeaders headers) {
        log.error("*************** returnHomeInfo not implemented! ***************");
        log.info("- Return home information: gateway: {}, data: {}", request.getGateway(), request.getData());

        return new TopicEventsResponse<MqttReply>();
    }

    @Override
    public TopicRequestsResponse<MqttReply<FlightTaskProgressGetResponse>> flightTaskProgressGet(TopicRequestsRequest<FlightTaskProgressGetRequest> request, MessageHeaders headers) {
        log.error("*************** flightTaskProgressGet not implemented! ***************");
        log.info("- Return home information: gateway: {}, data: {}", request.getGateway(), request.getData());

        return new TopicRequestsResponse<>();
    }

    @Override
    public HttpResultResponse inFlightWaylineDeliver(CreateInFlightWaylineTask param, CustomClaim customClaim) throws SQLException {
        param.setWorkspaceId(customClaim.getWorkspaceId());
        param.setInFlightWaylineId(UUID.randomUUID().toString());

        boolean isOnline = deviceRedisService.checkDeviceOnline(param.getDockSn());
        if (!isOnline) {
            throw new RuntimeException("机场已离线。");
        }

        boolean isSuccess = pushInFlightWayline(param);
        if (!isSuccess) {
            return HttpResultResponse.error("下发空中航线任务失败。");
        }
        return HttpResultResponse.success(param.getInFlightWaylineId());
    }

    private Boolean pushInFlightWayline(CreateInFlightWaylineTask param) throws SQLException {
        // get wayline file
        Optional<GetWaylineListResponse> waylineFile = waylineFileService.getWaylineByWaylineId(param.getWorkspaceId(), param.getFileId());
        if (waylineFile.isEmpty()) {
            throw new SQLException("无法获取飞行任务的航线文件，请查证。");
        }
        URL url = waylineFileService.getObjectUrl(param.getWorkspaceId(), waylineFile.get().getId());

        InFlightWaylineDeliverRequest flightTask = new InFlightWaylineDeliverRequest()
                .setInFlightWaylineId(param.getInFlightWaylineId())
                .setRthAltitude(param.getRthAltitude())
                .setOutOfControlAction(param.getOutOfControlAction())
                .setExitWaylineWhenRcLost(ExitWaylineWhenRcLostEnum.EXECUTE_RC_LOST_ACTION)
                .setFile(new FlighttaskFile()
                        .setUrl(url.toString())
                        .setFingerprint(waylineFile.get().getSign()))
                .setWaylinePrecisionType(param.getWaylinePrecisionType());
        if (Objects.nonNull(param.getRthMode())) {
            flightTask.setRthMode(param.getRthMode());
        }

        log.info(":: 下发空中航线: {}", flightTask);

        TopicServicesResponse<ServicesReplyData> serviceReply = abstractWaylineService.inFlightWaylineDeliver(
                SDKManager.getDeviceSDK(param.getDockSn()), flightTask);
        if (!serviceReply.getData().getResult().isSuccess()) {
            log.error("InFlightWayline task ====> Error code: {}", serviceReply.getData().getResult());
            return false;
        }
        Optional<InFlightWaylineProgress> runningOpt = waylineRedisService.getRunningInFlightWayline(param.getDockSn());
        if (runningOpt.isEmpty()) {
            waylineRedisService.setRunningInFlightWayline(param.getDockSn(),
                    new InFlightWaylineProgress().setInFlightWaylineId(param.getInFlightWaylineId()));
        }
        return true;
    }

    @Override
    public void updateInFlightWaylineStatus(String workspaceId, String inFlightWaylineId, UpdateInFlightWaylineParam param) {
        Optional<InFlightWaylineProgress> runningJob = waylineRedisService.getRunningInFlightWayline(param.getDockSn());
        if (runningJob.isEmpty()) {
            throw new RuntimeException("操作失败，空中航线任务不存在。");
        }
        InFlightWaylineProgress progress = runningJob.get();
        InFlightWaylineStatusEnum statusEnum = progress.getStatus();
        if (Objects.isNull(statusEnum) || statusEnum.getEnd()) {
            throw new RuntimeException("空中航线任务已结束，不能执行当前操作。");
        }
        switch (param.getStatus()) {
            case STOP:
                pauseInFlightWayline(param.getDockSn(), inFlightWaylineId, statusEnum);
                break;
            case RECOVER:
                recoverInFlightWayline(param.getDockSn(), inFlightWaylineId, statusEnum);
                break;
            case CANCEL:
                cancelInFlightWayline(param.getDockSn(), inFlightWaylineId, statusEnum);
                break;
            default:
                break;
        }
    }

    private void pauseInFlightWayline(String dockSn, String inFlightWaylineId, InFlightWaylineStatusEnum statusEnum) {
        if (statusEnum == InFlightWaylineStatusEnum.WAYLINE_PAUSED) {
            waylineRedisService.setPausedInFlightWayline(dockSn, inFlightWaylineId);
        }
        TopicServicesResponse<ServicesReplyData> reply = abstractWaylineService.inFlightWaylineStop(
                SDKManager.getDeviceSDK(dockSn), new InFlightWaylineRequest().setInFlightWaylineId(inFlightWaylineId));
        if (!reply.getData().getResult().isSuccess()) {
            log.error("inFlightWayline stop ===> Error: {}", reply.getData().getResult());
            throw new RuntimeException("Failed to stop in flight wayline job. Error: " + reply.getData().getResult().getCode());
        }
        waylineRedisService.delRunningInFlightWayline(dockSn);
        waylineRedisService.setPausedInFlightWayline(dockSn, inFlightWaylineId);
    }

    private void recoverInFlightWayline(String dockSn, String inFlightWaylineId, InFlightWaylineStatusEnum statusEnum) {
        Optional<InFlightWaylineProgress> runningOpt = waylineRedisService.getRunningInFlightWayline(dockSn);
        if (InFlightWaylineStatusEnum.WAYLINE_PROGRESS == statusEnum
                && inFlightWaylineId.equals(runningOpt.map(InFlightWaylineProgress::getInFlightWaylineId).get())) {
            waylineRedisService.setRunningInFlightWayline(dockSn, runningOpt.get());
            return;
        }
        TopicServicesResponse<ServicesReplyData> reply = abstractWaylineService.inFlightWaylineRecover(
                SDKManager.getDeviceSDK(dockSn), new InFlightWaylineRequest().setInFlightWaylineId(inFlightWaylineId));
        if (!reply.getData().getResult().isSuccess()) {
            log.error("inFlightWayline recover ===> Error: {}", reply.getData().getResult());
            throw new RuntimeException("Failed to recover in flight wayline job. Error: " + reply.getData().getResult().getCode());
        }
        waylineRedisService.delPausedInFlightWayline(dockSn);
    }

    private void cancelInFlightWayline(String dockSn, String inFlightWaylineId, InFlightWaylineStatusEnum statusEnum) {
        Optional<InFlightWaylineProgress> runningOpt = waylineRedisService.getRunningInFlightWayline(dockSn);
        if (InFlightWaylineStatusEnum.WAYLINE_CANCEL == statusEnum || runningOpt.isEmpty()) {
            return;
        }
        TopicServicesResponse<ServicesReplyData> reply = abstractWaylineService.inFlightWaylineCancel(
                SDKManager.getDeviceSDK(dockSn), new InFlightWaylineRequest().setInFlightWaylineId(inFlightWaylineId));
        if (!reply.getData().getResult().isSuccess()) {
            log.error("inFlightWayline Cancel ===> Error: {}", reply.getData().getResult());
            throw new RuntimeException("Failed to cancel in flight wayline job. Error: " + reply.getData().getResult().getCode());
        }
        waylineRedisService.delPausedInFlightWayline(dockSn);
        waylineRedisService.delRunningInFlightWayline(dockSn);
    }

    @Override
    public TopicEventsResponse<MqttReply> inFlightWaylineProgress(TopicEventsRequest<InFlightWaylineProgress> response, MessageHeaders headers) {
        InFlightWaylineProgress eventData = response.getData();
        log.info("inFlightWaylineProgress: {}", eventData);

        if (MqttReply.CODE_SUCCESS != eventData.getResult()) {
            log.error("inFlightWayline progress ===> Error: {}", eventData.getResult());
        }

        Optional<DeviceDTO> deviceOpt = deviceRedisService.getDeviceOnline(response.getGateway());
        if (deviceOpt.isEmpty()) {
            return new TopicEventsResponse<>();
        }
        waylineRedisService.setRunningInFlightWayline(response.getGateway(), eventData);

        if (eventData.getStatus().getEnd()) {
            waylineRedisService.delRunningInFlightWayline(response.getGateway());
            waylineRedisService.delPausedInFlightWayline(response.getGateway());
        }

        // add by Qfei, report flight task progress.
        this.flightTaskClient.inFlightWaylineProgress(response.getGateway(), response);

        return new TopicEventsResponse<>();
    }
}
