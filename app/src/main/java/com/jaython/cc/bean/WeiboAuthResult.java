package com.jaython.cc.bean;

/**
 * 通用的第三方授权Result
 *
 * @author markmjw
 * @date 2015-05-05
 */
public class WeiboAuthResult {
    private String id;
    private String accessToken;
    private long expiresIn;
    private String refreshToken;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public long getExpiresIn() {
        return expiresIn;
    }

    public void setExpiresIn(long expiresIn) {
        this.expiresIn = expiresIn;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }


}
