package com.dji.sample.manage.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.dji.sample.manage.dao.IDockSetMapper;
import com.dji.sample.manage.model.dto.DockSetDTO;
import com.dji.sample.manage.model.entity.DockSetEntity;
import com.dji.sample.manage.service.IDockSetService;
import com.dji.sdk.cloudapi.device.RainfallEnum;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 机场设置服务
 *
 * @author Qfei
 * @date 2026/5/21 11:06
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DockSetServiceImpl implements IDockSetService {

    private final IDockSetMapper dockSetMapper;

    @Override
    public List<DockSetDTO> getDockSettings(String workspaceId) {
        return dockSetMapper.selectList(
                Wrappers.lambdaQuery(DockSetEntity.class)
                        .eq(DockSetEntity::getWorkspaceId, workspaceId))
                .stream()
                .map(this::entity2Dto)
                .collect(Collectors.toList());
    }

    @Override
    public void saveDockSettings(String workspaceId, List<DockSetDTO> dockSetDTO) {
        dockSetDTO.forEach(dto -> saveDockSettings(workspaceId, dto));
    }

    private void saveDockSettings(String workspaceId, DockSetDTO dto) {
        Optional<DockSetEntity> settingOpt = getOneSetting(workspaceId, dto.getDeviceSn());
        if (settingOpt.isEmpty()) {
            dockSetMapper.insert(new DockSetEntity()
                    .setDeviceSn(dto.getDeviceSn())
                    .setWorkspaceId(workspaceId)
                    .setWindSpeed(dto.getWindSpeed())
                    .setRainfall(dto.getRainfall().getRain())
                    .setDroneLostReportPhone(dto.getDroneLostReportPhone())
                    .setRemainingPowerForReturnHome(dto.getRemainingPowerForReturnHome())
                    .setCreateTime(System.currentTimeMillis())
                    .setCreateUsername(dto.getCreateUsername()));
        } else {
            dockSetMapper.updateById(settingOpt.get()
                    .setWindSpeed(dto.getWindSpeed())
                    .setRainfall(dto.getRainfall().getRain())
                    .setDroneLostReportPhone(dto.getDroneLostReportPhone())
                    .setRemainingPowerForReturnHome(dto.getRemainingPowerForReturnHome())
                    .setUpdateTime(System.currentTimeMillis())
                    .setUpdateUsername(dto.getUpdateUsername()));
        }
    }

    private Optional<DockSetEntity> getOneSetting(String workspaceId, String deviceSn) {
        return Optional.ofNullable(dockSetMapper.selectOne(
                Wrappers.lambdaQuery(DockSetEntity.class)
                        .eq(DockSetEntity::getWorkspaceId, workspaceId)
                        .eq(DockSetEntity::getDeviceSn, deviceSn)));
    }

    private DockSetDTO entity2Dto(DockSetEntity entity) {
        if (entity == null) {
            return null;
        }
        return new DockSetDTO()
                .setDeviceSn(entity.getDeviceSn())
                .setWindSpeed(entity.getWindSpeed())
                .setRainfall(Objects.isNull(entity.getRainfall()) ? null : RainfallEnum.find(entity.getRainfall()))
                .setDroneLostReportPhone(entity.getDroneLostReportPhone())
                .setRemainingPowerForReturnHome(entity.getRemainingPowerForReturnHome())
                .setCreateTime(entity.getCreateTime())
                .setUpdateTime(entity.getUpdateTime())
                .setCreateUsername(entity.getCreateUsername())
                .setUpdateUsername(entity.getUpdateUsername());
    }

    @Override
    public DockSetDTO getDockSettings(String workspaceId, String dockSn) {
        return getOneSetting(workspaceId, dockSn).map(this::entity2Dto).orElse(null);
    }
}
