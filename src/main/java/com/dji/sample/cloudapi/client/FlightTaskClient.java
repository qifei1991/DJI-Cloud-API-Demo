package com.dji.sample.cloudapi.client;

import com.dji.sample.cloudapi.model.param.SortiesRecordParam;
import com.dji.sample.cloudapi.util.ClientUri;
import com.dji.sample.manage.model.dto.DeviceDTO;
import com.dji.sample.manage.service.IDeviceService;
import com.dji.sample.wayline.model.dto.FlightTaskProgressReceiver;
import com.dji.sample.wayline.model.dto.WaylineJobDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * 飞行任务客户端
 *
 * @author Qfei
 * @date 2022/12/20 17:30
 */
@Slf4j
@Component
public class FlightTaskClient extends AbstractClient {

    @Autowired
    private IDeviceService deviceService;

    /**
     * Report the aircraft-manager that started a flight task.
     * @param job WaylineJobDTO
     */
    @Async("asyncThreadPool")
    public void startFlightTask(WaylineJobDTO job) {
        SortiesRecordParam recordParam = SortiesRecordParam.builder()
                .sortiesId(job.getJobId())
                .name(job.getJobName())
                .waylineId(job.getFileId())
                .state(job.getStatus())
                .flightType(job.getTaskType())
                .build();
        obtainDroneSn(job, recordParam);
        this.applicationJsonPost(ClientUri.URI_SORTIES_START, recordParam);
    }

    /**
     * Report the aircraft-manager that started a flight task.
     * @param job WaylineJobDTO
     */
    @Async("asyncThreadPool")
    public void stopFlightTask(WaylineJobDTO job) {
        SortiesRecordParam recordParam = SortiesRecordParam.builder()
                .sortiesId(job.getJobId())
                .fileTotal(job.getMediaCount())
                .state(job.getStatus())
                .endTime(job.getEndTime().format(FORMATTER))
                .build();
        obtainDroneSn(job, recordParam);
        this.applicationJsonPost(ClientUri.URI_SORTIES_STOP, recordParam);
    }

    private void obtainDroneSn(WaylineJobDTO job, SortiesRecordParam recordParam) {
        // Set the drone sn that shoots the media
        Optional<DeviceDTO> dockDTO = deviceService.getDeviceBySn(job.getDockSn());
        dockDTO.ifPresent(deviceDTO -> recordParam.setAircraftSn(deviceDTO.getChildDeviceSn()));
    }

    /**
     * Report the aircraft-manager that the progress about the flight task.
     * @param jobId flight ID
     * @param progressReceiver the progress information.
     */
    @Async("asyncThreadPool")
    public void reportFlightTaskProgress(String jobId, FlightTaskProgressReceiver progressReceiver) {
        this.applicationJsonPost(ClientUri.URI_SORTIES_PROGRESS, progressReceiver, jobId);
    }

}
