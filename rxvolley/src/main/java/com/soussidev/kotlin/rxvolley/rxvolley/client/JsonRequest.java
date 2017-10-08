package com.soussidev.kotlin.rxvolley.rxvolley.client;

import android.util.Log;

import com.soussidev.kotlin.rxvolley.rxvolley.RxVolley;
import com.soussidev.kotlin.rxvolley.rxvolley.http.HttpHeaderParser;
import com.soussidev.kotlin.rxvolley.rxvolley.http.NetworkResponse;
import com.soussidev.kotlin.rxvolley.rxvolley.http.Request;
import com.soussidev.kotlin.rxvolley.rxvolley.http.Response;
import com.soussidev.kotlin.rxvolley.rxvolley.rx.Result;
import com.soussidev.kotlin.rxvolley.rxvolley.toolbox.HttpParamsEntry;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Map;



/**
 * Created by Soussi on 08/10/2017.
 */

public class JsonRequest extends Request<byte[]> {

    private final String mRequestBody;
    private final HttpParams mParams;

    public JsonRequest(RequestConfig config, HttpParams params, HttpCallback callback) {
        super(config, callback);
        mRequestBody = params.getJsonParams();
        mParams = params;
    }

    @Override
    public ArrayList<HttpParamsEntry> getHeaders() {
        return mParams.getHeaders();
    }

    @Override
    protected void deliverResponse(Map<String, String> headers, byte[] response) {
        if (mCallback != null) {
            mCallback.onSuccess(headers, response);
        }
        getConfig().mSubject.onNext(new Result(getUrl(), response, headers));
    }

    @Override
    public Response<byte[]> parseNetworkResponse(NetworkResponse response) {
        return Response.success(response.data, response.headers,
                HttpHeaderParser.parseCacheHeaders(getUseServerControl(), getCacheTime(),
                        response));
    }

    @Override
    public String getBodyContentType() {
        return String.format("application/json; charset=%s", getConfig().mEncoding);
    }

    @Override
    public String getCacheKey() {
        if (getMethod() == RxVolley.Method.POST) {
            return getUrl() + mParams.getJsonParams();
        } else {
            return getUrl();
        }
    }

    @Override
    public byte[] getBody() {
        try {
            return mRequestBody == null ? null : mRequestBody.getBytes(getConfig().mEncoding);
        } catch (UnsupportedEncodingException uee) {
            Log.d("RxVolley", String.format("Unsupported Encoding while trying to get the bytes of %s" +
                    " using %s", mRequestBody, getConfig().mEncoding));
            return null;
        }
    }

    @Override
    public Priority getPriority() {
        return Priority.IMMEDIATE;
    }
}
