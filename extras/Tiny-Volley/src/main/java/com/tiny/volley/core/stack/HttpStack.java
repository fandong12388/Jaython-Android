package com.tiny.volley.core.stack;

import com.tiny.volley.core.exception.VolleyError;
import com.tiny.volley.core.request.Request;
import com.tiny.volley.core.response.CustomResponse;

import java.io.IOException;
import java.util.Map;


/**
 * time: 2015/8/19
 * description:
 *
 * @author sunjianfei
 */
public interface HttpStack {
    CustomResponse performRequest(Request<?> request, Map<String, String> params) throws IOException, VolleyError;
}
