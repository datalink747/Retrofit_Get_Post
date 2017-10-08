package com.soussidev.kotlin.rxvolley.rxvolley.interf;

import com.soussidev.kotlin.rxvolley.rxvolley.client.ProgressListener;
import com.soussidev.kotlin.rxvolley.rxvolley.http.Request;
import com.soussidev.kotlin.rxvolley.rxvolley.http.Response;
import com.soussidev.kotlin.rxvolley.rxvolley.http.VolleyError;

/**
 * Created by Soussi on 08/10/2017.
 */

public interface IDelivery {


    /**
     *
     *
     * @param request
     * @param response
     */
    void postResponse(Request<?> request, Response<?> response);

    /**
     *
     *
     * @param request
     * @param error
     */
    void postError(Request<?> request, VolleyError error);

    void postResponse(Request<?> request, Response<?> response, Runnable runnable);

    /**
     *
     */
    void postStartHttp(Request<?> request);

    /**
     *
     *
     * @param transferredBytes
     * @param totalSize
     */
    void postProgress(ProgressListener listener, long transferredBytes, long totalSize);

}
