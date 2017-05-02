package com.jaython.cc.data.model;

import com.jaython.cc.bean.Dynamic;
import com.jaython.cc.bean.DynamicComment;
import com.jaython.cc.bean.parser.DynamicParser;
import com.jaython.cc.data.constants.ApiConstant;
import com.jaython.cc.utils.BitmapUtil;
import com.jaython.cc.utils.ValidateUtil;
import com.qiniu.http.Response;
import com.qiniu.storage.UploadManager;
import com.tiny.volley.core.request.HttpGsonRequest;
import com.tiny.volley.core.request.Request;
import com.tiny.volley.core.request.RequestBuilder;
import com.tiny.volley.core.response.HttpResponse;
import com.tiny.volley.utils.GsonUtil;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static com.jaython.cc.data.pool.RequestPool.gRequestPool;

/**
 * time: 17/1/29
 * description:
 *
 * @author fandong
 */
public class DynamicModel {
    private String mSp;

    public void setSp(String sp) {
        this.mSp = sp;
    }

    //发布一条动态
    public Observable<Boolean> publish(Dynamic dynamic) {
        //1.首先上传图片
        return Observable.<Dynamic>create(subscribe -> {
            try {
                if (ValidateUtil.isValidate(dynamic.getImages())) {
                    upload(dynamic.getImages());
                }
                subscribe.onNext(dynamic);
            } catch (Exception e) {
                subscribe.onError(e);
            } finally {
                subscribe.onCompleted();
            }
        }).subscribeOn(Schedulers.io())
                .switchMap(this::request);
    }

    //上传数据
    private Observable<Boolean> request(Dynamic dynamic) {
        //1.创建Request
        HttpGsonRequest<Boolean> request = RequestBuilder.<Boolean>create()
                .requestMethod(Request.Method.POST)
                .url(ApiConstant.API_DYNAMIC_PUBLISH)
                .put("data", GsonUtil.toJson(dynamic))
                .build();
        //2.进行数据的处理
        return gRequestPool.request(request)
                .filter((resp) -> null != resp)
                .map(HttpResponse::getData)
                .observeOn(AndroidSchedulers.mainThread());
    }


    private void upload(List<String> pictures) {
        try {
            for (int i = 0; i < pictures.size(); i++) {
                String picture = pictures.get(i);
                String target = BitmapUtil.compressBitmap(picture);
                if (null != target) {
                    //文件名称
                    int lastIndex = target.lastIndexOf("/");
                    String fileName = target.substring(lastIndex + 1);
                    String url = ApiConstant.QINIU_PREFIX + fileName;
                    String token = ApiConstant.QINIUAUTH.uploadToken(ApiConstant.QINIU_BUCKET, fileName, 3600, null);
                    Response response = new UploadManager().put(target, fileName, token);
                    if (response.isOK()) {
                        pictures.remove(i);
                        pictures.add(i, url);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    //下拉刷新
    public Observable<ArrayList<Dynamic>> refresh() {
        return request(true);
    }


    //加载更多
    public Observable<ArrayList<Dynamic>> loadMore() {
        return request(false);
    }

    //获取动态
    public Observable<ArrayList<Dynamic>> request(boolean refresh) {
        //1.创建Request
        HttpGsonRequest<ArrayList<Dynamic>> mRefreshRequest = RequestBuilder.<ArrayList<Dynamic>>create()
                .requestMethod(Request.Method.POST)
                .parser(new DynamicParser())
                .put("sp", refresh ? "" : mSp)
                .url(ApiConstant.API_DYNAMIC_LIST)
                .build();
        //2.进行数据的处理
        return gRequestPool.request(mRefreshRequest)
                .filter((resp) -> null != resp)
                .map(HttpResponse::getData)
                .doOnNext(list -> {
                    if (ValidateUtil.isValidate(list)) {
                        mSp = list.get(list.size() - 1).getCreated();
                    }
                })
                .observeOn(AndroidSchedulers.mainThread());
    }

    //点赞
    public void requestPraise(Integer dynamicId) {
        if (dynamicId < 0) return;
        //1.创建Request
        HttpGsonRequest<Boolean> request = RequestBuilder.<Boolean>create(Boolean.class)
                .requestMethod(Request.Method.POST)
                .put("dynamicId", dynamicId)
                .url(ApiConstant.API_DYNAMIC_PRAISE)
                .build();
        //2.进行数据的处理
        gRequestPool.request(request)
                .subscribe();
    }

    //评论
    public void requestComment(DynamicComment dynamicComment) {
        if (dynamicComment.getDynamicId() < 0) return;
        //1.创建Request
        HttpGsonRequest<Boolean> request = RequestBuilder.<Boolean>create(Boolean.class)
                .requestMethod(Request.Method.POST)
                .put("dynamicId", dynamicComment.getDynamicId())
                .put("type", dynamicComment.getType())
                .put("content", dynamicComment.getContent())
                .url(ApiConstant.API_DYNAMIC_COMMENT)
                .build();
        //2.进行数据的处理
        gRequestPool.request(request)
                .subscribe();
    }

}
