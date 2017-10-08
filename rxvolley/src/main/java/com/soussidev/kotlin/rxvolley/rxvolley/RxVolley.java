package com.soussidev.kotlin.rxvolley.rxvolley;

import android.text.TextUtils;

import com.kymjs.common.FileUtils;
import com.soussidev.kotlin.rxvolley.rxvolley.client.FileRequest;
import com.soussidev.kotlin.rxvolley.rxvolley.client.FormRequest;
import com.soussidev.kotlin.rxvolley.rxvolley.client.HttpCallback;
import com.soussidev.kotlin.rxvolley.rxvolley.client.HttpParams;
import com.soussidev.kotlin.rxvolley.rxvolley.client.JsonRequest;
import com.soussidev.kotlin.rxvolley.rxvolley.client.ProgressListener;
import com.soussidev.kotlin.rxvolley.rxvolley.client.RequestConfig;
import com.soussidev.kotlin.rxvolley.rxvolley.http.DefaultRetryPolicy;
import com.soussidev.kotlin.rxvolley.rxvolley.http.Request;
import com.soussidev.kotlin.rxvolley.rxvolley.http.RequestQueue;
import com.soussidev.kotlin.rxvolley.rxvolley.http.RetryPolicy;
import com.soussidev.kotlin.rxvolley.rxvolley.interf.ICache;
import com.soussidev.kotlin.rxvolley.rxvolley.rx.Result;

import java.io.File;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;

/**
 * Created by Soussi on 08/10/2017.
 */

public class RxVolley {

    private RxVolley() {
    }

    public final static File CACHE_FOLDER = FileUtils.getExternalCacheDir("RxVolley");

    private static RequestQueue sRequestQueue;

    /**
     *
     */
    public synchronized static RequestQueue getRequestQueue() {
        if (sRequestQueue == null) {
            sRequestQueue = RequestQueue.newRequestQueue(CACHE_FOLDER);
        }
        return sRequestQueue;
    }

    /**
     *
     *
     * @return
     */
    public synchronized static boolean setRequestQueue(RequestQueue queue) {
        if (sRequestQueue == null) {
            sRequestQueue = queue;
            return true;
        } else {
            return false;
        }
    }

    /**
     *
     */
    public interface ContentType {
        int FORM = 0;
        int JSON = 1;
    }

    /**
     *
     */
    public interface Method {
        int GET = 0;
        int POST = 1;
        int PUT = 2;
        int DELETE = 3;
        int HEAD = 4;
        int OPTIONS = 5;
        int TRACE = 6;
        int PATCH = 7;
    }

    /**
     *
     */
    public static class Builder {
        private HttpParams params;
        private int contentType;
        private HttpCallback callback;
        private Request<?> request;
        private ProgressListener progressListener;
        private RequestConfig httpConfig = new RequestConfig();

        /**
         *
         */
        public Builder params(HttpParams params) {
            this.params = params;
            return this;
        }

        /**
         *
         */
        public Builder contentType(int contentType) {
            this.contentType = contentType;
            return this;
        }

        /**
         *
         */
        public Builder callback(HttpCallback callback) {
            this.callback = callback;
            return this;
        }

        /**
         * HttpRequest
         */
        public Builder setRequest(Request<?> request) {
            this.request = request;
            return this;
        }

        /**
         *
         */
        public Builder setTag(Object tag) {
            this.httpConfig.mTag = tag;
            return this;
        }


        /**
         *
         */
        public Builder httpConfig(RequestConfig httpConfig) {
            this.httpConfig = httpConfig;
            return this;
        }

        /**
         * 3000ms
         */
        public Builder timeout(int timeout) {
            this.httpConfig.mTimeout = timeout;
            return this;
        }

        /**
         *
         *
         * @param listener
         */
        public Builder progressListener(ProgressListener listener) {
            this.progressListener = listener;
            return this;
        }

        /**
         *
         */
        public Builder delayTime(int delayTime) {
            this.httpConfig.mDelayTime = delayTime;
            return this;
        }

        /**
         *
         */
        public Builder cacheTime(int cacheTime) {
            this.httpConfig.mCacheTime = cacheTime;
            return this;
        }

        /**
         *
         */
        public Builder cacheTime(int cacheTime, TimeUnit timeUnit) {
            this.httpConfig.mCacheTime = cacheTime;
            return this;
        }

        /**
         * cacheTime())
         */
        public Builder useServerControl(boolean useServerControl) {
            this.httpConfig.mUseServerControl = useServerControl;
            return this;
        }

        /**
         * RequestConfig$Method
         * GET/POST/PUT/DELETE/HEAD/OPTIONS/TRACE/PATCH
         */
        public Builder httpMethod(int httpMethod) {
            this.httpConfig.mMethod = httpMethod;
            if (httpMethod == Method.POST) {
                this.httpConfig.mShouldCache = false;
            }
            return this;
        }

        /**
         *
         */
        public Builder shouldCache(boolean shouldCache) {
            this.httpConfig.mShouldCache = shouldCache;
            return this;
        }

        /**
         *url
         */
        public Builder url(String url) {
            this.httpConfig.mUrl = url;
            return this;
        }

        /**
         *
         */
        public Builder retryPolicy(RetryPolicy retryPolicy) {
            this.httpConfig.mRetryPolicy = retryPolicy;
            return this;
        }

        /**
         * UTF-8
         */
        public Builder encoding(String encoding) {
            this.httpConfig.mEncoding = encoding;
            return this;
        }

        private Builder build() {
            if (request == null) {
                if (params == null) {
                    params = new HttpParams();
                } else {
                    if (httpConfig.mMethod == Method.GET)
                        httpConfig.mUrl += params.getUrlParams();
                }

                if (httpConfig.mShouldCache == null) {

                    if (httpConfig.mMethod == Method.GET) {
                        httpConfig.mShouldCache = Boolean.TRUE;
                    } else {
                        httpConfig.mShouldCache = Boolean.FALSE;
                    }
                }

                if (contentType == ContentType.JSON) {
                    request = new JsonRequest(httpConfig, params, callback);
                } else {
                    request = new FormRequest(httpConfig, params, callback);
                }

                request.setTag(httpConfig.mTag);
                request.setOnProgressListener(progressListener);

                if (TextUtils.isEmpty(httpConfig.mUrl)) {
                    throw new RuntimeException("Request url is empty");
                }
            }
            if (callback != null) {
                callback.onPreStart();
            }
            return this;
        }

        /**
         *
         */
        public Observable<Result> getResult() {
            doTask();
            return httpConfig.mSubject;
        }

        /**
         *
         */
        public void doTask() {
            build();
            getRequestQueue().add(request);
        }
    }

    public static void get(String url, HttpCallback callback) {
        new Builder().url(url).callback(callback).doTask();
    }

    public static void get(String url, HttpParams params, HttpCallback callback) {
        new Builder().url(url).params(params).callback(callback).doTask();
    }

    public static void post(String url, HttpParams params, HttpCallback callback) {
        new Builder().url(url).params(params).httpMethod(Method.POST).callback(callback).doTask();
    }

    public static void post(String url, HttpParams params, ProgressListener listener,
                            HttpCallback callback) {
        new Builder().url(url).params(params).progressListener(listener).httpMethod(Method.POST)
                .callback(callback).doTask();
    }

    public static void jsonGet(String url, HttpParams params, HttpCallback callback) {
        new Builder().url(url).params(params).contentType(ContentType.JSON)
                .httpMethod(Method.GET).callback(callback).doTask();
    }

    public static void jsonPost(String url, HttpParams params, HttpCallback callback) {
        new Builder().url(url).params(params).contentType(ContentType.JSON)
                .httpMethod(Method.POST).callback(callback).doTask();
    }

    /**
     *
     *
     * @param url
     * @return
     */
    public static byte[] getCache(String url) {
        ICache cache = getRequestQueue().getCache();
        if (cache != null) {
            ICache.Entry entry = cache.get(url);
            if (entry != null) {
                return entry.data;
            }
        }
        return new byte[0];
    }

    /**
     *
     *
     * @param storeFilePath
     * @param url
     * @param progressListener
     * @param callback
     */
    public static FileRequest download(String storeFilePath, String url, ProgressListener
            progressListener, HttpCallback callback) {
        RequestConfig config = new RequestConfig();
        config.mUrl = url;
        config.mRetryPolicy = new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS,
                20, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        FileRequest request = new FileRequest(storeFilePath, config, callback);
        request.setTag(url);
        request.setOnProgressListener(progressListener);
        new Builder().setRequest(request).doTask();
        return request;
    }


}
