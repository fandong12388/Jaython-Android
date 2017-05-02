package com.jaython.cc.data.model;

import android.text.TextUtils;

import com.jaython.cc.bean.QQUser;
import com.jaython.cc.bean.User;
import com.jaython.cc.bean.WeiboUser;
import com.jaython.cc.bean.share.WeiboLoginInfo;
import com.jaython.cc.data.constants.ApiConstant;
import com.jaython.cc.data.manager.WeiboManager;
import com.jaython.cc.data.pool.RequestPool;
import com.tencent.connect.auth.QQToken;
import com.tiny.volley.core.request.HttpGsonRequest;
import com.tiny.volley.core.request.Request;
import com.tiny.volley.core.request.RequestBuilder;
import com.tiny.volley.core.response.HttpResponse;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;

/**
 * time:2017/1/14
 * description:
 *
 * @author fandong
 */
public class LoginModel {

    public Observable<User> registerQQUser(QQToken token, QQUser user) {
        HttpGsonRequest<User> request = RequestBuilder.<User>create(User.class)
                .url(ApiConstant.API_USER_LOGIN)
                .put("openId", token.getOpenId())
                .put("nickname", user.getNickname())
                .put("sex", user.getGender().equals("男") ? 1 : 2)
                .put("province", user.getProvince())
                .put("city", user.getCity())
                .put("headimgurl", user.getFigureurl_qq_2())
                .put("platform", "qq")
                .put("unionId", token.getOpenId())
                .build();
        return RequestPool.gRequestPool.request(request)
                .filter(resp -> resp != null && resp.data != null)
                .map(HttpResponse::getData)
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<User> registerWeiboUser(WeiboLoginInfo loginInfo) {
        //首先获取微博用户信息
        HttpGsonRequest<WeiboUser> request = RequestBuilder.<WeiboUser>create(WeiboUser.class)
                .requestMethod(Request.Method.GET)
                .url(WeiboManager.URL_GET_USER_INFO)
                .paramsType(RequestBuilder.TYPE_NO_NEED_BASE)
                .put("access_token", loginInfo.getToken())
                .put("uid", loginInfo.getUid())
                .build();

        return RequestPool.gRequestPool.request(request)
                .filter(resp -> resp != null && resp.data != null)
                .map(HttpResponse::getData)
                .switchMap(user -> {
                    String province = user.getProvince();
                    String city = user.getCity();
                    if (!TextUtils.isEmpty(user.getLocation())) {
                        String location = user.getLocation();
                        String[] addr = location.split(" ");
                        if (addr.length > 1) {
                            province = addr[0];
                            city = addr[1];
                        }
                    }
                    HttpGsonRequest<User> userReq = RequestBuilder.<User>create(User.class)
                            .url(ApiConstant.API_USER_LOGIN)
                            .put("openId", user.getId())
                            .put("nickname", user.getScreen_name())
                            .put("sex", ("m").equals(user.getGender()) ? 1 : 2)
                            .put("province", province)
                            .put("city", city)
                            .put("headimgurl", user.getProfile_image_url())
                            .put("platform", "sina")
                            .put("unionId", user.getId())
                            .build();
                    return RequestPool.gRequestPool.request(userReq)
                            .filter(resp -> resp != null && resp.data != null)
                            .map(HttpResponse::getData)
                            .observeOn(AndroidSchedulers.mainThread());
                });
    }
}
