package com.soussidev.kotlin.rxvolley.rxvolley.client;

/**
 * Created by Soussi on 08/10/2017.
 */

public interface ProgressListener {

    void onProgress(long transferredBytes, long totalSize);
}
