package com.dji.sample.manage.annotation;

import com.dji.sample.manage.model.enums.PropertyParamEnum;

import java.lang.annotation.*;

/**
 * 属性设置参数位置
 *
 * @author Qfei
 * @date 2025/5/7 17:45
 */
@Documented
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface PropertyParamPosition {
    PropertyParamEnum value() default PropertyParamEnum.PARENT;
}
