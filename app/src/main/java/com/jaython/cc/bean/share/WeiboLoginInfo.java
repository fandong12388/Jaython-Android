package com.jaython.cc.bean.share;

import android.os.Bundle;
import android.text.TextUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

/**
 * time: 15/6/11
 * description:
 *
 * @author fandong
 */
public class WeiboLoginInfo implements Serializable {
    private static final long serialVersionUID = -3449382709440377132L;
    private String mUid = "";
    private String mAccessToken = "";
    private String mRefreshToken = "";
    private long mExpiresTime = 0L;
    private String mPhoneNum = "";

    public WeiboLoginInfo() {
    }


    public WeiboLoginInfo(String accessToken, String expiresIn) {
        this.mAccessToken = accessToken;
        this.mExpiresTime = System.currentTimeMillis();
        if (expiresIn != null) {
            this.mExpiresTime += Long.parseLong(expiresIn) * 1000L;
        }

    }

    public static WeiboLoginInfo parseAccessToken(String responseJsonText) {
        if (!TextUtils.isEmpty(responseJsonText) && responseJsonText.indexOf("{") >= 0) {
            try {
                JSONObject e = new JSONObject(responseJsonText);
                WeiboLoginInfo token = new WeiboLoginInfo();
                token.setUid(e.optString("uid"));
                token.setToken(e.optString("access_token"));
                token.setExpiresIn(e.optString("expires_in"));
                token.setRefreshToken(e.optString("refresh_token"));
                token.setPhoneNum(e.optString("phone_num"));
                return token;
            } catch (JSONException var3) {
                var3.printStackTrace();
            }
        }

        return null;
    }

    public static WeiboLoginInfo parseAccessToken(Bundle bundle) {
        if (bundle != null) {
            WeiboLoginInfo accessToken = new WeiboLoginInfo();
            accessToken.setUid(getString(bundle, "uid", ""));
            accessToken.setToken(getString(bundle, "access_token", ""));
            accessToken.setExpiresIn(getString(bundle, "expires_in", ""));
            accessToken.setRefreshToken(getString(bundle, "refresh_token", ""));
            return accessToken;
        } else {
            return null;
        }
    }

    private static String getString(Bundle bundle, String key, String defaultValue) {
        if (bundle != null) {
            String value = bundle.getString(key);
            return value != null ? value : defaultValue;
        } else {
            return defaultValue;
        }
    }

    public boolean isSessionValid() {
        return !TextUtils.isEmpty(this.mAccessToken);
    }

    public Bundle toBundle() {
        Bundle bundle = new Bundle();
        bundle.putString("uid", this.mUid);
        bundle.putString("access_token", this.mAccessToken);
        bundle.putString("refresh_token", this.mRefreshToken);
        bundle.putString("expires_in", Long.toString(this.mExpiresTime));
        bundle.putString("phone_num", this.mPhoneNum);
        return bundle;
    }

    public String toString() {
        return "uid: " + this.mUid + ", " + "access_token" + ": " + this.mAccessToken + ", " + "refresh_token" + ": " + this.mRefreshToken + ", " + "phone_num" + ": " + this.mPhoneNum + ", " + "expires_in" + ": " + Long.toString(this.mExpiresTime);
    }

    public String getUid() {
        return this.mUid;
    }

    public void setUid(String uid) {
        this.mUid = uid;
    }

    public String getToken() {
        return this.mAccessToken;
    }

    public void setToken(String mToken) {
        this.mAccessToken = mToken;
    }

    public String getRefreshToken() {
        return this.mRefreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.mRefreshToken = refreshToken;
    }

    public long getExpiresTime() {
        return this.mExpiresTime;
    }

    public void setExpiresTime(long mExpiresTime) {
        this.mExpiresTime = mExpiresTime;
    }

    public void setExpiresIn(String expiresIn) {
        if (!TextUtils.isEmpty(expiresIn) && !expiresIn.equals("0")) {
            this.setExpiresTime(System.currentTimeMillis() + Long.parseLong(expiresIn) * 1000L);
        }

    }

    public String getPhoneNum() {
        return this.mPhoneNum;
    }

    public void setPhoneNum(String mPhoneNum) {
        this.mPhoneNum = mPhoneNum;
    }

    // 检查登录是否过期
    public boolean isLoginValid() {
        boolean valid = false;
        try {
            if (!TextUtils.isEmpty(getUid()) &&
                    !TextUtils.isEmpty(getToken())
                    && (getExpiresTime() == 0
                    || (System.currentTimeMillis() < getExpiresTime()))) {
                valid = true;
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return valid;
    }

}

