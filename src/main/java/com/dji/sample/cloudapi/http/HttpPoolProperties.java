package com.dji.sample.cloudapi.http;
/**
 * @descirption
 * @name HttpPoolConfig
 * @author Qifei
 * @date 2021-07-07 17:13
 */

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConfigurationPropertiesBinding;

/**
 * @name HttpPoolConfig
 * @description http连接池配置类
 *
 * @author Qfei
 * @date 2021-07-07 17:13
 */
@Data
@ConfigurationProperties(prefix = "mapzone-httpclient", ignoreUnknownFields = false)
@ConfigurationPropertiesBinding
public class HttpPoolProperties {
    public Integer maxTotal = 64;
    public Integer defaultMaxPerRoute = 64;
    /**
     * 连接上服务器(握手成功)的时间，超出抛出connect timeout
     */
    public Integer connectTimeout = 10000;
    /**
     * 从连接池中获取连接的超时时间
     * 超时间未拿到可用连接，会抛出org.apache.http.conn.ConnectionPoolTimeoutException: Timeout waiting for connection from pool
     */
    public Integer connectionRequestTimeout = 10000;
    /**
     * 服务器返回数据(response)的时间，超过抛出read timeout
     */
    public Integer socketTimeout = 60000;
    public Integer validateAfterInactivity = 10000;
}
