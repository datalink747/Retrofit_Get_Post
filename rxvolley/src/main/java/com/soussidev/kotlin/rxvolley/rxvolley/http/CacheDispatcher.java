package com.soussidev.kotlin.rxvolley.rxvolley.http;

import android.os.Process;
import android.util.Log;

import com.soussidev.kotlin.rxvolley.rxvolley.interf.ICache;
import com.soussidev.kotlin.rxvolley.rxvolley.interf.IDelivery;
import com.soussidev.kotlin.rxvolley.rxvolley.interf.IPersistence;
import com.soussidev.kotlin.rxvolley.rxvolley.rx.RxBus;

import java.util.concurrent.BlockingQueue;

/**
 * Created by Soussi on 08/10/2017.
 */

public class CacheDispatcher extends Thread {

    private final BlockingQueue<Request<?>> mCacheQueue;
    private final BlockingQueue<Request<?>> mNetworkQueue;
    private final ICache mCache;
    private final IDelivery mDelivery;
    private final RxBus mPoster;

    private volatile boolean mQuit = false;

    /**
     *
     *
     * @param cacheQueue
     * @param networkQueue
     */
    public CacheDispatcher(BlockingQueue<Request<?>> cacheQueue, BlockingQueue<Request<?>>
            networkQueue, ICache cache, IDelivery delivery) {
        mCacheQueue = cacheQueue;
        mNetworkQueue = networkQueue;
        mCache = cache;
        mDelivery = delivery;
        mPoster = RxBus.getDefault();
    }

    /**
     *
     */
    public void quit() {
        mQuit = true;
        interrupt();
    }

    /**
     *
     */
    @Override
    public void run() {
        Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);
        mCache.initialize();
        while (true) {
            try {
                final Request<?> request = mCacheQueue.take();
                if (request.isCanceled()) {
                    request.finish("cache-discard-canceled");
                    continue;
                }

                ICache.Entry entry = mCache.get(request.getCacheKey());
                if (entry == null) {
                    mNetworkQueue.put(request);
                    continue;
                }


                if (entry.isExpired() && !(request instanceof IPersistence)) {
                    request.setCacheEntry(entry);
                    mNetworkQueue.put(request);
                    continue;
                }


                Response<?> response = request.parseNetworkResponse(new NetworkResponse(entry.data,
                        entry.responseHeaders));
                Log.d("RxVolley", "CacheDispatcher：http resopnd from cache");
                sleep(request.getConfig().mDelayTime);
                if (request.getCallback() != null) {
                    request.getCallback().onSuccessInAsync(entry.data);
                }

                mDelivery.postResponse(request, response);
            } catch (InterruptedException e) {
                if (mQuit) {
                    return;
                }
            }
        }
    }
}
