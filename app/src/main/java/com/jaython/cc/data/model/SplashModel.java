package com.jaython.cc.data.model;

import android.graphics.Bitmap;
import android.text.TextUtils;
import android.view.View;

import com.jaython.cc.bean.AdvData;
import com.jaython.cc.data.constants.ApiConstant;
import com.jaython.cc.data.constants.SPConstant;
import com.jaython.cc.utils.Logger;
import com.jaython.cc.utils.PreferenceUtil;
import com.tiny.loader.TinyImageLoader;
import com.tiny.loader.core.listener.ImageLoadingAdapterListener;
import com.tiny.volley.core.request.HttpGsonRequest;
import com.tiny.volley.core.request.RequestBuilder;
import com.tiny.volley.utils.GsonUtil;

import rx.Observable;

import static com.jaython.cc.data.pool.RequestPool.gRequestPool;

/**
 * time:2016/6/21
 * description:
 *
 * @author sunjianfei
 */
public class SplashModel {

    ImageLoadingAdapterListener listener = new ImageLoadingAdapterListener() {
        @Override
        public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
            PreferenceUtil.putString(SPConstant.KEY_SPLASH_PIC, imageUri);
        }
    };

    public void getSplashData() {
        request().map(AdvData::getImageUrl)
                .filter(url -> !TextUtils.isEmpty(url))
                .doOnNext(url -> {
                    TinyImageLoader.create(url)
                            .setImageLoadinglistener(listener)
                            .load();
                }).subscribe(Logger::e, Logger::e);
    }

    /**
     * 刷新网络数据
     */
    private Observable<AdvData> request() {
        //1.创建Request
        HttpGsonRequest<AdvData> mRefreshRequest = RequestBuilder.create(AdvData.class)
                .url(ApiConstant.API_SPLASH_ADV)
                .build();
        //2.进行数据的处理
        return gRequestPool.request(mRefreshRequest)
                .filter((resp) -> null != resp && null != resp.data)
                .map(resp -> {
                    AdvData bean = resp.getData();
                    if (null == bean) {
                        //如果为空,则清除缓存广告
                        PreferenceUtil.remove(SPConstant.KEY_SPLASH);
                    } else {
                        PreferenceUtil.putString(SPConstant.KEY_SPLASH, GsonUtil.toJson(bean));
                    }
                    return bean;
                });

    }

}
