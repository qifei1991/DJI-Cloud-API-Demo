package com.dji.sample.agora.model;

/**
 * @author Qfei
 * @date 2024/5/23 15:05
 */
public class ResultData<T> {

    private Integer code;
    private String message;
    private T data;

    @Override
    public String toString() {
        return "ResultData{" +
                "code=" + code +
                ", message='" + message + '\'' +
                ", data=" + data +
                '}';
    }

    public Integer getCode() {
        return code;
    }

    public ResultData<T> setCode(Integer code) {
        this.code = code;
        return this;
    }

    public String getMessage() {
        return message;
    }

    public ResultData<T> setMessage(String message) {
        this.message = message;
        return this;
    }

    public T getData() {
        return data;
    }

    public ResultData<T> setData(T data) {
        this.data = data;
        return this;
    }
}
