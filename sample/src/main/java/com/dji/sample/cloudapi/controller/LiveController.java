package com.dji.sample.cloudapi.controller;

import com.dji.sample.cloudapi.service.LiveService;
import com.dji.sdk.common.HttpResultResponse;
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
    public HttpResultResponse<List<CapacityDeviceDTO>> getLiveCapacity(@PathVariable("workspace_id") String workspaceId) {
        return HttpResultResponse.success(this.liveStreamService.getLiveCapacity(workspaceId));
    }

    /**
     * Get live capability data of all drones in the current workspace from the database.
     * @param sn device sn
     * @return live capacity.
     */
    @GetMapping("/{sn}/capacity/")
    public HttpResultResponse<CapacityDeviceDTO> getDeviceLiveCapacity(@PathVariable("sn") String sn) {
        return HttpResultResponse.success(this.liveService.getDeviceLiveCapacity(sn));
    }

    /**
     * Start Live-streaming according to the parameters passed in from the aircraft-manager service.
     * @param liveParam Live-streaming parameters.
     * @return HttpResultResponse
     */
    @PostMapping("/streams/start")
    public HttpResultResponse liveStart(@RequestBody LiveTypeDTO liveParam) {
        return liveStreamService.liveStart(liveParam);
    }

    /**
     * Stop live-streaming according to the parameters passed in from the aircraft-manager service.
     * @param liveParam Live-streaming parameters.
     * @return HttpResultResponse
     */
    @PostMapping("/streams/stop")
    public HttpResultResponse liveStop(@RequestBody LiveTypeDTO liveParam) {
        return liveStreamService.liveStop(liveParam.getVideoId());
    }

    /**
     * Set the quality of the live-streaming according to the parameters passed in from the aircraft-manager service.
     * @param liveParam Live-streaming parameters.
     * @return HttpResultResponse
     */
    @PostMapping("/streams/update")
    public HttpResultResponse liveSetQuality(@RequestBody LiveTypeDTO liveParam) {
        return liveStreamService.liveSetQuality(liveParam);
    }

    /**
     * Set the lens of the live-streaming according to the parameters passed in from the aircraft-manager service.
     * @param liveParam Live-streaming parameters.
     * @return HttpResultResponse
     */
    @PostMapping("/streams/switch")
    public HttpResultResponse liveLensChange(@RequestBody LiveTypeDTO liveParam) {
        return liveStreamService.liveLensChange(liveParam);
    }
}
