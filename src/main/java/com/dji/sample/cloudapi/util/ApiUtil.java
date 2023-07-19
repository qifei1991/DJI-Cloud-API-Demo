package com.dji.sample.cloudapi.util;

import cn.hutool.core.util.NumberUtil;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 工具类
 *
 * @author Qfei
 * @date 2023/7/19 13:57
 */
@Slf4j
@NoArgsConstructor
public class ApiUtil {

    private static final String[] DIRECTION_DESC = {"正北", "东北", "正东", "东南", "正南", "西南", "正西", "西北"};
    /**
     * 通过角度计算方向
     * @param course 角度(0~359)
     * @return  方向
     */
    public static String course2direction(Float course) {
        if (!NumberUtil.isValid(course)) {
            return null;
        }
        course = course < 0 ? (course + 360) : course >= 359 ? 360 : course; //处理不正常的角度及把359转成360方便计算

        String str;

        int mod = Math.floorMod(Math.round(course), 45);
        float num = course / 45; //每45度为一个正方向
        //如果是整数表示为八个正方向
        if (mod == 0) {
            str = DIRECTION_DESC[Math.round(num)];
        } else {
            //非八个正方向
            String direction = "";
            if (num > 7 || num < 1) {
                direction = "北"; //偏北
            } else if (num > 1 && num < 3) {
                direction = "东"; //偏东
            } else if (num > 3 && num < 5) {
                direction = "南"; //偏南
            } else if (num > 5 && num < 7) {
                direction = "西"; //偏西
            }
            String slantAngle = Math.round(course - 90 * Math.round(course / 90)) + "°"; //偏角值(正角或负角)
            str = '偏' + direction + slantAngle;
        }
        return str;
    }

    public static void main(String[] args) {

        log.info("方向: {}", ApiUtil.course2direction(-109.1F));
    }

}
