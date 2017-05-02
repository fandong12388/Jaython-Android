package com.tiny.volley.core.request;


import com.tiny.volley.bean.HttpParams;
import com.tiny.volley.core.HttpManager;
import com.tiny.volley.core.exception.AuthFailureError;

/**
 * time: 15/6/15
 * description: 上传图片对应的请求实体
 *
 * @author sunjianfei
 */
public class HttpMultipartRequest<T> extends HttpGsonRequest<T> {

    public HttpMultipartRequest(String url, HttpParams params) {
        super(Method.POST, url, params);
    }


    @Override
    public String getBodyContentType() {
        return "multipart/form-data; boundary=" + HttpManager.BOUNDARY;
    }

    @Override
    public byte[] getBody() throws AuthFailureError {
        byte[] body = HttpManager.buildPostParams(mHttpParams);
        if (body == null) {
            return super.getBody();
        } else {
            return body;
        }
    }

}
