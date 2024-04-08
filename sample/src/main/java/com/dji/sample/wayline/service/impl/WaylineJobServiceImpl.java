package com.dji.sample.wayline.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.PatternPool;
import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.ClassUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dji.sample.component.mqtt.model.EventsReceiver;
import com.dji.sample.component.redis.RedisConst;
import com.dji.sample.component.redis.RedisOpsUtils;
import com.dji.sample.manage.model.dto.DeviceDTO;
import com.dji.sample.manage.service.IDeviceRedisService;
import com.dji.sample.manage.service.IDeviceService;
import com.dji.sample.media.model.MediaFileCountDTO;
import com.dji.sample.media.service.IFileService;
import com.dji.sample.wayline.dao.IWaylineJobMapper;
import com.dji.sample.wayline.model.dto.WaylineJobDTO;
import com.dji.sample.wayline.model.entity.WaylineJobEntity;
import com.dji.sample.wayline.model.enums.WaylineJobStatusEnum;
import com.dji.sample.wayline.model.param.CreateJobParam;
import com.dji.sample.wayline.service.IWaylineFileService;
import com.dji.sample.wayline.service.IWaylineJobService;
import com.dji.sample.wayline.service.IWaylineRedisService;
import com.dji.sdk.cloudapi.device.DockModeCodeEnum;
import com.dji.sdk.cloudapi.device.DroneModeCodeEnum;
import com.dji.sdk.cloudapi.device.OsdDock;
import com.dji.sdk.cloudapi.device.OsdDockDrone;
import com.dji.sdk.cloudapi.wayline.*;
import com.dji.sdk.common.Pagination;
import com.dji.sdk.common.PaginationData;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.lang.reflect.Field;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
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

    private static final String JOB_NAME_CONTINUE_SUF = "_续飞";

    private static final Pattern JOB_NAME_PATTERN = PatternPool.get("\\s*_续飞([0-9]*)$");

    @Autowired
    private IWaylineJobMapper mapper;

    @Autowired
    private IWaylineFileService waylineFileService;

    @Autowired
    private IDeviceService deviceService;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private IFileService fileService;

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
                .taskType(param.getTaskType().getType())
                .waylineType(param.getWaylineType().getValue())
                .outOfControlAction(param.getOutOfControlAction().getAction())
                .rthAltitude(param.getRthAltitude())
                .mediaCount(0)
                // modify by Qfei, 2023-10-10 18:56:22, 新建的任务，将jobId作为groupId.
                .exitWaylineWhenRcLost(param.getExitWaylineWhenRcLost())
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
            jobEntity.setName(buildJobContinueName(jobEntity.getName()));
            long beginTime = LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
            jobEntity.setBeginTime(beginTime);
            jobEntity.setEndTime(beginTime);
            jobEntity.setMediaCount(0);
        }

        return this.insertWaylineJob(jobEntity);
    }

    public String buildJobContinueName(String oldName) {
        Matcher matcher = JOB_NAME_PATTERN.matcher(oldName);
        String jobName = oldName;
        int num = 1;
        if (matcher.find() && matcher.groupCount() > 0) {
            num = Integer.valueOf(matcher.group(1)) + 1;
            jobName = StrUtil.removeSuffix(jobName, matcher.group());
        }
        return jobName + JOB_NAME_CONTINUE_SUF + num;
    }

    @Override
    public List<WaylineJobDTO> getJobsByConditions(String workspaceId, Collection<String> jobIds, WaylineJobStatusEnum status) {
        return mapper.selectList(
                new LambdaQueryWrapper<WaylineJobEntity>()
                        .eq(WaylineJobEntity::getWorkspaceId, workspaceId)
                        .eq(Objects.nonNull(status), WaylineJobEntity::getStatus, status.getVal())
                        .and(!CollectionUtils.isEmpty(jobIds),
                                wrapper -> jobIds.forEach(id -> wrapper.eq(WaylineJobEntity::getJobId, id).or())))
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
        Page<WaylineJobEntity> pageData = mapper.selectPage(new Page<WaylineJobEntity>(page, pageSize), lambdaQueryWrapper);
        List<WaylineJobDTO> records = pageData.getRecords()
                .stream()
                .map(this::entity2Dto)
                .collect(Collectors.toList());

        return new PaginationData<WaylineJobDTO>(records, new Pagination(pageData.getCurrent(), pageData.getSize(), pageData.getTotal()));
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
                .taskType(Optional.ofNullable(dto.getTaskType()).map(TaskTypeEnum::getType).orElse(null))
                .waylineType(Optional.ofNullable(dto.getWaylineType()).map(WaylineTypeEnum::getValue).orElse(null))
                .username(dto.getUsername())
                .rthAltitude(dto.getRthAltitude())
                .outOfControlAction(Optional.ofNullable(dto.getOutOfControlAction())
                        .map(OutOfControlActionEnum::getAction).orElse(null))
                .exitWaylineWhenRcLost(dto.getExitWaylineWhenRcLost())
                .parentId(dto.getParentId())
                .groupId(dto.getGroupId())
                .continuable(dto.getContinuable())
                .build();
    }

    @Override
    public WaylineJobStatusEnum getWaylineState(String dockSn) {
        Optional<DeviceDTO> dockOpt = deviceRedisService.getDeviceOnline(dockSn);
        if (dockOpt.isEmpty() || !StringUtils.hasText(dockOpt.get().getChildDeviceSn())) {
            return WaylineJobStatusEnum.UNKNOWN;
        }
        Optional<OsdDock> dockOsdOpt = deviceRedisService.getDeviceOsd(dockSn, OsdDock.class);
        Optional<OsdDockDrone> deviceOsdOpt = deviceRedisService.getDeviceOsd(dockOpt.get().getChildDeviceSn(), OsdDockDrone.class);
        if (dockOsdOpt.isEmpty() || deviceOsdOpt.isEmpty() || DockModeCodeEnum.WORKING != dockOsdOpt.get().getModeCode()) {
            return WaylineJobStatusEnum.UNKNOWN;
        }

        OsdDockDrone osdDevice = deviceOsdOpt.get();
        if (DroneModeCodeEnum.WAYLINE == osdDevice.getModeCode()
                || DroneModeCodeEnum.MANUAL == osdDevice.getModeCode()
                || DroneModeCodeEnum.TAKEOFF_AUTO == osdDevice.getModeCode()) {
            if (StringUtils.hasText(waylineRedisService.getPausedWaylineJobId(dockSn))) {
                return WaylineJobStatusEnum.PAUSED;
            }
            if (waylineRedisService.getRunningWaylineJob(dockSn).isPresent()) {
                return WaylineJobStatusEnum.IN_PROGRESS;
            }
        }
        return WaylineJobStatusEnum.UNKNOWN;
    }

    @Override
    public void deleteJob(String workspaceId, String jobId) {
        this.mapper.delete(
                new LambdaQueryWrapper<WaylineJobEntity>()
                        .eq(WaylineJobEntity::getWorkspaceId, workspaceId)
                        .eq(WaylineJobEntity::getJobId, jobId));
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
                        .orElse(new GetWaylineListResponse()).getName())
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
                .taskType(TaskTypeEnum.find(entity.getTaskType()))
                .waylineType(WaylineTypeEnum.find(entity.getWaylineType()))
                .rthAltitude(entity.getRthAltitude())
                .outOfControlAction(OutOfControlActionEnum.find(entity.getOutOfControlAction()))
                .mediaCount(entity.getMediaCount())
                .exitWaylineWhenRcLost(entity.getExitWaylineWhenRcLost())
                .parentId(entity.getParentId())
                .groupId(entity.getGroupId())
                .continuable(entity.getContinuable());

        if (Objects.nonNull(entity.getEndTime())) {
            builder.endTime(LocalDateTime.ofInstant(Instant.ofEpochMilli(entity.getEndTime()), ZoneId.systemDefault()));
        }
        if (WaylineJobStatusEnum.IN_PROGRESS.getVal() == entity.getStatus()) {
            builder.progress(waylineRedisService.getRunningWaylineJob(entity.getDockSn())
                    .map(EventsReceiver::getOutput)
                    .map(FlighttaskProgress::getProgress)
                    .map(FlighttaskProgressData::getPercent)
                    .orElse(null));
        }

        // modify by Qfei, 2023-10-11 09:50:15
        if (StringUtils.hasText(entity.getParentId()) && entity.getContinuable()) {
            Optional<ProgressExtBreakPoint> breakPointReceiver = waylineRedisService.getProgressExtBreakPoint(entity.getParentId());
            breakPointReceiver.ifPresent(x -> builder.breakPoint(
                    new FlighttaskBreakPoint().setIndex(x.getIndex())
                            .setState(x.getState())
                            .setProgress(x.getProgress())
                            .setWaylineId(x.getWaylineId())));
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
