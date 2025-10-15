package com.dji.sample.component.mqtt.config;

import com.auth0.jwt.algorithms.Algorithm;
import com.dji.sample.common.util.JwtUtil;
import com.dji.sample.component.mqtt.model.MqttClientOptions;
import com.dji.sample.component.mqtt.model.MqttProtocolEnum;
import com.dji.sample.component.mqtt.model.MqttUseEnum;
import com.dji.sdk.cloudapi.control.DrcModeMqttBroker;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.mqtt.core.DefaultMqttPahoClientFactory;
import org.springframework.integration.mqtt.core.MqttPahoClientFactory;
import org.springframework.util.StringUtils;

import java.util.Map;

/**
 *
 * @author sean.zhou
 * @date 2021/11/10
 * @version 0.1
 */
@Slf4j
@Configuration
@Data
@ConfigurationProperties
public class MqttPropertyConfiguration {

    private static Map<MqttUseEnum, MqttClientOptions> mqtt;

    public void setMqtt(Map<MqttUseEnum, MqttClientOptions> mqtt) {
        MqttPropertyConfiguration.mqtt = mqtt;
    }

    /**
     * Get the configuration options of the basic link of the mqtt client.
     * @return
     */
    static MqttClientOptions getBasicClientOptions() {
        if (!mqtt.containsKey(MqttUseEnum.BASIC)) {
            throw new Error("请先配置mqtt的基础连接参数，否则服务将无法正常启动!");
        }
        return mqtt.get(MqttUseEnum.BASIC);
    }

    /**
     * Get the mqtt address of the basic link.
     * @return
     */
    public static String getBasicMqttAddress() {
        return getMqttAddress(getBasicClientOptions());
    }

    /**
     * Splice the mqtt address according to the parameters of different clients.
     * @param options
     * @return
     */
    private static String getMqttAddress(MqttClientOptions options) {
        StringBuilder addr = new StringBuilder()
                .append(options.getProtocol().getProtocolAddr())
                .append(options.getHost().trim())
                .append(":")
                .append(options.getPort());
        if ((options.getProtocol() == MqttProtocolEnum.WS || options.getProtocol() == MqttProtocolEnum.WSS)
                && StringUtils.hasText(options.getPath())) {
            addr.append(options.getPath());
        }
        return addr.toString();
    }

    private static DrcModeMqttBroker getDrcModeMqttBroker(String mqttAddress, String clientId,
            String username, Long age, Map<String, ?> map) {

        Algorithm algorithm = JwtUtil.algorithm;
        String token = JwtUtil.createToken(map, age * 1000L, algorithm, null, null);
        return new DrcModeMqttBroker()
                .setAddress(mqttAddress)
                .setUsername(username)
                .setClientId(clientId)
                .setExpireTime(System.currentTimeMillis() / 1000 + age)
                .setPassword(token)
                .setEnableTls(false);
    }

    /**
     * Get the connection parameters of the mqtt client of the drc link.
     * @param clientId
     * @param username
     * @param age   The validity period of the token. unit: s
     * @param map   Custom data added in token.
     * @return
     */
    public static DrcModeMqttBroker getMqttBrokerWithDrc(String clientId, String username, Long age, Map<String, ?> map) {
        if (!mqtt.containsKey(MqttUseEnum.DRC)) {
            throw new RuntimeException("请先在后台服务中配置DRC远程控制连接参数!");
        }
        String mqttAddress = getMqttAddress(mqtt.get(MqttUseEnum.DRC));
        return getDrcModeMqttBroker(mqttAddress, clientId, username, age, map);
    }

    public static DrcModeMqttBroker getWebMqttBrokerWithDrc(String clientId, String username, Long age, Map<String, ?> map) {
        if (!mqtt.containsKey(MqttUseEnum.WEB)) {
            log.info("Web端使用的DRC远程连接参数和飞行器使用的连接参数一致！address: {}", mqtt.get(MqttUseEnum.DRC));
            return getMqttBrokerWithDrc(clientId, username, age, map);
        }
        String mqttAddress = getMqttAddress(mqtt.get(MqttUseEnum.WEB));
        return getDrcModeMqttBroker(mqttAddress, clientId, username, age, map);
    }

    @Bean
    public MqttConnectOptions mqttConnectOptions() {
        MqttClientOptions customizeOptions = getBasicClientOptions();
        MqttConnectOptions mqttConnectOptions = new MqttConnectOptions();
        mqttConnectOptions.setServerURIs(new String[]{ getBasicMqttAddress() });
        mqttConnectOptions.setUserName(customizeOptions.getUsername());
        mqttConnectOptions.setPassword(StringUtils.hasText(customizeOptions.getPassword()) ?
                customizeOptions.getPassword().toCharArray() : new char[0]);
        mqttConnectOptions.setAutomaticReconnect(true);
        mqttConnectOptions.setKeepAliveInterval(10);
        return mqttConnectOptions;
    }

    @Bean
    public MqttPahoClientFactory mqttClientFactory() {
        DefaultMqttPahoClientFactory factory = new DefaultMqttPahoClientFactory();
        factory.setConnectionOptions(mqttConnectOptions());
        return factory;
    }
}
