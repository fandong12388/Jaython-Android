package com.tiny.volley.core.network;


import com.tiny.volley.core.exception.VolleyError;
import com.tiny.volley.core.request.Request;
import com.tiny.volley.core.response.NetworkResponse;

/**
 * time: 2015/8/19
 * description:
 *
 * @author sunjianfei
 */
public interface Network {

    /**
     * 发送请求，包含了retry机制
     *
     * @param request 请求实体
     * @return
     * @throws VolleyError
     */
    NetworkResponse performRequest(Request<?> request) throws VolleyError;
}
