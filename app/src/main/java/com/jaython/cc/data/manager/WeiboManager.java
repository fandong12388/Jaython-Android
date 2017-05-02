package com.jaython.cc.data.manager;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;

import com.jaython.cc.JaythonApplication;
import com.jaython.cc.bean.share.WeiboLoginInfo;
import com.jaython.cc.data.constants.SPConstant;
import com.jaython.cc.utils.Logger;
import com.jaython.cc.utils.PreferenceUtil;
import com.jaython.cc.utils.helper.UIHelper;
import com.sina.weibo.sdk.api.share.IWeiboShareAPI;
import com.sina.weibo.sdk.api.share.WeiboShareSDK;
import com.sina.weibo.sdk.auth.WeiboAuth;
import com.sina.weibo.sdk.auth.WeiboAuthListener;
import com.sina.weibo.sdk.auth.sso.SsoHandler;
import com.sina.weibo.sdk.exception.WeiboException;
import com.tiny.volley.utils.GsonUtil;

/**
 * time: 2017/2/6
 * description:
 *
 * @author fandong
 */
public class WeiboManager {

    public static final String APP_KEY = "591086646";
    public static final String APP_SECRET = "efb49335a852c7d93c1ad07ba0cc482f";
    public static final String WEIBO_REDIRECT_URL = "https://api.weibo.com/oauth2/default.html";
    public static final String WEIBO_HOST = "https://api.weibo.com/2/";
    public static final String URL_GET_USER_INFO = WEIBO_HOST + "users/show.json";
    public static final String WEIBO_SCOPE = "email,direct_messages_read,direct_messages_write,"
            + "friendships_groups_read,friendships_groups_write,statuses_to_me_read,"
            + "follow_app_official_microblog,invitation_write";

    private static WeiboManager sManager;
    private WeiboAuth mWeiboAuth;
    private SsoHandler mSsoHandler;
    private WeiboLoginInfo mWeiboLoginInfo;
    private SinaAuthListener mSinaAuthListener;

    private IWeiboShareAPI mWeiboShareAPI;
    /**
     * 微博登陆授权的回调监听
     */
    private WeiboAuthListener mAuthListener = new WeiboAuthListener() {
        @Override
        public void onComplete(Bundle values) {
            mWeiboLoginInfo = WeiboLoginInfo.parseAccessToken(values);
            if (mWeiboLoginInfo.isSessionValid()) {
                PreferenceUtil.putString(SPConstant.KEY_SINA_LOGIN_INFO, GsonUtil.toJson(mWeiboLoginInfo));
            } else {
                String code = values.getString("code");
                String message = "Oauth failed ！";
                if (!TextUtils.isEmpty(code)) {
                    message = message + "\nObtained the code: " + code;
                }
                UIHelper.shortToast(message);
            }
            if (mSinaAuthListener != null) {
                mSinaAuthListener.onComplete(mWeiboLoginInfo);
                mSinaAuthListener = null;
            }
        }

        @Override
        public void onWeiboException(WeiboException e) {
            e.printStackTrace();
            if (mSinaAuthListener != null) {
                mSinaAuthListener.onError(e);
                mSinaAuthListener = null;
            }
        }

        @Override
        public void onCancel() {
            if (mSinaAuthListener != null) {
                mSinaAuthListener.onCancel();
                mSinaAuthListener = null;
            }
        }
    };


    private WeiboManager() {
        mWeiboShareAPI = WeiboShareSDK.createWeiboAPI(JaythonApplication.gContext, APP_KEY);
    }

    public synchronized static WeiboManager getInstance() {
        if (null == sManager) {
            sManager = new WeiboManager();
        }
        return sManager;
    }

    /**
     * 微博登陆授权
     *
     * @param sinaAuthListener 登陆成功的回调
     * @param activity         登陆的界面
     */
    public void loginSina(SinaAuthListener sinaAuthListener, Activity activity) {
        //1.获取微博客户端相关信息，如是否安装、支持 SDK 的版本
        boolean isInstalledWeibo = mWeiboShareAPI.isWeiboAppInstalled();
        if (!isInstalledWeibo) {
            sinaAuthListener.onError(null);
            return;
        }
        //2.获取登陆信息
        WeiboLoginInfo loginInfo = loadSinaLoginInfo();
        if (null != loginInfo) {
            Logger.e("Jaython Request", loginInfo.toString());
            if (null != sinaAuthListener) {
                sinaAuthListener.onComplete(loginInfo);
            }
        } else {
            mSinaAuthListener = sinaAuthListener;
            mWeiboAuth = new WeiboAuth(activity,
                    APP_KEY,
                    WEIBO_REDIRECT_URL,
                    WEIBO_SCOPE);
            mSsoHandler = new SsoHandler(activity, mWeiboAuth);
            mSsoHandler.authorize(mAuthListener);
        }
    }

    /**
     * 获取新浪微博的登陆信息
     *
     * @return 如果没有保存登陆信息或者登陆信息的token失效，那么返回null
     */
    public WeiboLoginInfo loadSinaLoginInfo() {
        String loginInfo = PreferenceUtil.getString(SPConstant.KEY_SINA_LOGIN_INFO);
        if (!TextUtils.isEmpty(loginInfo)) {
            WeiboLoginInfo weiboLoginInfo = GsonUtil.fromJson(loginInfo, WeiboLoginInfo.class);
            if (weiboLoginInfo.isLoginValid()) {
                return weiboLoginInfo;
            }
        }
        return null;
    }

    public SsoHandler getSsoHandler() {
        return mSsoHandler;
    }

    public interface SinaAuthListener {
        void onComplete(WeiboLoginInfo loginInfo);

        void onError(Exception e);

        void onCancel();
    }

}
