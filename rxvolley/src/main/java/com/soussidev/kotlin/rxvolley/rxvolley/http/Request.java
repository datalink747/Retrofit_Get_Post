package com.soussidev.kotlin.rxvolley.rxvolley.http;

import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import com.soussidev.kotlin.rxvolley.rxvolley.client.HttpCallback;
import com.soussidev.kotlin.rxvolley.rxvolley.client.ProgressListener;
import com.soussidev.kotlin.rxvolley.rxvolley.client.RequestConfig;
import com.soussidev.kotlin.rxvolley.rxvolley.interf.ICache;
import com.soussidev.kotlin.rxvolley.rxvolley.toolbox.HttpParamsEntry;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Map;

/**
 * Created by Soussi on 08/10/2017.
 */

public abstract class Request <T> implements Comparable<Request<T>> {

    private final RequestConfig mConfig;

    public int mDefaultTrafficStatsTag;
    public boolean mResponseDelivered = false;
    public boolean mCanceled = false;

    public Object mTag;
    public Integer mSequence;

    protected final HttpCallback mCallback;
    protected ProgressListener mProgressListener;
    protected RequestQueue mRequestQueue;
    private ICache.Entry mCacheEntry = null;

    public Request(RequestConfig config, HttpCallback callback) {
        if (config == null) {
            config = new RequestConfig();
        }
        mConfig = config;
        mCallback = callback;
        mDefaultTrafficStatsTag = findDefaultTrafficStatsTag(config.mUrl);
    }

    /**
     * Set listener for tracking download progress
     *
     * @param listener
     */
    public void setOnProgressListener(ProgressListener listener) {
        mProgressListener = listener;
    }

    public int getMethod() {
        return mConfig.mMethod;
    }

    public RequestConfig getConfig() {
        return mConfig;
    }

    /**
     *
     */
    public Object getTag() {
        return mTag;
    }

    public void setTag(Object tag) {
        this.mTag = tag;
    }

    public HttpCallback getCallback() {
        return mCallback;
    }

    /**
     * @return A tag
     */
    public int getTrafficStatsTag() {
        return mDefaultTrafficStatsTag;
    }

    /**
     * @return The hashcode of the URL's host component, or 0 if there is none.
     */
    private static int findDefaultTrafficStatsTag(String url) {
        if (!TextUtils.isEmpty(url)) {
            Uri uri = Uri.parse(url);
            if (uri != null) {
                String host = uri.getHost();
                if (host != null) {
                    return host.hashCode();
                }
            }
        }
        return 0;
    }

    /**
     *
     */
    public void finish(String log) {
        Log.d("RxVolley", log);
        if (mRequestQueue != null) {
            mRequestQueue.finish(this);
        }
    }

    Request<?> setRequestQueue(RequestQueue requestQueue) {
        mRequestQueue = requestQueue;
        return this;
    }

    public final int getSequence() {
        if (mSequence == null) {
            throw new IllegalStateException(
                    "getSequence called before setSequence");
        }
        return mSequence;
    }

    void setSequence(int sequence) {
        this.mSequence = sequence;
    }

    public String getUrl() {
        return mConfig.mUrl;
    }

    public abstract String getCacheKey();

    Request<?> setCacheEntry(ICache.Entry entry) {
        mCacheEntry = entry;
        return this;
    }

    public ICache.Entry getCacheEntry() {
        return mCacheEntry;
    }

    public void cancel() {
        mCanceled = true;
    }

    public boolean isCanceled() {
        return mCanceled;
    }

    public ArrayList<HttpParamsEntry> getParams() {
        return null;
    }

    public ArrayList<HttpParamsEntry> getHeaders() {
        return new ArrayList<>();
    }

    protected String getParamsEncoding() {
        return mConfig.mEncoding;
    }

    public String getBodyContentType() {
        return "application/x-www-form-urlencoded; charset=" + getParamsEncoding();
    }

    /**
     *
     */
    public byte[] getBody() {
        ArrayList<HttpParamsEntry> params = getParams();
        if (params != null && params.size() > 0) {
            return encodeParameters(params, getParamsEncoding());
        }
        return null;
    }

    /**
     *
     */
    private byte[] encodeParameters(ArrayList<HttpParamsEntry> params,
                                    String paramsEncoding) {
        StringBuilder encodedParams = new StringBuilder();
        try {
            for (HttpParamsEntry entry : params) {
                encodedParams.append(URLEncoder.encode(entry.k, paramsEncoding));
                encodedParams.append('=');
                encodedParams.append(URLEncoder.encode(entry.v, paramsEncoding));
                encodedParams.append('&');
            }
            return encodedParams.toString().getBytes(paramsEncoding);
        } catch (UnsupportedEncodingException uee) {
            throw new RuntimeException("Encoding not supported: "
                    + paramsEncoding, uee);
        }
    }

    public boolean shouldCache() {
        return mConfig.mShouldCache == null ? false : mConfig.mShouldCache;
    }

    /**
     *
     */
    public enum Priority {
        LOW, NORMAL, HIGH, IMMEDIATE
    }

    public Priority getPriority() {
        return Priority.NORMAL;
    }

    public final int getTimeoutMs() {
        if (mConfig.mTimeout == 0) {
            return mConfig.mRetryPolicy.getCurrentTimeout();
        } else {
            return mConfig.mTimeout;
        }
    }

    /**
     * Returns the retry policy that should be used for this request.
     */
    public RetryPolicy getRetryPolicy() {
        return mConfig.mRetryPolicy;
    }

    /**
     *
     */
    public void markDelivered() {
        mResponseDelivered = true;
    }

    /**
     *
     */
    public boolean hasHadResponseDelivered() {
        return mResponseDelivered;
    }

    /**
     *
     *
     * @param response
     * @return HttpRespond, or null in the case of an error
     */
    abstract public Response<T> parseNetworkResponse(NetworkResponse response);

    /**
     *
     */
    protected VolleyError parseNetworkError(VolleyError volleyError) {
        return volleyError;
    }

    /**
     *
     *
     * @param response {@link #parseNetworkResponse(NetworkResponse)}
     */
    abstract protected void deliverResponse(Map<String, String> headers, T response);

    /**
     *
     *
     * @param error
     */
    public void deliverError(final VolleyError error) {
        final int errorNo;
        String strMsg;
        if (error != null) {
            if (error.networkResponse != null) {
                errorNo = error.networkResponse.statusCode;
            } else {
                errorNo = -1;
            }
            strMsg = error.getMessage();
        } else {
            errorNo = -1;
            strMsg = "unknow";
        }
        if (mCallback != null) {
            mCallback.onFailure(errorNo, strMsg);
            mCallback.onFailure(error);
        }
        getConfig().mSubject.onError(error);
    }

    public void deliverStartHttp() {
        if (mCallback != null) {
            mCallback.onPreHttp();
        }
    }

    /**
     *
     */
    public void requestFinish() {
        if (mCallback != null) {
            mCallback.onFinish();
        }
        getConfig().mSubject.onComplete();
    }

    /**
     *
     */
    @Override
    public int compareTo(Request<T> other) {
        Priority left = this.getPriority();
        Priority right = other.getPriority();
        return left == right ? mSequence - other.mSequence : right
                .ordinal() - left.ordinal();
    }

    @Override
    public String toString() {
        String trafficStatsTag = "0x" + Integer.toHexString(getTrafficStatsTag());
        return (mCanceled ? "[X] " : "[ ] ") + getUrl() + " " + trafficStatsTag
                + " " + getPriority() + " " + mSequence;
    }

    public int getCacheTime() {
        return mConfig.mCacheTime;
    }

    public boolean getUseServerControl() {
        return mConfig.mUseServerControl;
    }
}

