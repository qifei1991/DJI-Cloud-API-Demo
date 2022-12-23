package com.dji.sample.component;

import cn.hutool.core.collection.CollUtil;
import com.dji.sample.common.model.ResponseResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.List;

/**
 * @author sean
 * @version 0.2
 * @date 2021/12/1
 */
@Slf4j
@ControllerAdvice
@ResponseBody
public class GlobalExceptionHandler {

    /**
     * Please do not return directly like this, there is a risk.
     * @param e
     * @return
     */
    @ExceptionHandler(Exception.class)
    public ResponseResult exceptionHandler(Exception e) {
        log.error("Exception message: " + e.getMessage(), e);
        return ResponseResult.error(e.getLocalizedMessage());
    }

    @ExceptionHandler(NullPointerException.class)
    public ResponseResult nullPointerExceptionHandler(NullPointerException e) {
        log.error("NullPointerException message: " + e.getMessage(), e);
        return ResponseResult.error("A null object appeared.");
    }

    @ExceptionHandler({MethodArgumentNotValidException.class, BindException.class})
    public ResponseResult methodArgumentNotValidExceptionHandler(BindException e) {
        log.error("BindException message: " + e.getMessage(), e);
        return ResponseResult.error(obtainMsg(e));
    }

    private static String obtainMsg(BindException e) {
        BindingResult bindingResult = e.getBindingResult();
        StringBuilder sb = new StringBuilder();
        List<String> fieldErrors = new ArrayList<>();
        for (FieldError fieldError : bindingResult.getFieldErrors()) {
            fieldErrors.add(fieldError.getField().concat(": ").concat(String.valueOf(fieldError.getDefaultMessage())));
        }
        return sb.append(CollUtil.join(fieldErrors, ", ")).toString();
    }

}
