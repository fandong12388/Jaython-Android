package com.jaython.cc.data.model;

import com.jaython.cc.bean.NewsItem;
import com.jaython.cc.bean.parser.NewsParser;
import com.jaython.cc.data.constants.ApiConstant;
import com.jaython.cc.data.constants.SPConstant;
import com.jaython.cc.utils.PreferenceUtil;
import com.jaython.cc.utils.ValidateUtil;
import com.tiny.volley.core.request.HttpGsonRequest;
import com.tiny.volley.core.request.Request;
import com.tiny.volley.core.request.RequestBuilder;
import com.tiny.volley.core.response.HttpResponse;

import java.util.List;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;

import static com.jaython.cc.data.pool.RequestPool.gRequestPool;

/**
 * time:2016/11/25
 * description:资讯ViewModel
 *
 * @author sunjianfei
 */
public class NewsModel {
    private String mSp;
    private String mAdSp;

    public void setSp(String sp) {
        this.mSp = sp;
    }

    //下拉刷新
    public Observable<List<NewsItem>> refresh(Action0 clearAdapter) {
        return request(true)
                .doOnNext(__ -> {
                    if (null != clearAdapter) clearAdapter.call();
                });
    }

    //加载更多
    public Observable<List<NewsItem>> loadMore() {
        return request(false);
    }


    //获取资讯列表
    public Observable<List<NewsItem>> request(boolean refresh) {
        //1.创建Request
        HttpGsonRequest<List<NewsItem>> mRefreshRequest = RequestBuilder.<List<NewsItem>>create()
                .requestMethod(Request.Method.POST)
                .parser(new NewsParser())
                .put("city", PreferenceUtil.getString(SPConstant.KEY_CITY))
                .put("sp", refresh ? "" : mSp)
                .put("adSp", refresh ? "" : mAdSp)
                .url(ApiConstant.API_NEWS_LIST)
                .build();
        //2.进行数据的处理
        return gRequestPool.request(mRefreshRequest)
                .filter((resp) -> null != resp)
                .map(HttpResponse::getData)
                .doOnNext(list -> {
                    if (ValidateUtil.isValidate(list)) {
                        mSp = list.get(list.size() - 1).getShelvesTime();
                    }
                })
                .observeOn(AndroidSchedulers.mainThread());
    }

    //下拉刷新
    public Observable<List<NewsItem>> refreshCollect() {
        return requestCollect(true);
    }

    //加载更多
    public Observable<List<NewsItem>> loadMoreCollect() {
        return requestCollect(false);
    }

    //获取资讯列表
    public Observable<List<NewsItem>> requestCollect(boolean refresh) {
        //1.创建Request
        HttpGsonRequest<List<NewsItem>> mRefreshRequest = RequestBuilder.<List<NewsItem>>create()
                .requestMethod(Request.Method.POST)
                .parser(new NewsParser())
                .put("city", PreferenceUtil.getString(SPConstant.KEY_CITY))
                .put("sp", refresh ? "" : mSp)
                .put("adSp", refresh ? "" : mAdSp)
                .put("type", 1)
                .url(ApiConstant.API_NEWS_COLLECT_LIST)
                .build();
        //2.进行数据的处理
        return gRequestPool.request(mRefreshRequest)
                .filter((resp) -> null != resp)
                .map(HttpResponse::getData)
                .doOnNext(list -> {
                    if (ValidateUtil.isValidate(list)) {
                        mSp = list.get(list.size() - 1).getShelvesTime();
                    }
                })
                .observeOn(AndroidSchedulers.mainThread());
    }
}
