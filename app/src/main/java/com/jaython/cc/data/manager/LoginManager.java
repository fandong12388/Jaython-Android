package com.jaython.cc.data.manager;

import android.text.TextUtils;

import com.jaython.cc.JaythonApplication;
import com.jaython.cc.bean.User;
import com.jaython.cc.data.cache.ACache;
import com.jaython.cc.data.constants.SPConstant;
import com.jaython.cc.utils.PreferenceUtil;
import com.tiny.volley.utils.GsonUtil;

/**
 * time: 15/7/20
 * description:封装用户登陆信息的管理器
 *
 * @author sunjianfei
 */
public class LoginManager {

    private static final LoginManager gManager = new LoginManager();

    private User mLoginUser;

    private LoginManager() {

    }

    public static LoginManager getInstance() {
        return gManager;
    }

    public User getLoginUser() {
        if (mLoginUser == null) {
            isLoginValidate();
        }
        return mLoginUser;
    }

    public void setLoginUser(User user) {
        mLoginUser = user;
    }

    public String getUid() {
        if (null != mLoginUser) {
            return mLoginUser.getUid();
        }
        return null;
    }

    public boolean isLoginValidate() {
        if (mLoginUser != null) {
            return true;
        }
        String user = PreferenceUtil.getString(SPConstant.KEY_USER);
        if (!TextUtils.isEmpty(user)) {
            try {
                User loginUser = GsonUtil.fromJson(user, User.class);
                if (null != loginUser) {
                    mLoginUser = loginUser;
                    return true;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    public void saveLoginUser(User user) {
        //1.赋值
        mLoginUser = user;
        //2.保存
        PreferenceUtil.putString(SPConstant.KEY_USER, GsonUtil.toJson(user));
    }

    /**
     * 判断user是否是当前用户
     */
    public boolean isOwner(User user) {
        if (user == null) {
            return false;
        }
        String userId = user.getUid();
        if (TextUtils.isEmpty(userId)) {
            return false;
        }
        User hostUser = mLoginUser;

        if (hostUser == null) {
            return false;
        }

        if (userId.equals(hostUser.getUid())) {
            return true;
        } else {
            return false;
        }
    }

    public void logout() {
        mLoginUser = null;
        PreferenceUtil.remove(SPConstant.KEY_USER);
        AppInitManager.loginOut();
        ACache.get(JaythonApplication.gContext).clear();
    }
}
