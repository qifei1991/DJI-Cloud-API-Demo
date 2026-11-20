package com.dji.sample.wayline.service;

import com.dji.sample.wayline.model.dto.WaylineFileFlightDTO;
import com.dji.sample.wayline.model.dto.WaylineJobDTO;

import java.sql.SQLException;
import java.util.Optional;

/**
 * The wayline file copy used by the flight task.
 * @author sean
 * @version 1.0
 * @date 2026/11/20
 */
public interface IWaylineFileFlightService {

    /**
     * Query the wayline file copy which the flight task uses.
     * @param jobId
     * @return
     */
    Optional<WaylineFileFlightDTO> getFlightFileByJobId(String jobId);

    /**
     * Get the wayline file copy of this job. The copy is created when it does not exist,
     * and the lost action in the copy is modified by the parameters of the job.
     * @param waylineJob
     * @return
     */
    WaylineFileFlightDTO getOrCreateFlightFile(WaylineJobDTO waylineJob) throws SQLException;

    /**
     * Delete the wayline file copy of this job, include the object in the bucket.
     * @param jobId
     * @return
     */
    Boolean deleteFlightFileByJobId(String jobId);
}
