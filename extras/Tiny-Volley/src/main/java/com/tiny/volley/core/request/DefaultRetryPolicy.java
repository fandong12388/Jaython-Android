package com.tiny.volley.core.request;


import com.tiny.volley.core.exception.VolleyError;

/**
 * time: 2015/8/19
 * description:
 *
 * @author sunjianfei
 */
public class DefaultRetryPolicy implements RetryPolicy {
    public static final int DEFAULT_TIMEOUT_MS = 2500;
    public static final int DEFAULT_MAX_RETRIES = 0;
    public static final float DEFAULT_BACKOFF_MULT = 1.0F;
    private final int mMaxNumRetries;
    private final float mBackoffMultiplier;
    private int mCurrentTimeoutMs;
    private int mCurrentRetryCount;

    public DefaultRetryPolicy() {
        this(DEFAULT_TIMEOUT_MS, DEFAULT_MAX_RETRIES, DEFAULT_BACKOFF_MULT);
    }

    public DefaultRetryPolicy(int initialTimeoutMs, int maxNumRetries, float backoffMultiplier) {
        this.mCurrentTimeoutMs = initialTimeoutMs;
        this.mMaxNumRetries = maxNumRetries;
        this.mBackoffMultiplier = backoffMultiplier;
    }

    public int getCurrentTimeout() {
        return this.mCurrentTimeoutMs;
    }

    public int getCurrentRetryCount() {
        return this.mCurrentRetryCount;
    }

    public float getBackoffMultiplier() {
        return this.mBackoffMultiplier;
    }

    public void retry(VolleyError error) throws VolleyError {
        ++this.mCurrentRetryCount;
        this.mCurrentTimeoutMs = (int) ((float) this.mCurrentTimeoutMs + (float) this.mCurrentTimeoutMs * this.mBackoffMultiplier);
        if (!this.hasAttemptRemaining()) {
            throw error;
        }
    }

    protected boolean hasAttemptRemaining() {
        return this.mCurrentRetryCount <= this.mMaxNumRetries;
    }
}
