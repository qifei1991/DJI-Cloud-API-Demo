package com.dji.sample.agora.model;

/**
 * @author Qfei
 * @date 2024/5/23 15:06
 */
public class TokenResult {

    private String token;

    @Override
    public String toString() {
        return "TokenResult{" +
                "token='" + token + '\'' +
                '}';
    }

    public String getToken() {
        return token;
    }

    public TokenResult setToken(String token) {
        this.token = token;
        return this;
    }
}
