package com.soussidev.kotlin.rxvolley.rxvolley.http;

import com.soussidev.kotlin.rxvolley.rxvolley.interf.ICache;

import java.util.Map;

/**
 * Created by Soussi on 08/10/2017.
 */

public class Response <T> {
    /**
     *
     */
    public final T result;

    /**
     *
     */
    public final ICache.Entry cacheEntry;

    public final VolleyError error;

    public final Map<String, String> headers;

    public boolean isSuccess() {
        return error == null;
    }

    private Response(T result, Map<String, String> headers,
                     ICache.Entry cacheEntry) {
        this.result = result;
        this.cacheEntry = cacheEntry;
        this.error = null;
        this.headers = headers;
    }

    private Response(VolleyError error) {
        this.result = null;
        this.cacheEntry = null;
        this.headers = null;
        this.error = error;
    }

    /**
     * HttpRespond
     *
     * @param result     Http
     * @param cacheEntry
     */
    public static <T> Response<T> success(T result, Map<String, String> headers,
                                          ICache.Entry cacheEntry) {
        return new Response<T>(result, headers, cacheEntry);
    }

    /**
     * HttpRespond
     *
     * @param error
     */
    public static <T> Response<T> error(VolleyError error) {
        return new Response<T>(error);
    }
}
