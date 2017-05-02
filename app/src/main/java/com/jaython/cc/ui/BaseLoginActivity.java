package com.jaython.cc.ui;

import android.content.Intent;

import com.jaython.cc.R;
import com.jaython.cc.bean.QQUser;
import com.jaython.cc.bean.parser.QQTokenParser;
import com.jaython.cc.bean.share.WeiboLoginInfo;
import com.jaython.cc.data.constants.EventConstant;
import com.jaython.cc.data.manager.AppInitManager;
import com.jaython.cc.data.manager.LoginManager;
import com.jaython.cc.data.manager.QQManager;
import com.jaython.cc.data.manager.RxBusManager;
import com.jaython.cc.data.manager.WeiboManager;
import com.jaython.cc.data.model.LoginModel;
import com.jaython.cc.utils.Logger;
import com.jaython.cc.utils.helper.UIHelper;
import com.sina.weibo.sdk.auth.sso.SsoHandler;
import com.tencent.connect.UserInfo;
import com.tencent.connect.auth.QQToken;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;
import com.tiny.volley.bean.parser.BaseParser;
import com.tiny.volley.utils.GsonUtil;
import com.umeng.analytics.MobclickAgent;

/**
 * time:2017/1/14
 * description:
 *
 * @author fandong
 */
public class BaseLoginActivity<ViewModel> extends BaseActivity<ViewModel> {
    private LoginModel mLoginModel;
    //QQ
    private IUiListener mIUiListener;
    private QQToken mQQToken;
    //微博
    private WeiboManager.SinaAuthListener mSinaAuthListener;

    {
        this.mLoginModel = new LoginModel();
        this.mIUiListener = new IUiListener() {

            @Override
            public void onComplete(Object o) {
                String json = o.toString();
                Logger.e("jaython_qq", "json:" + json);
                if (json.contains("access_token")) {
                    BaseParser<QQToken> parser = new QQTokenParser();
                    mQQToken = parser.parser(json);
                    getQQUserInfo(mQQToken);
                } else {
                    QQUser user = GsonUtil.fromJson(json, QQUser.class);
                    //登陆成功
                    mLoginModel.registerQQUser(mQQToken, user)
                            .subscribe(u -> {
                                //1.保存信息
                                LoginManager.getInstance().setLoginUser(u);
                                LoginManager.getInstance().saveLoginUser(u);
                                //2.重置基本信息
                                AppInitManager.getInstance().initializeSDK();
                                //3.发送广播
                                RxBusManager.post(EventConstant.KEY_LOGIN, u);
                                //4.umeng
                                MobclickAgent.onProfileSignIn("qq", u.getUid());
                            }, Logger::e);
                }
            }

            @Override
            public void onError(UiError uiError) {

            }

            @Override
            public void onCancel() {

            }
        };
        this.mSinaAuthListener = new WeiboManager.SinaAuthListener() {
            @Override
            public void onComplete(WeiboLoginInfo loginInfo) {
                mLoginModel.registerWeiboUser(loginInfo)
                        .subscribe(u -> {

                            //1.保存信息
                            LoginManager.getInstance().setLoginUser(u);
                            LoginManager.getInstance().saveLoginUser(u);
                            //2.重置基本信息
                            AppInitManager.getInstance().initializeSDK();
                            //3.发送广播
                            RxBusManager.post(EventConstant.KEY_LOGIN, u);
                            //4.umeng
                            MobclickAgent.onProfileSignIn("weibo", u.getUid());
                        }, Logger::e);
            }

            @Override
            public void onError(Exception e) {
                dismissProgressDialog();
                if (e != null) {
                    UIHelper.shortToast(R.string.login_sina_error);
                } else {
                    UIHelper.shortToast(R.string.sina_not_install);
                }
            }

            @Override
            public void onCancel() {
                dismissProgressDialog();
            }
        };
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Logger.e("Jaython", "Login requestCode:" + requestCode + ";resultCode:" + resultCode);
        super.onActivityResult(requestCode, resultCode, data);
        //新浪的sso回调必须在这里添加
        if (32973 == requestCode) {
            SsoHandler ssoHandler = WeiboManager.getInstance().getSsoHandler();
            if (ssoHandler != null) {
                ssoHandler.authorizeCallBack(requestCode, resultCode, data);
            }
        } else {
            Tencent.onActivityResultData(requestCode, resultCode, data, mIUiListener);
        }
    }

    //QQ登录
    public void loginQQ() {
        if (!QQManager.getInstance().getTencent().isSessionValid()) {
            QQManager.getInstance().getTencent().login(this, QQManager.QQ_SCOPE, mIUiListener);
        }
    }

    //QQ获取用户信息
    public void getQQUserInfo(QQToken token) {
        UserInfo mInfo = new UserInfo(this, token);
        mInfo.getUserInfo(mIUiListener);
    }

    //微博登录
    public void loginWeibo() {
        WeiboManager.getInstance().loginSina(mSinaAuthListener, this);
    }
}
