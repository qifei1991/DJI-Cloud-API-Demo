package com.dji.sample.cloudapi.client;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import com.dji.sample.cloudapi.model.enums.WaylineType;
import com.dji.sample.cloudapi.model.param.SortiesRecordParam;
import com.dji.sample.cloudapi.util.ClientUri;
import com.dji.sample.control.model.dto.FlyToProgressReceiver;
import com.dji.sample.control.model.dto.TakeoffProgressReceiver;
import com.dji.sample.control.model.param.TakeoffToPointParam;
import com.dji.sample.manage.model.dto.DeviceDTO;
import com.dji.sample.manage.service.IDeviceService;
import com.dji.sample.wayline.model.dto.WaylineJobDTO;
import com.dji.sample.wayline.model.dto.WaylineTaskProgressReceiver;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * 飞行任务客户端
 *
 * @author Qfei
 * @date 2022/12/20 17:30
 */
@Slf4j
@Component
@Lazy
public class FlightTaskClient extends AbstractClient {

    @Autowired
    private IDeviceService deviceService;

    /**
     * Report the aircraft-manager that started a flight task.
     * @param job WaylineJobDTO
     */
    @Async("asyncThreadPool")
    public void startFlightTask(WaylineJobDTO job) {
        // 断点续飞的任务，不生成飞行记录
        if (StringUtils.hasText(job.getParentId()) && job.getContinuable()) {
            return;
        }
        SortiesRecordParam recordParam = SortiesRecordParam.builder()
                .sortiesId(job.getJobId())
                .name(job.getJobName())
                .waylineId(job.getFileId())
                .state(job.getStatus())
                .flightType(WaylineType.getWaylineType(job.getWaylineType().getVal()).getFlightType())
                .startTime(Optional.ofNullable(job.getExecuteTime()).map(x -> x.format(FORMATTER)).orElse(DateUtil.now()))
                .userName(job.getUsername())
                .groupId(job.getGroupId())
                .build();
        obtainDroneSn(job.getDockSn(), recordParam);
        this.applicationJsonPost(ClientUri.URI_SORTIES_START, recordParam);
    }

    /**
     * Report the aircraft-manager that a flight task completed.
     * @param job WaylineJobDTO
     */
    @Async("asyncThreadPool")
    public void flightTaskCompleted(WaylineJobDTO job) {

        SortiesRecordParam recordParam = SortiesRecordParam.builder()
                .sortiesId(job.getJobId())
                .name(job.getJobName())
                .fileTotal(job.getMediaCount())
                .state(job.getStatus())
                .endTime(Optional.ofNullable(job.getCompletedTime()).map(x -> x.format(FORMATTER)).orElse(DateUtil.now()))
                .groupId(job.getGroupId())
                .build();
        obtainDroneSn(job.getDockSn(), recordParam);
        this.applicationJsonPost(ClientUri.URI_SORTIES_COMPLETE, recordParam);
    }

    private void obtainDroneSn(String dockSn, SortiesRecordParam recordParam) {
        // Set the drone sn that shoots the media
        Optional<DeviceDTO> dockDTO = deviceService.getDeviceBySn(dockSn);
        dockDTO.ifPresent(deviceDTO -> recordParam.setAircraftSn(deviceDTO.getChildDeviceSn()));
    }

    /**
     * Report the aircraft-manager that the progress about the flight task.
     * @param jobId flight ID
     * @param progressReceiver the progress information.
     */
    @Async("asyncThreadPool")
    public void flightTaskProgress(String jobId, WaylineTaskProgressReceiver progressReceiver) {
        this.applicationJsonPost(ClientUri.URI_SORTIES_PROGRESS, progressReceiver, jobId);
    }

    public void startTakeoffTo(String dockSn, TakeoffToPointParam params) {
        SortiesRecordParam recordParam = SortiesRecordParam.builder()
                .sortiesId(params.getFlightId())
                .name(String.format("手控飞行-%s", DateUtil.format(LocalDateTime.now(), DatePattern.PURE_DATETIME_PATTERN)))
                .state(0)
                .flightType(WaylineType.Unknown.getFlightType())
                .startTime(DateUtil.now())
                .peekHeight(params.getSecurityTakeoffHeight())
                .userName(params.getUsername())
                .build();
        obtainDroneSn(dockSn, recordParam);
        this.applicationJsonPost(ClientUri.URI_SORTIES_START, recordParam);
    }

    public void finishTakeoffTo(String dockSn, TakeoffProgressReceiver receiver) {
        SortiesRecordParam recordParam = SortiesRecordParam.builder()
                .sortiesId(receiver.getFlightId())
                .state(2)
                .endTime(DateUtil.now())
                .build();
        obtainDroneSn(dockSn, recordParam);
        this.applicationJsonPost(ClientUri.URI_SORTIES_COMPLETE, recordParam);
    }

    public void finishFlyTo(String dockSn, FlyToProgressReceiver receiver) {
        SortiesRecordParam recordParam = SortiesRecordParam.builder()
                .sortiesId(receiver.getFlyToId())
                .state(2)
                .endTime(DateUtil.now())
                .build();
        obtainDroneSn(dockSn, recordParam);
        this.applicationJsonPost(ClientUri.URI_SORTIES_COMPLETE, recordParam);
    }

}
