package com.jaython.cc.data.model;

import com.jaython.cc.bean.ActionGroup;
import com.jaython.cc.bean.parser.ActionGroupParser;
import com.jaython.cc.data.constants.ApiConstant;
import com.tiny.volley.core.request.HttpGsonRequest;
import com.tiny.volley.core.request.RequestBuilder;
import com.tiny.volley.core.response.HttpResponse;

import java.util.List;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;

import static com.jaython.cc.data.pool.RequestPool.gRequestPool;


/**
 * time: 2017/1/13
 * description:
 *
 * @author fandong
 */
public class HomeModel {


    //获取动作的分组
    public Observable<List<ActionGroup>> requestActionGroup() {
        //1.创建request
        HttpGsonRequest<List<ActionGroup>> request = RequestBuilder.<List<ActionGroup>>create()
                .parser(new ActionGroupParser())
                .url(ApiConstant.API_ACTIONS)
                .build();
        //2.进行数据处理
        return gRequestPool.request(request)
                .filter((resp) -> null != resp && null != resp.data)
                .map(HttpResponse::getData)
                .observeOn(AndroidSchedulers.mainThread());
    }
}
