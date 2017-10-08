package com.soussidev.kotlin.rxvolley.rxvolley.http;

import android.annotation.TargetApi;
import android.net.TrafficStats;
import android.os.Build;
import android.os.Process;
import android.util.Log;

import com.soussidev.kotlin.rxvolley.rxvolley.interf.ICache;
import com.soussidev.kotlin.rxvolley.rxvolley.interf.IDelivery;
import com.soussidev.kotlin.rxvolley.rxvolley.interf.INetwork;

import java.util.concurrent.BlockingQueue;

/**
 * Created by Soussi on 08/10/2017.
 */

public class NetworkDispatcher extends Thread {
    private final BlockingQueue<Request<?>> mQueue;
    private final INetwork mNetwork;
    private final ICache mCache;
    private final IDelivery mDelivery;
    private volatile boolean mQuit = false;

    public NetworkDispatcher(BlockingQueue<Request<?>> queue, INetwork network, ICache cache,
                             IDelivery delivery) {
        mQueue = queue;
        mNetwork = network;
        mCache = cache;
        mDelivery = delivery;
    }

    /**
     *
     */
    public void quit() {
        mQuit = true;
        interrupt();
    }

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    private void addTrafficStatsTag(Request<?> request) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            TrafficStats.setThreadStatsTag(request.getTrafficStatsTag());
        }
    }

    /**
     *
     */
    @Override
    public void run() {
        Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);
        int stateCode = -1;
        while (true) {
            Request<?> request;
            try {
                request = mQueue.take();
            } catch (InterruptedException e) {
                if (mQuit) {
                    return;
                } else {
                    continue;
                }
            }
            try {
                if (request.isCanceled()) {
                    request.finish("任务已经取消");
                    continue;
                }
                mDelivery.postStartHttp(request);
                addTrafficStatsTag(request);
                NetworkResponse networkResponse = mNetwork.performRequest(request);
                stateCode = networkResponse.statusCode;

                if (networkResponse.notModified && request.hasHadResponseDelivered()) {
                    request.finish("已经分发过本响应");
                    continue;
                }
                Response<?> response = request.parseNetworkResponse(networkResponse);

                if (request.shouldCache() && response.cacheEntry != null) {
                    mCache.put(request.getCacheKey(), response.cacheEntry);
                }
                request.markDelivered();

                if (networkResponse.data != null) {
                    if (request.getCallback() != null) {
                        request.getCallback().onSuccessInAsync(networkResponse.data);
                    }
                }
                mDelivery.postResponse(request, response);
            } catch (VolleyError volleyError) {
                parseAndDeliverNetworkError(request, volleyError, stateCode);
            } catch (Exception e) {
                Log.d("RxVolley", String.format("Unhandled exception %s", e.getMessage()));
                parseAndDeliverNetworkError(request, new VolleyError(e), stateCode);
            }
        }
    }

    private void parseAndDeliverNetworkError(Request<?> request, VolleyError error, int stateCode) {
        error = request.parseNetworkError(error);
        mDelivery.postError(request, error);
    }
}
