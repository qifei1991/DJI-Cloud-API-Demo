package com.dji.sample.cloudapi.controller;

import com.dji.sample.cloudapi.service.LiveService;
import com.dji.sample.common.model.ResponseResult;
import com.dji.sample.manage.model.dto.CapacityDeviceDTO;
import com.dji.sample.manage.model.dto.LiveTypeDTO;
import com.dji.sample.manage.service.ILiveStreamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 直播控制
 *
 * @author Qfei
 * @date 2022/12/21 14:06
 */
@RestController
@RequestMapping("${url.cloud-api.prefix}${url.cloud-api.version}/live")
public class LiveController {

    @Autowired
    private ILiveStreamService liveStreamService;
    @Autowired
    private LiveService liveService;

    /**
     * Get live capability data of all drones in the current workspace from the database.
     * @param workspaceId workspace id
     * @return live capacity.
     */
    @GetMapping("/capacity/{workspace_id}")
    public ResponseResult<List<CapacityDeviceDTO>> getLiveCapacity(@PathVariable("workspace_id") String workspaceId) {
        return ResponseResult.success(this.liveStreamService.getLiveCapacity(workspaceId));
    }
    /**
     * Get live capability data of all drones in the current workspace from the database.
     * @param sn device sn
     * @return live capacity.
     */
    @GetMapping("/{sn}/capacity/")
    public ResponseResult<CapacityDeviceDTO> getDeviceLiveCapacity(@PathVariable("sn") String sn) {
        return ResponseResult.success(this.liveService.getDeviceLiveCapacity(sn));
    }

    /**
     * Start Live-streaming according to the parameters passed in from the aircraft-manager service.
     * @param liveParam Live-streaming parameters.
     * @return ResponseResult
     */
    @PostMapping("/streams/start")
    public ResponseResult liveStart(@RequestBody LiveTypeDTO liveParam) {
        return liveStreamService.liveStart(liveParam);
    }

    /**
     * Stop live-streaming according to the parameters passed in from the aircraft-manager service.
     * @param liveParam Live-streaming parameters.
     * @return ResponseResult
     */
    @PostMapping("/streams/stop")
    public ResponseResult liveStop(@RequestBody LiveTypeDTO liveParam) {
        return liveStreamService.liveStop(liveParam.getVideoId());
    }

    /**
     * Set the quality of the live-streaming according to the parameters passed in from the aircraft-manager service.
     * @param liveParam Live-streaming parameters.
     * @return ResponseResult
     */
    @PostMapping("/streams/update")
    public ResponseResult liveSetQuality(@RequestBody LiveTypeDTO liveParam) {
        return liveStreamService.liveSetQuality(liveParam);
    }

    /**
     * Set the lens of the live-streaming according to the parameters passed in from the aircraft-manager service.
     * @param liveParam Live-streaming parameters.
     * @return ResponseResult
     */
    @PostMapping("/streams/switch")
    public ResponseResult liveLensChange(@RequestBody LiveTypeDTO liveParam) {
        return liveStreamService.liveLensChange(liveParam);
    }
}
