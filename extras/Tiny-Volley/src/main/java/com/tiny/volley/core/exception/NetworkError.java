package com.tiny.volley.core.exception;


import com.tiny.volley.bean.ErrorCode;
import com.tiny.volley.core.response.NetworkResponse;

/**
 * time: 2015/8/19
 * description:
 *
 * @author sunjianfei
 */
public class NetworkError extends VolleyError {
    private ErrorCode mErrorCode;

    public NetworkError() {
    }

    public NetworkError(Throwable cause) {
        super(cause);
    }

    public NetworkError(NetworkResponse networkResponse) {
        super(networkResponse);
    }

    public ErrorCode getErrorCode() {
        return mErrorCode;
    }

    public void setErrorCode(ErrorCode code) {
        this.mErrorCode = code;
    }
}
