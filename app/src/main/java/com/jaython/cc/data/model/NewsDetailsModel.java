package com.jaython.cc.data.model;

import com.jaython.cc.bean.NewsComment;
import com.jaython.cc.bean.NewsDetailsPage;
import com.jaython.cc.bean.parser.NewsCommentParser;
import com.jaython.cc.data.constants.ApiConstant;
import com.jaython.cc.utils.Logger;
import com.jaython.cc.utils.ValidateUtil;
import com.tiny.volley.core.request.HttpGsonRequest;
import com.tiny.volley.core.request.Request;
import com.tiny.volley.core.request.RequestBuilder;
import com.tiny.volley.core.response.HttpResponse;

import java.util.List;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;

import static com.jaython.cc.data.pool.RequestPool.gRequestPool;

/**
 * time:2016/11/25
 * description:
 *
 * @author sunjianfei
 */
public class NewsDetailsModel {
    private String sp;
    private int id;

    public void setId(int id) {
        this.id = id;
    }

    /**
     * 获取资讯列表
     *
     * @return
     */
    public Observable<NewsDetailsPage> requestNewsContent() {
        //1.创建Request
        HttpGsonRequest<NewsDetailsPage> mRefreshRequest = RequestBuilder.create(NewsDetailsPage.class)
                .requestMethod(Request.Method.POST)
                .put("id", id)
                .url(ApiConstant.API_NEWS_DETAILS)
                .build();
        //2.进行数据的处理
        return gRequestPool.request(mRefreshRequest)
                .filter((resp) -> null != resp && null != resp.data)
                .map(HttpResponse::getData)
                .doOnNext(data -> {
                    List<NewsComment> list = data.getNewsComments();
                    if (ValidateUtil.isValidate(list)) {
                        sp = list.get(list.size() - 1).getCreated();
                    }
                })
                .observeOn(AndroidSchedulers.mainThread());
    }


    //分页获取评论
    public Observable<List<NewsComment>> requestNewsComment() {
        //1.创建Request
        HttpGsonRequest<List<NewsComment>> mRefreshRequest = RequestBuilder.<List<NewsComment>>create()
                .requestMethod(Request.Method.POST)
                .parser(new NewsCommentParser())
                .put("sp", sp)
                .put("newsId", id)
                .url(ApiConstant.API_NEWS_COMMENT)
                .build();
        //2.进行数据的处理
        return gRequestPool.request(mRefreshRequest)
                .filter((resp) -> null != resp)
                .map(HttpResponse::getData)
                .doOnNext(list -> {
                    if (ValidateUtil.isValidate(list)) {
                        sp = list.get(list.size() - 1).getCreated();
                    }
                })
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 提交评论
     *
     * @return
     */
    public Observable<String> submitComment(String content) {
        //1.创建Request
        HttpGsonRequest<String> mRefreshRequest = RequestBuilder.create(String.class)
                .requestMethod(Request.Method.POST)
                .put("newsId", id)
                .put("content", content)
                .url(ApiConstant.API_SUBMIT_COMMENT)
                .build();
        //2.进行数据的处理
        return gRequestPool.request(mRefreshRequest)
                .filter((resp) -> null != resp && null != resp.data)
                .map(HttpResponse::getData)
                .observeOn(AndroidSchedulers.mainThread());
    }

    //点赞/收藏
    public void requestNewsAction(Integer type) {
        //1.创建Request
        HttpGsonRequest<Boolean> mRefreshRequest = RequestBuilder.create(Boolean.class)
                .url(ApiConstant.API_NEWS_ACTION)
                .requestMethod(Request.Method.POST)
                .put("newsId", id)
                .put("type", type)
                .build();
        //2.进行数据的处理
        gRequestPool.request(mRefreshRequest)
                .subscribe(__ -> {
                }, Logger::e);
    }
}
