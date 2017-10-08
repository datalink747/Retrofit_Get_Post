package com.soussidev.kotlin.rxvolley.rxvolley.rx;

import com.soussidev.kotlin.rxvolley.rxvolley.http.Response;
import com.soussidev.kotlin.rxvolley.rxvolley.http.VolleyError;

import java.util.Map;

/**
 * Created by Soussi on 08/10/2017.
 */

public class Result {
    public String url;
    public byte[] data;
    public VolleyError error;
    public Map<String, String> headers;
    public int errorCode;

    public boolean isSuccess() {
        return error == null;
    }

    public Result(String url, Response<byte[]> response) {
        this.url = url;
        this.data = response.result;
        this.error = response.error;
        this.headers = response.headers;
    }

    public Result(String url, byte[] result, Map<String, String> headers) {
        this.url = url;
        this.data = result;
        this.error = null;
        this.headers = headers;
    }

    public Result(String url, byte[] result) {
        this.url = url;
        this.data = result;
        this.error = null;
        this.headers = null;
    }

    public Result(String url, VolleyError error, int errorCode) {
        this.url = url;
        this.error = error;
        this.errorCode = errorCode;
    }
}
