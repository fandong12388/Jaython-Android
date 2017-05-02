package com.tiny.volley.core.exception;


import com.tiny.volley.core.response.NetworkResponse;

/**
 * time: 2015/8/19
 * description:
 *
 * @author sunjianfei
 */
public class ServerError extends VolleyError {
    public ServerError(NetworkResponse networkResponse) {
        super(networkResponse);
    }

    public ServerError() {
    }
}
