package com.jaython.cc.data.model;

import com.jaython.cc.bean.ActionCompose;
import com.jaython.cc.bean.parser.ActionComposeParser;
import com.jaython.cc.data.constants.ApiConstant;
import com.jaython.cc.data.pool.RequestPool;
import com.tiny.volley.core.request.HttpGsonRequest;
import com.tiny.volley.core.request.RequestBuilder;
import com.tiny.volley.core.response.HttpResponse;

import java.util.List;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;

/**
 * time:2017/1/14
 * description:
 *
 * @author fandong
 */
public class ComposeModel {

    public Observable<List<ActionCompose>> requestActionCompose() {
        return null;
    }


    public Observable<List<ActionCompose>> refreshActionCompose() {
        HttpGsonRequest<List<ActionCompose>> request = RequestBuilder.<List<ActionCompose>>create()
                .parser(new ActionComposeParser())
                .url(ApiConstant.API_COMPOSE)
                .put("pageSize", 10)
                .build();

        return RequestPool.gRequestPool.request(request)
                .filter(resp -> resp != null && !resp.data.isEmpty())
                .map(HttpResponse::getData)
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<List<ActionCompose>> loadMoreActionCompose(String sp) {
        HttpGsonRequest<List<ActionCompose>> request = RequestBuilder.<List<ActionCompose>>create()
                .parser(new ActionComposeParser())
                .url(ApiConstant.API_COMPOSE)
                .put("pageSize", 10)
                .put("sp", sp)
                .build();

        return RequestPool.gRequestPool.request(request)
                .filter(resp -> resp != null)
                .map(HttpResponse::getData)
                .observeOn(AndroidSchedulers.mainThread());
    }

    public void collectCompose(String uid, Integer composeId) {
        HttpGsonRequest<Boolean> request = RequestBuilder.<Boolean>create()
                .url(ApiConstant.API_COLLECT_COMPOSE)
                .put("composeId", composeId)
                .put("uid", uid)
                .build();

        RequestPool.gRequestPool.request(request)
                .filter(resp -> resp != null)
                .map(HttpResponse::getData)
                .subscribe();
    }

    public Observable<ActionCompose> getActionCompose(Integer id, String uid) {
        HttpGsonRequest<ActionCompose> request = RequestBuilder.<ActionCompose>create(ActionCompose.class)
                .url(ApiConstant.API_COMPOSE_DETAIL)
                .put("composeId", id)
                .put("uid", uid)
                .build();

        return RequestPool.gRequestPool.request(request)
                .filter(resp -> resp != null)
                .map(HttpResponse::getData)
                .observeOn(AndroidSchedulers.mainThread());
    }

    //分页获取
    public Observable<List<ActionCompose>> refreshCollectCompose() {
        HttpGsonRequest<List<ActionCompose>> request = RequestBuilder.<List<ActionCompose>>create()
                .parser(new ActionComposeParser())
                .url(ApiConstant.API_COMPOSE_COLLECT)
                .put("pageSize", 10)
                .build();

        return RequestPool.gRequestPool.request(request)
                .filter(resp -> resp != null)
                .map(HttpResponse::getData)
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<List<ActionCompose>> loadMoreCollectCompose(String sp) {
        HttpGsonRequest<List<ActionCompose>> request = RequestBuilder.<List<ActionCompose>>create()
                .parser(new ActionComposeParser())
                .url(ApiConstant.API_COMPOSE_COLLECT)
                .put("pageSize", 10)
                .put("sp", sp)
                .build();

        return RequestPool.gRequestPool.request(request)
                .filter(resp -> resp != null)
                .map(HttpResponse::getData)
                .observeOn(AndroidSchedulers.mainThread());
    }
}
