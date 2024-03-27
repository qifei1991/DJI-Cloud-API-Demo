package com.dji.sample.cloudapi.client;

import com.dji.sample.cloudapi.util.ClientUri;
import com.dji.sdk.cloudapi.wayline.GetWaylineListResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * 航线文件客户端
 *
 * @author Qfei
 * @date 2023/7/7 15:54
 */
@Slf4j
@Component
public class WaylineFileClient extends AbstractClient {

    public void reportWaylineImport(Optional<GetWaylineListResponse> waylineOpt) {
        waylineOpt.ifPresent(x -> this.applicationJsonPost(ClientUri.URI_WAYLINE_REPORT, x));
    }
}
