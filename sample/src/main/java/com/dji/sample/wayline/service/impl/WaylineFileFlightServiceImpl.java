package com.dji.sample.wayline.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.dji.sample.component.oss.model.OssConfiguration;
import com.dji.sample.component.oss.service.impl.OssServiceContext;
import com.dji.sample.wayline.dao.IWaylineFileFlightMapper;
import com.dji.sample.wayline.model.dto.KmzFileProperties;
import com.dji.sample.wayline.model.dto.WaylineFileFlightDTO;
import com.dji.sample.wayline.model.dto.WaylineJobDTO;
import com.dji.sample.wayline.model.entity.WaylineFileFlightEntity;
import com.dji.sample.wayline.service.IWaylineFileFlightService;
import com.dji.sample.wayline.service.IWaylineFileService;
import com.dji.sample.wayline.util.KmzFileUtils;
import com.dji.sdk.cloudapi.wayline.GetWaylineListResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

/**
 * The wayline file copy used by the flight task.
 * The lost action in the copy is modified by the parameters of the flight task.
 * @author sean
 * @version 1.0
 * @date 2026/11/20
 */
@Service
@Slf4j
public class WaylineFileFlightServiceImpl implements IWaylineFileFlightService {

    @Autowired
    private IWaylineFileFlightMapper mapper;

    @Autowired
    private IWaylineFileService waylineFileService;

    @Autowired
    private OssServiceContext ossService;

    @Autowired
    private TransactionTemplate transactionTemplate;

    @Override
    public Optional<WaylineFileFlightDTO> getFlightFileByJobId(String jobId) {
        if (!StringUtils.hasText(jobId)) {
            return Optional.empty();
        }
        WaylineFileFlightEntity fileFlight = mapper.selectOne(
                new LambdaQueryWrapper<WaylineFileFlightEntity>()
                        .eq(WaylineFileFlightEntity::getJobId, jobId)
                        .orderByDesc(WaylineFileFlightEntity::getId)
                        .last("limit 1"));
        return Optional.ofNullable(this.entityConvertToDTO(fileFlight));
    }

    @Override
    public WaylineFileFlightDTO getOrCreateFlightFile(WaylineJobDTO waylineJob) throws SQLException {
        // The copy is reused when the flight task is prepared repeatedly.
        Optional<WaylineFileFlightDTO> flightFileOpt = this.getFlightFileByJobId(waylineJob.getJobId());
        if (flightFileOpt.isPresent()) {
            return flightFileOpt.get();
        }

        // get the original wayline file
        Optional<GetWaylineListResponse> waylineFileOpt =
                waylineFileService.getWaylineByWaylineId(waylineJob.getWorkspaceId(), waylineJob.getFileId());
        if (waylineFileOpt.isEmpty()) {
            throw new SQLException("无法获取飞行任务的航线文件，请查证。");
        }
        GetWaylineListResponse waylineFile = waylineFileOpt.get();

        // modify the lost action in the wayline file
        byte[] copyContent = KmzFileUtils.modifyRcLostAction(this.getObjectBytes(waylineFile.getObjectKey()),
                KmzFileUtils.getExitOnRcLost(waylineJob.getExitWaylineWhenRcLost()),
                KmzFileUtils.getExecuteRcLostAction(waylineJob.getOutOfControlAction()));

        // the copy is named with the original name and the current timestamp
        String copyName = waylineFile.getName() + "_" + System.currentTimeMillis();

        // upload the copy. format: {object_dir_prefix}/wayline_file_copy/{copy_name}.kmz
        String objectKey = OssConfiguration.objectDirPrefix + "/" + KmzFileProperties.WAYLINE_FILE_COPY_DIR
                + "/" + copyName + KmzFileProperties.WAYLINE_FILE_SUFFIX;
        String sign = DigestUtils.md5DigestAsHex(copyContent);
        ossService.putObject(OssConfiguration.bucket, objectKey, new ByteArrayInputStream(copyContent));

        WaylineFileFlightEntity fileFlight = WaylineFileFlightEntity.builder()
                .name(copyName)
                .fileId(UUID.randomUUID().toString())
                .originalFileId(waylineFile.getId())
                .jobId(waylineJob.getJobId())
                .objectKey(objectKey)
                .sign(sign)
                .username(StringUtils.hasText(waylineJob.getUsername()) ? waylineJob.getUsername() : waylineFile.getUsername())
                .build();
        try {
            // The upload exception is not thrown by the oss service, so the copy must be checked before saving.
            this.checkObjectUploaded(objectKey, copyContent.length);
            // The insert is put in a separate transaction to keep the io of the object storage out of the transaction.
            Integer inserted = transactionTemplate.execute(status -> mapper.insert(fileFlight));
            if (Objects.isNull(inserted) || inserted <= 0) {
                throw new SQLException("保存航线文件副本失败。");
            }
        } catch (DuplicateKeyException e) {
            // One job has only one copy, the copy has been saved by another thread or instance.
            this.deleteObject(objectKey);
            log.warn("The wayline file copy of the job already exists. jobId: {}", waylineJob.getJobId());
            return this.getFlightFileByJobId(waylineJob.getJobId())
                    .orElseThrow(() -> new SQLException("保存航线文件副本失败。", e));
        } catch (SQLException | RuntimeException e) {
            // The copy is useless when it is not saved, otherwise the object is an orphan file.
            this.deleteObject(objectKey);
            throw e;
        }

        log.info("Generate the wayline file copy. jobId: {}, name: {}, objectKey: {}",
                waylineJob.getJobId(), copyName, objectKey);
        return this.entityConvertToDTO(fileFlight);
    }

    @Override
    public Boolean deleteFlightFileByJobId(String jobId) {
        if (!StringUtils.hasText(jobId)) {
            return true;
        }
        List<WaylineFileFlightEntity> fileFlights = mapper.selectList(
                new LambdaQueryWrapper<WaylineFileFlightEntity>()
                        .eq(WaylineFileFlightEntity::getJobId, jobId));
        if (fileFlights.isEmpty()) {
            return true;
        }
        Integer deleted = transactionTemplate.execute(status -> mapper.delete(
                new LambdaQueryWrapper<WaylineFileFlightEntity>()
                        .eq(WaylineFileFlightEntity::getJobId, jobId)));
        if (Objects.isNull(deleted) || deleted <= 0) {
            return false;
        }
        boolean isDeleted = true;
        for (WaylineFileFlightEntity fileFlight : fileFlights) {
            isDeleted = this.deleteObject(fileFlight.getObjectKey()) && isDeleted;
        }
        return isDeleted;
    }

    /**
     * The putObject method of the oss service does not throw the exception when the upload fails,
     * so the copy is read back and checked before it is saved.
     * @param objectKey
     * @param expectedLength
     */
    private void checkObjectUploaded(String objectKey, int expectedLength) throws SQLException {
        try (InputStream object = ossService.getObject(OssConfiguration.bucket, objectKey)) {
            if (object.available() != expectedLength) {
                throw new SQLException("航线文件副本上传失败, objectKey:[" + objectKey + "].");
            }
        } catch (IOException e) {
            throw new SQLException("航线文件副本上传失败, objectKey:[" + objectKey + "].", e);
        }
    }

    private boolean deleteObject(String objectKey) {
        boolean isDeleted = ossService.deleteObject(OssConfiguration.bucket, objectKey);
        if (!isDeleted) {
            log.error("Failed to delete the wayline file copy, objectKey: {}", objectKey);
        }
        return isDeleted;
    }

    private byte[] getObjectBytes(String objectKey) throws SQLException {
        try (InputStream object = ossService.getObject(OssConfiguration.bucket, objectKey)) {
            if (object.available() == 0) {
                throw new SQLException("无法从文件存储中找到航线文件, objectKey:[" + objectKey +
                        "], bucket[" + OssConfiguration.bucket + "].");
            }
            return object.readAllBytes();
        } catch (IOException e) {
            throw new SQLException("读取航线文件失败, objectKey:[" + objectKey + "].", e);
        }
    }

    private WaylineFileFlightDTO entityConvertToDTO(WaylineFileFlightEntity entity) {
        if (Objects.isNull(entity)) {
            return null;
        }
        return WaylineFileFlightDTO.builder()
                .name(entity.getName())
                .fileId(entity.getFileId())
                .originalFileId(entity.getOriginalFileId())
                .jobId(entity.getJobId())
                .objectKey(entity.getObjectKey())
                .sign(entity.getSign())
                .username(entity.getUsername())
                .createTime(entity.getCreateTime())
                .updateTime(entity.getUpdateTime())
                .build();
    }
}
