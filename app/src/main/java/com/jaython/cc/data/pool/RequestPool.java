package com.jaython.cc.data.pool;


import com.tiny.volley.bean.ErrorCode;
import com.tiny.volley.core.exception.NetworkError;
import com.tiny.volley.core.network.NetworkExecutor;
import com.tiny.volley.core.request.Request;
import com.tiny.volley.core.response.HttpResponse;
import com.tiny.volley.core.response.NetworkResponse;
import com.tiny.volley.core.stack.HttpUrlStack;

import java.util.Hashtable;

import rx.Observable;
import rx.schedulers.Schedulers;

/**
 * time: 2015/8/19
 * description:
 *
 * @author sunjianfei
 */
public class RequestPool {

    public static final RequestPool gRequestPool = new RequestPool();

    private Hashtable<String, Observable<?>> mObservables;

    private NetworkExecutor mNetwork;

    private RequestPool() {
    }

    public void init() {
        //1.初始化容器
        mObservables = new Hashtable<>();
        //2.构建网络执行器
        HttpUrlStack stack = new HttpUrlStack();
        mNetwork = new NetworkExecutor(stack);
    }


    public <T> Observable<HttpResponse<T>> request(Request<T> request) {
        String requestId = request.genRequestId();
        if (!mObservables.containsKey(requestId)) {
            Observable<HttpResponse<T>> observable = Observable.<HttpResponse<T>>create(subscriber -> {
                try {
                    //1.请求网络
                    NetworkResponse response = mNetwork.performRequest(request);
                    HttpResponse<T> httpResponse = request.parseNetworkResponse(response);
                    //2.发出事件
                    if (200 == httpResponse.status) {
                        subscriber.onNext(httpResponse);
                        subscriber.onCompleted();
                    } else {
                        ErrorCode code = ErrorCode.handleCode(httpResponse);
                        NetworkError error = new NetworkError();
                        error.setErrorCode(code);
                        subscriber.onError(error);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    NetworkError error = new NetworkError();
                    error.setErrorCode(ErrorCode.FAIL404);
                    subscriber.onError(error);
                } finally {
                    mObservables.remove(request.genRequestId());
                }
            }).subscribeOn(Schedulers.io());
            mObservables.put(requestId, observable);
            return observable;
        } else {
            return (Observable<HttpResponse<T>>) mObservables.get(requestId);
        }
    }

    private <T> Observable<HttpResponse<T>> getErrorObservable(HttpResponse<T> httpResponse) {
        ErrorCode code = ErrorCode.handleCode(httpResponse);
        NetworkError error = new NetworkError();
        error.setErrorCode(code);
        return Observable.<HttpResponse<T>>create(subscriber -> {
            subscriber.onError(error);
            subscriber.onCompleted();
        }).subscribeOn(Schedulers.computation());
    }


}
