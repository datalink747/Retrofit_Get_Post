package com.soussidev.kotlin.rxvolley.rxvolley.client;

import android.graphics.Bitmap;

import com.soussidev.kotlin.rxvolley.rxvolley.http.VolleyError;

import java.util.Map;

/**
 * Created by Soussi on 08/10/2017.
 */

public abstract class HttpCallback {

    public void onPreStart() {
    }

    /**
     *
     */
    public void onPreHttp() {
    }

    /**
     *
     *
     * @param t
     */
    public void onSuccessInAsync(byte[] t) {
    }

    /**
     *
     *
     * @param t HttpRequest
     */
    public void onSuccess(String t) {
    }

    /**
     *
     *
     * @param headers HttpRespond
     * @param t       HttpRequest
     */
    public void onSuccess(Map<String, String> headers, byte[] t) {
        onSuccess(new String(t));
    }

    /**
     * Http
     *
     * @param errorNo
     * @param strMsg
     */
    public void onFailure(int errorNo, String strMsg) {
    }

    /**
     * Http
     *
     */
    public void onFailure(VolleyError error) {
    }

    /**
     *
     */
    public void onFinish() {
    }

    /**
     *
     */
    public void onSuccess(Map<String, String> headers, Bitmap bitmap) {
    }

}
