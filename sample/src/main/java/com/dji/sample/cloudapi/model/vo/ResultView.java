package com.dji.sample.cloudapi.model.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 返回信息封装类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResultView implements Serializable {

    private static final long serialVersionUID = 1L;

    private boolean success;

    private String code;//编码

    private String msg;//消息

    private String errorMsg;//错误消息

    private Object datas;//数据集

    public ResultView(STATUS status) {
        this.code = status.statusCode;
        this.msg = status.statusMsg;
        this.success = "1000".equals(code);
    }

    public ResultView(STATUS status, String errorMsg) {
        this.code = status.statusCode;
        this.msg = status.statusMsg;
        this.success = "1000".equals(code);
        this.errorMsg = errorMsg;
    }

    public ResultView(STATUS status, Object datas) {
        this.code = status.statusCode;
        this.msg = status.statusMsg;
        this.datas = datas;
        this.success = "1000".equals(code);
    }

    public enum STATUS {

        /**
         * 自定义
         */
        CUSTOM("自定义编码", "自定义消息"),

        SUCCESS("1000", "操作成功"),
        FAIL("1010", "操作失败"),

        /**
         * 请求类异常
         */
        REQUEST_TIMEOUT("1040", "请求超时"),
        PARAMETER_EXPECTION("1041", "无效的参数"),
        TOKEN_EXPIRED("1042", "Token过期"),
        LICENSE_EXPIRED("1043", "许可过期"),

        /**
         * 程序类异常
         */
        SERVER_EXPECTION("1050", "服务器运行异常"),
        USERNAME_OR_PASSWORD_ERROR("1051", "用户名或者密码错误"),
        NO_USER_EXISTS("1052", "用户不存在 "),
        UNKNOWN_SERVER_EXPECTION("1053", "未知的服务器异常");

        private String statusCode;
        private String statusMsg;

        STATUS(String statusCode, String statusMsg) {
            this.statusCode = statusCode;
            this.statusMsg = statusMsg;
        }

        public STATUS setCode(String statusCode) {
            this.statusCode = statusCode;
            return this;
        }

        public STATUS setMessage(String message) {
            this.statusMsg = message;
            return this;
        }

        @Override
        public String toString() {
            return "{ \"statusCode\":" + this.statusCode + ",\"statusMsg\":\"" + this.statusMsg + "\"}";
        }
    }

    @Override
    public String toString() {
        return "ResultView [success= " + success + ", code=" + code + ", msg=" + msg + ", data=" + datas + "]";
    }

}