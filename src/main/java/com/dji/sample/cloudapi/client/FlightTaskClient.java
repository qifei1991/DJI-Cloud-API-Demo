package com.dji.sample.cloudapi.client;

import com.dji.sample.cloudapi.model.param.SortiesRecordParam;
import com.dji.sample.cloudapi.model.vo.ResultView;
import com.dji.sample.cloudapi.util.ClientUri;
import com.dji.sample.wayline.model.dto.FlightTaskProgressReceiver;
import com.dji.sample.wayline.model.dto.WaylineJobDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * 飞行任务客户端
 *
 * @author Qfei
 * @date 2022/12/20 17:30
 */
@Slf4j
@Component
public class FlightTaskClient extends AbstractClient {

    /**
     * Report the aircraft-manager that started a flight task.
     * @param job WaylineJobDTO
     */
    @Async("asyncThreadPool")
    public void startFlightTask(WaylineJobDTO job) {
        ResultView result = this.applicationJsonPost(ClientUri.URI_SORTIES_START,
                SortiesRecordParam.builder()
                        .sortiesId(job.getJobId())
                        .name(job.getJobName())
                        .waylineId(job.getFileId())
                        .state(job.getStatus())
                        .flightType(job.getTaskType())
                        .build());
        log.debug("Report flight task start. response: " + result.toString());
    }

    /**
     * Report the aircraft-manager that started a flight task.
     * @param job WaylineJobDTO
     * @param sn deviceSn
     */
    @Async("asyncThreadPool")
    public void stopFlightTask(WaylineJobDTO job, String sn) {
        ResultView result = this.applicationJsonPost(ClientUri.URI_SORTIES_STOP,
                SortiesRecordParam.builder()
                        .sortiesId(job.getJobId())
                        .aircraftSn(sn)
                        .fileTotal(job.getMediaCount())
                        .state(job.getStatus())
                        .endTime(job.getEndTime().format(FORMATTER))
                        .build());
        log.debug("Report flight task end. response: " + result.toString());
    }

    /**
     * Report the aircraft-manager that the progress about the flight task.
     * @param jobId flight ID
     * @param progressReceiver the progress information.
     */
    @Async("asyncThreadPool")
    public void reportFlightTaskProgress(String jobId, FlightTaskProgressReceiver progressReceiver) {
        ResultView result = this.applicationJsonPost(ClientUri.URI_SORTIES_PROGRESS, progressReceiver, jobId);
        log.debug("Report flight task executing progress. response: " + result.toString());
    }

}
