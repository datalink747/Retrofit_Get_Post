package com.soussidev.kotlin.rxvolley.rxvolley.http;

import android.util.Log;

import com.kymjs.common.FileUtils;
import com.soussidev.kotlin.rxvolley.rxvolley.client.FileRequest;
import com.soussidev.kotlin.rxvolley.rxvolley.interf.ICache;
import com.soussidev.kotlin.rxvolley.rxvolley.interf.IHttpStack;
import com.soussidev.kotlin.rxvolley.rxvolley.interf.INetwork;
import com.soussidev.kotlin.rxvolley.rxvolley.toolbox.ByteArrayPool;
import com.soussidev.kotlin.rxvolley.rxvolley.toolbox.HttpParamsEntry;
import com.soussidev.kotlin.rxvolley.rxvolley.toolbox.HttpStatus;
import com.soussidev.kotlin.rxvolley.rxvolley.toolbox.PoolingByteArrayOutputStream;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

/**
 * Created by Soussi on 08/10/2017.
 */

public class Network implements INetwork {
    protected final IHttpStack mHttpStack;

    public Network(IHttpStack httpStack) {
        mHttpStack = httpStack;
    }

    /**

     *
     * @param request
     * @return
     * @throws VolleyError
     */
    @Override
    public NetworkResponse performRequest(Request<?> request) throws VolleyError {
        while (true) {
            URLHttpResponse httpResponse = null;
            byte[] responseContents = null;
            HashMap<String, String> responseHeaders = new HashMap<>();
            try {

                ArrayList<HttpParamsEntry> headers = new ArrayList<>();

                addCacheHeaders(headers, request.getCacheEntry());
                httpResponse = mHttpStack.performRequest(request, headers);

                int statusCode = httpResponse.getResponseCode();
                responseHeaders = httpResponse.getHeaders();

                if (statusCode == HttpStatus.SC_NOT_MODIFIED) { // 304
                    return new NetworkResponse(HttpStatus.SC_NOT_MODIFIED,
                            request.getCacheEntry() == null ? null : request.getCacheEntry().data,
                            responseHeaders, true);
                }

                if (httpResponse.getContentStream() != null) {
                    if (request instanceof FileRequest) {
                        responseContents = ((FileRequest) request).handleResponse(httpResponse);
                    } else {
                        responseContents = entityToBytes(httpResponse);
                    }
                } else {
                    responseContents = new byte[0];
                }

                if (statusCode < 200 || statusCode > 299) {
                    throw new IOException();
                }
                return new NetworkResponse(statusCode, responseContents, responseHeaders, false);
            } catch (SocketTimeoutException e) {
                attemptRetryOnException("socket", request, new VolleyError(
                        new SocketTimeoutException("socket timeout")));
            } catch (MalformedURLException e) {
                attemptRetryOnException("connection", request,
                        new VolleyError("Bad URL " + request.getUrl(), e));
            } catch (IOException e) {
                int statusCode;
                NetworkResponse networkResponse;
                if (httpResponse != null) {
                    statusCode = httpResponse.getResponseCode();
                } else {
                    throw new VolleyError("NoConnection error", e);
                }
                Log.d("RxVolley", String.format(Locale.getDefault(), "Unexpected response code %d for %s", statusCode,
                        request.getUrl()));
                if (responseContents != null) {
                    networkResponse = new NetworkResponse(statusCode, responseContents,
                            responseHeaders, false);
                    if (statusCode == HttpStatus.SC_UNAUTHORIZED
                            || statusCode == HttpStatus.SC_FORBIDDEN) {
                        attemptRetryOnException("auth", request, new VolleyError(networkResponse));
                    } else {
                        throw new VolleyError(networkResponse);
                    }
                } else {
                    throw new VolleyError(String.format(Locale.getDefault(), "Unexpected response code %d for %s",
                            statusCode, request.getUrl()));
                }
            }
        }
    }

    /**
     * Attempts to prepare the request for a retry. If there are no more
     * attempts remaining in the request's retry policy, a timeout exception is
     * thrown.
     *
     * @param request The request to use.
     */
    private static void attemptRetryOnException(String logPrefix, Request<?> request,
                                                VolleyError exception) throws VolleyError {
        RetryPolicy retryPolicy = request.getRetryPolicy();
        int oldTimeout = request.getTimeoutMs();

        try {
            if (retryPolicy != null) {
                retryPolicy.retry(exception);
            } else {
                Log.d("RxVolley", "not retry policy");
            }
        } catch (VolleyError e) {
            Log.d("RxVolley", String.format("%s-timeout-giveup [timeout=%s]", logPrefix, oldTimeout));
            throw e;
        }
        Log.d("RxVolley", String.format("%s-retry [timeout=%s]", logPrefix, oldTimeout));
    }

    /**
     *
     */
    private void addCacheHeaders(ArrayList<HttpParamsEntry> headers, ICache.Entry entry) {
        if (entry == null) {
            return;
        }
        if (entry.etag != null) {
            headers.add(new HttpParamsEntry("If-None-Match", entry.etag));
        }
        if (entry.serverDate > 0) {
            Date refTime = new Date(entry.serverDate);
            DateFormat sdf = SimpleDateFormat.getDateTimeInstance();
            headers.add(new HttpParamsEntry("If-Modified-Since", sdf.format(refTime)));

        }
    }

    /**
     *
     *
     * @throws IOException
     * @throws VolleyError
     */
    private byte[] entityToBytes(URLHttpResponse httpResponse) throws IOException,
            VolleyError {
        PoolingByteArrayOutputStream bytes = new PoolingByteArrayOutputStream(
                ByteArrayPool.get(), (int) httpResponse.getContentLength());
        byte[] buffer = null;
        try {
            InputStream in = httpResponse.getContentStream();
            if (in == null) {
                throw new VolleyError("server error");
            }
            buffer = ByteArrayPool.get().getBuf(1024);
            int count;
            while ((count = in.read(buffer)) != -1) {
                bytes.write(buffer, 0, count);
            }
            return bytes.toByteArray();
        } finally {
            FileUtils.closeIO(httpResponse.getContentStream());
            ByteArrayPool.get().returnBuf(buffer);
            FileUtils.closeIO(bytes);
        }
    }
}