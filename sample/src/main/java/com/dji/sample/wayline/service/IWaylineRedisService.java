package com.dji.sample.wayline.service;

import com.dji.sample.component.mqtt.model.EventsReceiver;
import com.dji.sample.wayline.model.dto.ConditionalWaylineJobKey;
import com.dji.sample.wayline.model.dto.WaylineJobDTO;
import com.dji.sdk.cloudapi.wayline.FlighttaskProgress;
import com.dji.sdk.cloudapi.wayline.ProgressExtBreakPoint;

import java.util.Optional;

/**
 * @author sean
 * @version 1.4
 * @date 2023/3/24
 */
public interface IWaylineRedisService {

    /**
     * Save the status of the wayline job performed by the dock into redis.
     * @param dockSn
     * @param data
     */
    void setRunningWaylineJob(String dockSn, EventsReceiver<FlighttaskProgress> data);

    /**
     * Query the status of wayline job performed by the dock in redis.
     * @param dockSn
     * @return
     */
    Optional<EventsReceiver<FlighttaskProgress>> getRunningWaylineJob(String dockSn);

    /**
     * Delete the wayline job status of the dock operation in redis.
     * @param dockSn
     * @return
     */
    Boolean delRunningWaylineJob(String dockSn);

    /**
     * Save the wayline job suspended by the dock to redis.
     * @param dockSn
     * @param jobId
     */
    void setPausedWaylineJob(String dockSn, String jobId);

    /**
     * Query the wayline job id suspended by the dock in redis.
     * @param dockSn
     * @return
     */
    String getPausedWaylineJobId(String dockSn);

    /**
     * Delete the wayline job suspended by the dock in redis.
     * @param dockSn
     * @return
     */
    Boolean delPausedWaylineJob(String dockSn);

    /**
     * Save the wayline job blocked by the dock to redis.
     * @param dockSn
     * @param jobId
     */
    void setBlockedWaylineJob(String dockSn, String jobId);

    /**
     * Query the wayline job id blocked by the dock in redis.
     * @param dockSn
     * @return
     */
    String getBlockedWaylineJobId(String dockSn);

    /**
     * Save the conditional wayline job by the dock to redis.
     * @param waylineJob
     */
    void setConditionalWaylineJob(WaylineJobDTO waylineJob);

    /**
     * Query the conditional wayline job id by the dock in redis.
     * @param jobId
     * @return
     */
    Optional<WaylineJobDTO> getConditionalWaylineJob(String jobId);

    /**
     * Delete the conditional wayline job by the dock in redis.
     * @param jobId
     * @return
     */
    Boolean delConditionalWaylineJob(String jobId);

    Boolean addPrepareConditionalWaylineJob(WaylineJobDTO waylineJob);

    Optional<ConditionalWaylineJobKey> getNearestConditionalWaylineJob();

    Double getConditionalWaylineJobTime(ConditionalWaylineJobKey jobKey);

    Boolean removePrepareConditionalWaylineJob(ConditionalWaylineJobKey jobKey);

    /**
     * 保存航线任务断点信息到Redis中
     * @param jobId 任务ID
     * @param progressExtBreakPoint 断点信息对象
     */
    void setProgressExtBreakPoint(String jobId, ProgressExtBreakPoint progressExtBreakPoint);

    /**
     * 获取Redis中保存的任务断点信息
     * @param jobId 任务ID
     * @return 航线飞行进度的断点信息
     */
    Optional<ProgressExtBreakPoint> getProgressExtBreakPoint(String jobId);

    /**
     * 删除Redis中保存的任务断点信息
     * @param jobId 任务ID
     * @return 是否删除
     */
    Boolean delBreakPointReceiver(String jobId);
}
