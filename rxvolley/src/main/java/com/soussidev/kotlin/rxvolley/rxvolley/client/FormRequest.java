package com.soussidev.kotlin.rxvolley.rxvolley.client;

import android.util.Log;

import com.soussidev.kotlin.rxvolley.rxvolley.RxVolley;
import com.soussidev.kotlin.rxvolley.rxvolley.http.HttpHeaderParser;
import com.soussidev.kotlin.rxvolley.rxvolley.http.NetworkResponse;
import com.soussidev.kotlin.rxvolley.rxvolley.http.Request;
import com.soussidev.kotlin.rxvolley.rxvolley.http.Response;
import com.soussidev.kotlin.rxvolley.rxvolley.rx.Result;
import com.soussidev.kotlin.rxvolley.rxvolley.toolbox.HttpParamsEntry;

import java.io.ByteArrayOutputStream;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Map;



/**
 * Created by Soussi on 08/10/2017.
 */

public class FormRequest extends Request<byte[]> {

    private final HttpParams mParams;

    public FormRequest(RequestConfig config, HttpParams params, HttpCallback callback) {
        super(config, callback);
        if (params == null) {
            params = new HttpParams();
        }
        this.mParams = params;
    }

    @Override
    public String getCacheKey() {
        if (getMethod() == RxVolley.Method.POST) {
            return getUrl() + mParams.getUrlParams();
        } else {
            return getUrl();
        }
    }

    @Override
    public String getBodyContentType() {
        if (mParams.getContentType() != null) {
            return mParams.getContentType();
        } else {
            return super.getBodyContentType();
        }
    }

    @Override
    public ArrayList<HttpParamsEntry> getHeaders() {
        return mParams.getHeaders();
    }

    @Override
    public byte[] getBody() {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            if (mProgressListener != null) {
                mParams.writeTo(new CountingOutputStream(bos, mParams.getContentLength(),
                        mProgressListener));
            } else {
                mParams.writeTo(bos);
            }
        } catch (IOException e) {
            Log.d("RxVolley", "FormRequest#getBody()--->IOException writing to ByteArrayOutputStream");
        }
        return bos.toByteArray();
    }

    @Override
    public Response<byte[]> parseNetworkResponse(NetworkResponse response) {
        return Response.success(response.data, response.headers,
                HttpHeaderParser.parseCacheHeaders(getUseServerControl(), getCacheTime(),
                        response));
    }

    @Override
    protected void deliverResponse(Map<String, String> headers, final byte[] response) {
        if (mCallback != null) {
            mCallback.onSuccess(headers, response);
        }
        getConfig().mSubject.onNext(new Result(getUrl(), response, headers));
    }

    @Override
    public Priority getPriority() {
        return Priority.IMMEDIATE;
    }

    public static class CountingOutputStream extends FilterOutputStream {
        private final ProgressListener progListener;
        private long transferred;
        private long fileLength;

        public CountingOutputStream(final OutputStream out, long fileLength,
                                    final ProgressListener listener) {
            super(out);
            this.fileLength = fileLength;
            this.progListener = listener;
            this.transferred = 0;
        }

        public void write(int b) throws IOException {
            out.write(b);
            if (progListener != null) {
                this.transferred++;
                if ((transferred % 20 == 0) && (transferred <= fileLength)) {
                    RxVolley.getRequestQueue().getDelivery().postProgress(this.progListener,
                            this.transferred, fileLength);
                }
            }
        }
    }
}
