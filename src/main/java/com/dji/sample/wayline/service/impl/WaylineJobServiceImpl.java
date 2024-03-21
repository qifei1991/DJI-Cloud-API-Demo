package com.dji.sample.wayline.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.ClassUtil;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dji.sample.cloudapi.client.FlightTaskClient;
import com.dji.sample.common.error.CommonErrorEnum;
import com.dji.sample.common.model.CustomClaim;
import com.dji.sample.common.model.Pagination;
import com.dji.sample.common.model.PaginationData;
import com.dji.sample.common.model.ResponseResult;
import com.dji.sample.component.mqtt.model.*;
import com.dji.sample.component.mqtt.service.IMessageSenderService;
import com.dji.sample.component.redis.RedisConst;
import com.dji.sample.component.redis.RedisOpsUtils;
import com.dji.sample.control.model.param.DrcModeParam;
import com.dji.sample.control.service.IDrcService;
import com.dji.sample.manage.model.dto.DeviceDTO;
import com.dji.sample.manage.model.enums.DeviceModeCodeEnum;
import com.dji.sample.manage.model.enums.DockModeCodeEnum;
import com.dji.sample.manage.model.receiver.OsdDockReceiver;
import com.dji.sample.manage.model.receiver.OsdSubDeviceReceiver;
import com.dji.sample.manage.service.IDeviceRedisService;
import com.dji.sample.manage.service.IDeviceService;
import com.dji.sample.media.model.MediaFileCountDTO;
import com.dji.sample.media.model.MediaMethodEnum;
import com.dji.sample.media.service.IFileService;
import com.dji.sample.wayline.dao.IWaylineJobMapper;
import com.dji.sample.wayline.model.dto.*;
import com.dji.sample.wayline.model.entity.WaylineJobEntity;
import com.dji.sample.wayline.model.enums.*;
import com.dji.sample.wayline.model.param.CreateJobParam;
import com.dji.sample.wayline.model.param.UpdateJobParam;
import com.dji.sample.wayline.service.IWaylineFileService;
import com.dji.sample.wayline.service.IWaylineJobService;
import com.dji.sample.wayline.service.IWaylineRedisService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.mqtt.support.MqttHeaders;
import org.springframework.messaging.MessageHeaders;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.lang.reflect.Field;
import java.net.URL;
import java.time.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author sean
 * @version 1.1
 * @date 2022/6/1
 */
@Service
@Transactional
@Slf4j
public class WaylineJobServiceImpl implements IWaylineJobService {

    @Autowired
    private IWaylineJobMapper mapper;

    @Autowired
    private IWaylineFileService waylineFileService;

    @Autowired
    private IDeviceService deviceService;

    @Autowired
    private IMessageSenderService messageSender;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private IFileService fileService;

    @Autowired
    private FlightTaskClient flightTaskClient;

    @Autowired
    private IDrcService drcService;

    @Autowired
    private IDeviceRedisService deviceRedisService;

    @Autowired
    private IWaylineRedisService waylineRedisService;

    private Optional<WaylineJobDTO> insertWaylineJob(WaylineJobEntity jobEntity) {
        int id = mapper.insert(jobEntity);
        if (id <= 0) {
            return Optional.empty();
        }
        return Optional.ofNullable(this.entity2Dto(jobEntity));
    }

    @Override
    public Optional<WaylineJobDTO> createWaylineJob(CreateJobParam param, String workspaceId, String username, Long beginTime, Long endTime) {
        if (Objects.isNull(param)) {
            return Optional.empty();
        }
        String jobId = UUID.randomUUID().toString();
        // Immediate tasks, allocating time on the backend.
        WaylineJobEntity jobEntity = WaylineJobEntity.builder()
                .name(param.getName())
                .dockSn(param.getDockSn())
                .fileId(param.getFileId())
                .username(username)
                .workspaceId(workspaceId)
                .jobId(jobId)
                .beginTime(beginTime)
                .endTime(endTime)
                .status(WaylineJobStatusEnum.PENDING.getVal())
                .taskType(param.getTaskType().getVal())
                .waylineType(param.getWaylineType().getVal())
                .outOfControlAction(param.getOutOfControlAction())
                .exitWaylineWhenRcLost(param.getExitWaylineWhenRcLost())
                .rthAltitude(param.getRthAltitude())
                .mediaCount(0)
                // modify by Qfei, 2023-10-10 18:56:22, 新建的任务，将jobId作为groupId.
                .groupId(jobId)
                .continuable(Objects.nonNull(param.getContinuable()) ? param.getContinuable() : Boolean.FALSE)
                .build();

        return insertWaylineJob(jobEntity);
    }

    @Override
    public Optional<WaylineJobDTO> createWaylineJobByParent(String workspaceId, String parentId, Boolean continuable) {
        Optional<WaylineJobDTO> parentJobOpt = this.getJobByJobId(workspaceId, parentId);
        if (parentJobOpt.isEmpty()) {
            return Optional.empty();
        }
        WaylineJobEntity jobEntity = this.dto2Entity(parentJobOpt.get());
        jobEntity.setJobId(UUID.randomUUID().toString());
        jobEntity.setErrorCode(null);
        jobEntity.setCompletedTime(null);
        jobEntity.setExecuteTime(null);
        jobEntity.setStatus(WaylineJobStatusEnum.PENDING.getVal());
        jobEntity.setParentId(parentId);

        // 断点续飞
        if (Boolean.TRUE.equals(continuable)) {
            jobEntity.setName(jobEntity.getName() + "_续飞");
            long beginTime = LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
            jobEntity.setBeginTime(beginTime);
            jobEntity.setEndTime(beginTime);
            jobEntity.setMediaCount(0);
        }

        return this.insertWaylineJob(jobEntity);
    }

    /**
     * For immediate tasks, the server time shall prevail.
     * @param param
     */
    private void fillImmediateTime(CreateJobParam param) {

        long now = System.currentTimeMillis() / 1000;
        param.setTaskDays(Collections.singletonList(now));
        param.setTaskPeriods(Collections.singletonList(Collections.singletonList(now)));
    }

    @Override
    public ResponseResult publishFlightTask(CreateJobParam param, CustomClaim customClaim) {
        if (WaylineTaskTypeEnum.IMMEDIATE == param.getTaskType()) {
            fillImmediateTime(param);
        }

        param.getTaskDays().sort((a, b) -> (int) (a - b));
        param.getTaskPeriods().sort((a, b) -> (int) (a.get(0) - b.get(0)));
        for (Long taskDay : param.getTaskDays()) {
            LocalDate date = LocalDate.ofInstant(Instant.ofEpochSecond(taskDay), ZoneId.systemDefault());
            for (List<Long> taskPeriod : param.getTaskPeriods()) {
                long beginTime = LocalDateTime.of(date, LocalTime.ofInstant(Instant.ofEpochSecond(taskPeriod.get(0)), ZoneId.systemDefault()))
                        .atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
                long endTime = taskPeriod.size() > 1 && Objects.nonNull(taskPeriod.get(1)) ?
                        LocalDateTime.of(date, LocalTime.ofInstant(Instant.ofEpochSecond(taskPeriod.get(1)), ZoneId.systemDefault()))
                                .atZone(ZoneId.systemDefault()).toInstant().toEpochMilli() : beginTime;
                if (WaylineTaskTypeEnum.IMMEDIATE != param.getTaskType() && endTime < (System.currentTimeMillis() / 1000)) {
                    return ResponseResult.error("The task has expired.");
                }
                Optional<WaylineJobDTO> waylineJobOpt = this.createWaylineJob(param, customClaim.getWorkspaceId(),
                        customClaim.getUsername(), beginTime, endTime);
                if (waylineJobOpt.isEmpty()) {
                    return ResponseResult.error("Failed to create wayline job.");
                }

                WaylineJobDTO waylineJob = waylineJobOpt.get();
                if (WaylineTaskTypeEnum.IMMEDIATE == param.getTaskType()) {
                    return this.publishOneFlightTask(waylineJob);
                }

                // If it is a conditional task type, add conditions to the job parameters.
                addPreparedJob(waylineJob, param, beginTime, endTime);
            }
        }
        return ResponseResult.success();
    }

    private void addPreparedJob(WaylineJobDTO waylineJob, CreateJobParam param, Long beginTime, Long endTime) {
        if (WaylineTaskTypeEnum.CONDITION == param.getTaskType()) {
            waylineJob.setConditions(
                    WaylineTaskConditionDTO.builder()
                            .executableConditions(Objects.nonNull(param.getMinStorageCapacity()) ?
                                    WaylineTaskExecutableConditionDTO.builder().storageCapacity(param.getMinStorageCapacity()).build() : null)
                            .readyConditions(WaylineTaskReadyConditionDTO.builder()
                                    .batteryCapacity(param.getMinBatteryCapacity())
                                    .beginTime(beginTime)
                                    .endTime(endTime)
                                    .build())
                            .build());

            waylineRedisService.setConditionalWaylineJob(waylineJob);
        }
        // value: {workspace_id}:{dock_sn}:{job_id}
        boolean isAdd = waylineRedisService.addPreparedWaylineJob(waylineJob);
        if (!isAdd) {
            throw new RuntimeException("Failed to create prepare job.");
        }
    }

    @Override
    public ResponseResult publishOneFlightTask(WaylineJobDTO waylineJob) {

        boolean isSuccess = this.prepareFlightTask(waylineJob);
        if (!isSuccess) {
            return ResponseResult.error("飞行任务下发准备失败.");
        }

        // Issue an immediate task execution command.
        if (WaylineTaskTypeEnum.IMMEDIATE == waylineJob.getTaskType()) {
            boolean isExecuted = executeFlightTask(waylineJob.getWorkspaceId(), waylineJob.getJobId());
            if (!isExecuted) {
                return ResponseResult.error("飞行任务执行失败.");
            }
        }
        return ResponseResult.success();
    }

    private Boolean prepareFlightTask(WaylineJobDTO waylineJob) {

        boolean isOnline = deviceRedisService.checkDeviceOnline(waylineJob.getDockSn());
        if (!isOnline) {
            throw new RuntimeException("机场离线，任务下发失败。");
        }

        // get wayline file
        Optional<WaylineFileDTO> waylineFile = waylineFileService.getWaylineByWaylineId(waylineJob.getWorkspaceId(), waylineJob.getFileId());
        if (waylineFile.isEmpty()) {
            throw new RuntimeException("无法获取飞行任务的航线文件，请查证。");
        }

        // get file url
        URL url = waylineFileService.getObjectUrl(waylineJob.getWorkspaceId(), waylineFile.get().getWaylineId());

        WaylineTaskCreateDTO flightTask = WaylineTaskCreateDTO.builder()
                .flightId(waylineJob.getJobId())
                .executeTime(waylineJob.getBeginTime().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli())
                .taskType(waylineJob.getTaskType())
                .waylineType(waylineJob.getWaylineType())
                .rthAltitude(waylineJob.getRthAltitude())
                .outOfControlAction(waylineJob.getOutOfControlAction())
                .exitWaylineWhenRcLost(waylineJob.getExitWaylineWhenRcLost())
                .file(WaylineTaskFileDTO.builder()
                        .url(url.toString())
                        .fingerprint(waylineFile.get().getSign())
                        .build())
                .build();

        if (WaylineTaskTypeEnum.CONDITION == waylineJob.getTaskType()) {
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

        ServiceReply serviceReply = messageSender.publishServicesTopic(
                waylineJob.getDockSn(), WaylineMethodEnum.FLIGHT_TASK_PREPARE.getMethod(), flightTask, waylineJob.getJobId());
        if (ResponseResult.CODE_SUCCESS != serviceReply.getResult()) {
            log.info("Prepare task ====> Error code: {}", serviceReply.getResult());
            this.updateJob(WaylineJobDTO.builder()
                    .workspaceId(waylineJob.getWorkspaceId())
                    .jobId(waylineJob.getJobId())
                    .executeTime(LocalDateTime.now())
                    .status(WaylineJobStatusEnum.FAILED.getVal())
                    .completedTime(LocalDateTime.now())
                    .code(serviceReply.getResult()).build());
            return false;
        }
        return true;
    }

    @Override
    public Boolean executeFlightTask(String workspaceId, String jobId) {
        // get job
        Optional<WaylineJobDTO> waylineJob = this.getJobByJobId(workspaceId, jobId);
        if (waylineJob.isEmpty()) {
            throw new IllegalArgumentException("飞行任务不存在.");
        }

        boolean isOnline = deviceRedisService.checkDeviceOnline(waylineJob.get().getDockSn());
        if (!isOnline) {
            throw new RuntimeException("机场离线状态，执行失败.");
        }

        WaylineJobDTO job = waylineJob.get();
        WaylineTaskCreateDTO flightTask = WaylineTaskCreateDTO.builder().flightId(jobId).build();

        ServiceReply serviceReply = messageSender.publishServicesTopic(
                job.getDockSn(), WaylineMethodEnum.FLIGHT_TASK_EXECUTE.getMethod(), flightTask, jobId);
        if (ResponseResult.CODE_SUCCESS != serviceReply.getResult()) {
            log.info("Execute job ====> Error code: {}", serviceReply.getResult());
            this.updateJob(WaylineJobDTO.builder()
                    .jobId(jobId)
                    .executeTime(LocalDateTime.now())
                    .status(WaylineJobStatusEnum.FAILED.getVal())
                    .completedTime(LocalDateTime.now())
                    .code(serviceReply.getResult()).build());
            // The conditional task fails and enters the blocking status.
            if (WaylineTaskTypeEnum.CONDITION == job.getTaskType()
                    && WaylineErrorCodeEnum.find(serviceReply.getResult()).isBlock()) {
                waylineRedisService.setBlockedWaylineJob(job.getDockSn(), jobId);
            }
            return false;
        }

        this.updateJob(WaylineJobDTO.builder()
                .jobId(jobId)
                .executeTime(LocalDateTime.now())
                .status(WaylineJobStatusEnum.IN_PROGRESS.getVal())
                .build());
        waylineRedisService.setRunningWaylineJob(job.getDockSn(), EventsReceiver.<WaylineTaskProgressReceiver>builder().bid(jobId).sn(job.getDockSn()).build());

        // add by Qfei, report start a wayline job.
        this.flightTaskClient.startFlightTask(job);

        return true;
    }

    @Override
    public void cancelFlightTask(String workspaceId, Collection<String> jobIds) {
        List<WaylineJobDTO> waylineJobs = getJobsByConditions(workspaceId, jobIds, WaylineJobStatusEnum.PENDING);

        Set<String> waylineJobIds = waylineJobs.stream().map(WaylineJobDTO::getJobId).collect(Collectors.toSet());
        // Check if the task status is correct.
        boolean isErr = !jobIds.removeAll(waylineJobIds) || !jobIds.isEmpty() ;
        if (isErr) {
            List<WaylineJobDTO> cannotCancelJobs = getJobsByConditions(workspaceId, jobIds, null);
            throw new IllegalArgumentException("以下任务的状态不支持取消，请排除后重新操作！" + Arrays.toString(cannotCancelJobs.stream().map(WaylineJobDTO::getJobName).toArray()));
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
            throw new RuntimeException("机场离线!");
        }

        ServiceReply serviceReply = messageSender.publishServicesTopic(
                dockSn, WaylineMethodEnum.FLIGHT_TASK_CANCEL.getMethod(), Map.of(MapKeyConst.FLIGHT_IDS, jobIds));
        if (ResponseResult.CODE_SUCCESS != serviceReply.getResult()) {
            log.info("Cancel job ====> Error code: {}", serviceReply.getResult());
            throw new RuntimeException("取消飞行计划失败");
        }

        for (String jobId : jobIds) {
            this.updateJob(WaylineJobDTO.builder()
                    .workspaceId(workspaceId)
                    .jobId(jobId)
                    .status(WaylineJobStatusEnum.CANCEL.getVal())
                    .completedTime(LocalDateTime.now())
                    .build());
        }
    }

    @Override
    public List<WaylineJobDTO> getJobsByConditions(String workspaceId, Collection<String> jobIds, WaylineJobStatusEnum status) {
        return mapper.selectList(
                new LambdaQueryWrapper<WaylineJobEntity>()
                        .eq(WaylineJobEntity::getWorkspaceId, workspaceId)
                        .eq(Objects.nonNull(status), WaylineJobEntity::getStatus, status.getVal())
                        .in(!CollectionUtils.isEmpty(jobIds), WaylineJobEntity::getJobId, jobIds))
                .stream()
                .map(this::entity2Dto)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<WaylineJobDTO> getJobByJobId(String workspaceId, String jobId) {
        WaylineJobEntity jobEntity = mapper.selectOne(
                new LambdaQueryWrapper<WaylineJobEntity>()
                        .eq(WaylineJobEntity::getWorkspaceId, workspaceId)
                        .eq(WaylineJobEntity::getJobId, jobId));
        return Optional.ofNullable(entity2Dto(jobEntity));
    }

    @Override
    public Boolean updateJob(WaylineJobDTO dto) {
        return mapper.update(this.dto2Entity(dto),
                new LambdaUpdateWrapper<WaylineJobEntity>()
                        .eq(WaylineJobEntity::getJobId, dto.getJobId())) > 0;
    }

    @Override
    public PaginationData<WaylineJobDTO> getJobsByWorkspaceId(String workspaceId, long page, long pageSize, String dockSn,
            String name, Integer taskType, List<Integer> status, Long beginTime, Long endTime, String orderField, String isAsc) {

        Field field = ClassUtil.getDeclaredField(WaylineJobEntity.class, orderField);
        QueryWrapper<WaylineJobEntity> queryWrapper = new QueryWrapper<>();
        if (Objects.nonNull(field)) {
            TableField annotation = field.getAnnotation(TableField.class);
            boolean notExit = Objects.nonNull(annotation) && !annotation.exist();
            String columnName = Objects.isNull(annotation) || !StringUtils.hasText(annotation.value()) ? field.getName() : annotation.value();
            queryWrapper.orderBy(!notExit, Boolean.getBoolean(isAsc), columnName);
        }
        LambdaQueryWrapper<WaylineJobEntity> lambdaQueryWrapper = queryWrapper
                .lambda()
                .eq(WaylineJobEntity::getWorkspaceId, workspaceId)
                .eq(CharSequenceUtil.isNotBlank(dockSn), WaylineJobEntity::getDockSn, dockSn)
                .like(CharSequenceUtil.isNotBlank(name), WaylineJobEntity::getName, name)
                .in(CollUtil.isNotEmpty(status), WaylineJobEntity::getStatus, status)
                .eq(Objects.nonNull(taskType), WaylineJobEntity::getTaskType, taskType)
                .ge(Objects.nonNull(beginTime), WaylineJobEntity::getBeginTime, beginTime)
                .le(Objects.nonNull(endTime), WaylineJobEntity::getBeginTime, endTime);
        if (!StringUtils.hasText(orderField)) {
            lambdaQueryWrapper.orderByDesc(WaylineJobEntity::getBeginTime);
        }

        Page<WaylineJobEntity> pageData = mapper.selectPage(new Page<>(page, pageSize), lambdaQueryWrapper);
        List<WaylineJobDTO> records = pageData.getRecords()
                .stream()
                .map(this::entity2Dto)
                .collect(Collectors.toList());

        return new PaginationData<WaylineJobDTO>(records, new Pagination(pageData));
    }

    @Override
    public Optional<WaylineJobDTO> getDockExecutingJob(String workspaceId, String dockSn) {
        return Optional.ofNullable(this.entity2Dto(mapper.selectOne(new LambdaQueryWrapper<WaylineJobEntity>()
                .eq(WaylineJobEntity::getWorkspaceId, workspaceId)
                .eq(WaylineJobEntity::getDockSn, dockSn)
                .eq(WaylineJobEntity::getStatus, WaylineJobStatusEnum.IN_PROGRESS.getVal())
                .orderByDesc(WaylineJobEntity::getCreateTime))));
    }

    @Override
    public List<WaylineJobDTO> getRemainingJobs(String workspaceId) {
        return mapper.selectList(new LambdaQueryWrapper<WaylineJobEntity>()
                        .eq(WaylineJobEntity::getWorkspaceId, workspaceId)
                        .eq(WaylineJobEntity::getStatus, WaylineJobStatusEnum.PENDING.getVal())
                        .ge(WaylineJobEntity::getBeginTime, System.currentTimeMillis()))
                .stream()
                .map(this::entity2Dto)
                .collect(Collectors.toList());
    }

    @Override
    @ServiceActivator(inputChannel = ChannelName.INBOUND_REQUESTS_FLIGHT_TASK_RESOURCE_GET, outputChannel = ChannelName.OUTBOUND)
//    @Transactional(isolation = Isolation.READ_UNCOMMITTED)
    public void flightTaskResourceGet(CommonTopicReceiver receiver, MessageHeaders headers) {
        Map<String, String> jobIdMap = objectMapper.convertValue(receiver.getData(), new TypeReference<Map<String, String>>() {});
        String jobId = jobIdMap.get(MapKeyConst.FLIGHT_ID);

        CommonTopicResponse.CommonTopicResponseBuilder<RequestsReply> builder = CommonTopicResponse.<RequestsReply>builder()
                .tid(receiver.getTid())
                .bid(receiver.getBid())
                .method(RequestsMethodEnum.FLIGHT_TASK_RESOURCE_GET.getMethod())
                .timestamp(System.currentTimeMillis());

        String topic = headers.get(MqttHeaders.RECEIVED_TOPIC).toString() + TopicConst._REPLY_SUF;

        Optional<DeviceDTO> deviceOpt = deviceRedisService.getDeviceOnline(receiver.getGateway());
        if (deviceOpt.isEmpty()) {
            return;
        }
        Optional<WaylineJobDTO> waylineJobOpt = this.getJobByJobId(deviceOpt.get().getWorkspaceId(), jobId);
        if (waylineJobOpt.isEmpty()) {
            builder.data(RequestsReply.error(CommonErrorEnum.ILLEGAL_ARGUMENT));
            messageSender.publish(topic, builder.build());
            return;
        }

        WaylineJobDTO waylineJob = waylineJobOpt.get();

        // get wayline file
        Optional<WaylineFileDTO> waylineFile = waylineFileService.getWaylineByWaylineId(waylineJob.getWorkspaceId(), waylineJob.getFileId());
        if (waylineFile.isEmpty()) {
            builder.data(RequestsReply.error(CommonErrorEnum.ILLEGAL_ARGUMENT));
            messageSender.publish(topic, builder.build());
            return;
        }

        // get file url
        URL url = null;
        try {
            url = waylineFileService.getObjectUrl(waylineJob.getWorkspaceId(), waylineFile.get().getWaylineId());
            builder.data(RequestsReply.success(WaylineTaskCreateDTO.builder()
                    .file(WaylineTaskFileDTO.builder()
                            .url(url.toString())
                            .fingerprint(waylineFile.get().getSign())
                            .build())
                    .build()));

        } catch (NullPointerException e) {
            e.printStackTrace();
            builder.data(RequestsReply.error(CommonErrorEnum.ILLEGAL_ARGUMENT));
            messageSender.publish(topic, builder.build());
            return;
        }

        messageSender.publish(topic, builder.build());

    }

    @Override
    public void uploadMediaHighestPriority(String workspaceId, String jobId) {
        Optional<WaylineJobDTO> jobOpt = getJobByJobId(workspaceId, jobId);
        if (jobOpt.isEmpty()) {
            throw new RuntimeException(CommonErrorEnum.ILLEGAL_ARGUMENT.getErrorMsg());
        }

        String dockSn = jobOpt.get().getDockSn();
        String key = RedisConst.MEDIA_HIGHEST_PRIORITY_PREFIX + dockSn;
        if (RedisOpsUtils.checkExist(key) && jobId.equals(((MediaFileCountDTO) RedisOpsUtils.get(key)).getJobId())) {
            return;
        }

        ServiceReply reply = messageSender.publishServicesTopic(dockSn,
                MediaMethodEnum.UPLOAD_FLIGHT_TASK_MEDIA_PRIORITIZE.getMethod(),
                Map.of(MapKeyConst.FLIGHT_ID, jobId));
        if (ResponseResult.CODE_SUCCESS != reply.getResult()) {
            throw new RuntimeException("设置任务媒体文件优先上传失败, 错误码: " + reply.getResult());
        }
    }

    private WaylineJobEntity dto2Entity(WaylineJobDTO dto) {
        WaylineJobEntity.WaylineJobEntityBuilder builder = WaylineJobEntity.builder();
        if (dto == null) {
            return builder.build();
        }
        if (Objects.nonNull(dto.getBeginTime())) {
            builder.beginTime(dto.getBeginTime().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli());
        }
        if (Objects.nonNull(dto.getEndTime())) {
            builder.endTime(dto.getEndTime().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli());
        }
        if (Objects.nonNull(dto.getExecuteTime())) {
            builder.executeTime(dto.getExecuteTime().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli());
        }
        if (Objects.nonNull(dto.getCompletedTime())) {
            builder.completedTime(dto.getCompletedTime().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli());
        }
        return builder.status(dto.getStatus())
                .mediaCount(dto.getMediaCount())
                .name(dto.getJobName())
                .errorCode(dto.getCode())
                .jobId(dto.getJobId())
                .fileId(dto.getFileId())
                .dockSn(dto.getDockSn())
                .workspaceId(dto.getWorkspaceId())
                .taskType(Optional.ofNullable(dto.getTaskType()).map(WaylineTaskTypeEnum::getVal).orElse(null))
                .waylineType(Optional.ofNullable(dto.getWaylineType()).map(WaylineTemplateTypeEnum::getVal).orElse(null))
                .username(dto.getUsername())
                .rthAltitude(dto.getRthAltitude())
                .outOfControlAction(dto.getOutOfControlAction())
                .exitWaylineWhenRcLost(dto.getExitWaylineWhenRcLost())
                .parentId(dto.getParentId())
                .groupId(dto.getGroupId())
                .continuable(dto.getContinuable())
                .build();
    }

    @Override
    public void updateJobStatus(String workspaceId, String jobId, UpdateJobParam param) {
        Optional<WaylineJobDTO> waylineJobOpt = this.getJobByJobId(workspaceId, jobId);
        if (waylineJobOpt.isEmpty()) {
            throw new RuntimeException("操作失败，飞行计划不存在。");
        }
        WaylineJobDTO waylineJob = waylineJobOpt.get();
        WaylineJobStatusEnum statusEnum = this.getWaylineState(waylineJob.getDockSn());
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
                throw new IllegalArgumentException("更新任务失败");
        }
    }

    @Override
    public ResponseResult breakPointContinueFlight(String workspaceId, String jobId) {

        Optional<WaylineTaskBreakPointReceiver> breakPointReceiver = waylineRedisService.getBreakPointReceiver(jobId);
        if (breakPointReceiver.isEmpty()) {
            return ResponseResult.error("无法获取航线断点信息，无法继续飞行。");
        }
        Optional<WaylineJobDTO> waylineJob = createWaylineJobByParent(workspaceId, jobId, true);
        if (waylineJob.isEmpty()) {
            return ResponseResult.error("创建断点飞行任务失败。");
        }

        if (!this.prepareFlightTask(waylineJob.get())) {
            deleteJob(workspaceId, jobId);
            return ResponseResult.error("飞行任务下发失败。");
        }
        // Issue an immediate task execution command.
        if (!executeFlightTask(waylineJob.get().getWorkspaceId(), waylineJob.get().getJobId())) {
            // 断点续飞任务如果失败,删除重新从父节点下发继续飞行的任务
            deleteJob(workspaceId, jobId);
            return ResponseResult.error("飞行任务执行失败。");
        }
        // 执行成功，需要将父节点任务执行状态修改为ok
        this.updateJob(WaylineJobDTO.builder()
                .workspaceId(workspaceId)
                .jobId(waylineJob.get().getParentId())
                .status(WaylineJobStatusEnum.SUCCESS.getVal())
                .build());

        return ResponseResult.success();
    }

    private void deleteJob(String workspaceId, String jobId) {
        this.mapper.delete(
                new LambdaQueryWrapper<WaylineJobEntity>()
                        .eq(WaylineJobEntity::getWorkspaceId, workspaceId)
                        .eq(WaylineJobEntity::getJobId, jobId));
    }


    @Override
    public WaylineJobStatusEnum getWaylineState(String dockSn) {
        Optional<DeviceDTO> dockOpt = deviceRedisService.getDeviceOnline(dockSn);
        if (dockOpt.isEmpty() || !StringUtils.hasText(dockOpt.get().getChildDeviceSn())) {
            return WaylineJobStatusEnum.UNKNOWN;
        }
        Optional<OsdDockReceiver> dockOsdOpt = deviceRedisService.getDeviceOsd(dockSn, OsdDockReceiver.class);
        Optional<OsdSubDeviceReceiver> deviceOsdOpt = deviceRedisService.getDeviceOsd(dockOpt.get().getChildDeviceSn(), OsdSubDeviceReceiver.class);
        if (dockOsdOpt.isEmpty() || deviceOsdOpt.isEmpty() || DockModeCodeEnum.WORKING != dockOsdOpt.get().getModeCode()) {
            return WaylineJobStatusEnum.UNKNOWN;
        }

        OsdSubDeviceReceiver osdDevice = deviceOsdOpt.get();
        if (DeviceModeCodeEnum.WAYLINE == osdDevice.getModeCode()
                || DeviceModeCodeEnum.MANUAL == osdDevice.getModeCode()
                || DeviceModeCodeEnum.TAKEOFF_AUTO == osdDevice.getModeCode()) {
            if (StringUtils.hasText(waylineRedisService.getPausedWaylineJobId(dockSn))) {
                return WaylineJobStatusEnum.PAUSED;
            }
            if (waylineRedisService.getRunningWaylineJob(dockSn).isPresent()) {
                return WaylineJobStatusEnum.IN_PROGRESS;
            }
        }
        return WaylineJobStatusEnum.UNKNOWN;
    }

    private void pauseJob(String workspaceId, String dockSn, String jobId, WaylineJobStatusEnum statusEnum) {
        if (WaylineJobStatusEnum.PAUSED == statusEnum && jobId.equals(waylineRedisService.getPausedWaylineJobId(dockSn))) {
            waylineRedisService.setPausedWaylineJob(dockSn, jobId);
            return;
        }

        ServiceReply reply = messageSender.publishServicesTopic(
                dockSn, WaylineMethodEnum.FLIGHT_TASK_PAUSE.getMethod(), "", jobId);
        if (ResponseResult.CODE_SUCCESS != reply.getResult()) {
            throw new RuntimeException("Failed to pause wayline job. Error Code: " + reply.getResult());
        }
        waylineRedisService.delRunningWaylineJob(dockSn);
        waylineRedisService.setPausedWaylineJob(dockSn, jobId);
    }

    private void resumeJob(String workspaceId, String dockSn, String jobId, WaylineJobStatusEnum statusEnum) {
        Optional<EventsReceiver<WaylineTaskProgressReceiver>> runningDataOpt = waylineRedisService.getRunningWaylineJob(dockSn);
        if (WaylineJobStatusEnum.IN_PROGRESS == statusEnum && jobId.equals(runningDataOpt.map(EventsReceiver::getSn).get())) {
            waylineRedisService.setRunningWaylineJob(dockSn, runningDataOpt.get());
            return;
        }
        ServiceReply reply = messageSender.publishServicesTopic(
                dockSn, WaylineMethodEnum.FLIGHT_TASK_RESUME.getMethod(), "", jobId);
        if (ResponseResult.CODE_SUCCESS != reply.getResult()) {
            throw new RuntimeException("Failed to resume wayline job. Error Code: " + reply.getResult());
        }

        runningDataOpt.ifPresent(runningData -> waylineRedisService.setRunningWaylineJob(dockSn, runningData));
        waylineRedisService.delPausedWaylineJob(dockSn);

        if (deviceService.checkDockDrcMode(dockSn)) {
            drcService.deviceDrcExit(workspaceId, DrcModeParam.builder().dockSn(dockSn)
                    .clientId(drcService.getDrcModeInRedis(dockSn)).build());
        }

    }

    private WaylineJobDTO entity2Dto(WaylineJobEntity entity) {
        if (entity == null) {
            return null;
        }

        WaylineJobDTO.WaylineJobDTOBuilder builder = WaylineJobDTO.builder()
                .jobId(entity.getJobId())
                .jobName(entity.getName())
                .fileId(entity.getFileId())
                .fileName(waylineFileService.getWaylineByWaylineId(entity.getWorkspaceId(), entity.getFileId())
                        .orElse(WaylineFileDTO.builder().build()).getName())
                .dockSn(entity.getDockSn())
                .dockName(deviceService.getDeviceBySn(entity.getDockSn())
                        .orElse(DeviceDTO.builder().build()).getNickname())
                .username(entity.getUsername())
                .workspaceId(entity.getWorkspaceId())
                .status(WaylineJobStatusEnum.IN_PROGRESS.getVal() == entity.getStatus() &&
                        entity.getJobId().equals(waylineRedisService.getPausedWaylineJobId(entity.getDockSn())) ?
                                WaylineJobStatusEnum.PAUSED.getVal() : entity.getStatus())
                .code(entity.getErrorCode())
                .beginTime(LocalDateTime.ofInstant(Instant.ofEpochMilli(entity.getBeginTime()), ZoneId.systemDefault()))
                .endTime(Objects.nonNull(entity.getEndTime()) ?
                        LocalDateTime.ofInstant(Instant.ofEpochMilli(entity.getEndTime()), ZoneId.systemDefault()) : null)
                .executeTime(Objects.nonNull(entity.getExecuteTime()) ?
                        LocalDateTime.ofInstant(Instant.ofEpochMilli(entity.getExecuteTime()), ZoneId.systemDefault()) : null)
                .completedTime(WaylineJobStatusEnum.find(entity.getStatus()).getEnd() ?
                        LocalDateTime.ofInstant(Instant.ofEpochMilli(entity.getUpdateTime()), ZoneId.systemDefault()) : null)
                .taskType(WaylineTaskTypeEnum.find(entity.getTaskType()))
                .waylineType(WaylineTemplateTypeEnum.find(entity.getWaylineType()))
                .rthAltitude(entity.getRthAltitude())
                .outOfControlAction(entity.getOutOfControlAction())
                .exitWaylineWhenRcLost(entity.getExitWaylineWhenRcLost())
                .mediaCount(entity.getMediaCount())
                .parentId(entity.getParentId())
                .groupId(entity.getGroupId())
                .continuable(entity.getContinuable());

        if (Objects.nonNull(entity.getEndTime())) {
            builder.endTime(LocalDateTime.ofInstant(Instant.ofEpochMilli(entity.getEndTime()), ZoneId.systemDefault()));
        }
        if (WaylineJobStatusEnum.IN_PROGRESS.getVal() == entity.getStatus()) {
            builder.progress(waylineRedisService.getRunningWaylineJob(entity.getDockSn())
                    .map(EventsReceiver::getOutput)
                    .map(WaylineTaskProgressReceiver::getProgress)
                    .map(WaylineTaskProgress::getPercent)
                    .orElse(null));
        }

        // modify by Qfei, 2023-10-11 09:50:15
        if (StringUtils.hasText(entity.getParentId()) && entity.getContinuable()) {
            Optional<WaylineTaskBreakPointReceiver> breakPointReceiver = waylineRedisService.getBreakPointReceiver(entity.getParentId());
            breakPointReceiver.ifPresent(x -> builder.breakPoint(
                    WaylineTaskBreakPoint.builder()
                            .index(x.getIndex())
                            .state(x.getState())
                            .progress(x.getProgress())
                            .waylineId(x.getWaylineId())
                            .build()));
        }

        if (entity.getMediaCount() == 0) {
            return builder.build();
        }

        // sync the number of media files
        String key = RedisConst.MEDIA_HIGHEST_PRIORITY_PREFIX + entity.getDockSn();
        String countKey = RedisConst.MEDIA_FILE_PREFIX + entity.getDockSn();
        Object mediaFileCount = RedisOpsUtils.hashGet(countKey, entity.getJobId());
        if (Objects.nonNull(mediaFileCount)) {
            builder.uploadedCount(((MediaFileCountDTO) mediaFileCount).getUploadedCount())
                   .uploading(RedisOpsUtils.checkExist(key) && entity.getJobId().equals(((MediaFileCountDTO)RedisOpsUtils.get(key)).getJobId()));
            return builder.build();
        }

        int uploadedSize = fileService.getFilesByWorkspaceAndJobId(entity.getWorkspaceId(), entity.getJobId()).size();
        // All media for this job have been uploaded.
        if (uploadedSize >= entity.getMediaCount()) {
            return builder.uploadedCount(uploadedSize).build();
        }
        RedisOpsUtils.hashSet(countKey, entity.getJobId(),
                MediaFileCountDTO.builder()
                        .jobId(entity.getJobId())
                        .mediaCount(entity.getMediaCount())
                        .uploadedCount(uploadedSize).build());
        return builder.build();
    }

}
