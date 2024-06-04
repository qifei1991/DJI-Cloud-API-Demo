package com.dji.sample.manage.model.common;

import com.dji.sdk.cloudapi.hms.HmsInTheSkyEnum;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author sean
 * @version 1.1
 * @date 2022/7/7
 */
@Slf4j
@Component
public class HmsJsonUtil {

    private static ObjectMapper mapper;

    @Autowired
    public void setMapper(ObjectMapper mapper) {
        HmsJsonUtil.mapper = mapper;
    }

    private static JsonNode nodes;

    private HmsJsonUtil(){

    }

    @PostConstruct
    private void loadJsonFile() {
        try (InputStream inputStream = new ClassPathResource("hms.json").getInputStream()){
            nodes = mapper.readTree(inputStream);
        } catch (IOException e) {
            log.error("hms.json failed to load.");
            e.printStackTrace();
        }
    }

    public static HmsMessage get(String key) {
        if (nodes.get(key) == null) {
            // modify by Qfei, 判断是否包含 'in_the_sky' 字符串，如果去掉后有msg，就取去掉后的值
            if (key.endsWith(HmsInTheSkyEnum.IN_THE_SKY.getText())) {
                String newKey = key.substring(0, key.indexOf(HmsInTheSkyEnum.IN_THE_SKY.getText()));
                if (null != nodes.get(newKey)) {
                    mapper.convertValue(nodes.get(newKey), HmsMessage.class);
                }
            }
            return new HmsMessage();
        }
        return mapper.convertValue(nodes.get(key), HmsMessage.class);
    }
}
