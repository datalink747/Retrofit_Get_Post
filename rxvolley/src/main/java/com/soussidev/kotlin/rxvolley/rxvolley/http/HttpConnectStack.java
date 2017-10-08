package com.soussidev.kotlin.rxvolley.rxvolley.http;

import com.soussidev.kotlin.rxvolley.rxvolley.RxVolley;
import com.soussidev.kotlin.rxvolley.rxvolley.interf.IHttpStack;
import com.soussidev.kotlin.rxvolley.rxvolley.toolbox.HTTPSTrustManager;
import com.soussidev.kotlin.rxvolley.rxvolley.toolbox.HttpParamsEntry;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSocketFactory;

/**
 * Created by Soussi on 08/10/2017.
 */

public class HttpConnectStack implements IHttpStack {

    private static final String HEADER_CONTENT_TYPE = "Content-Type";

    private final UrlRewriter mUrlRewriter;
    private final SSLSocketFactory mSslSocketFactory;

    public interface UrlRewriter {
        /**
         *
         */
        String rewriteUrl(String originalUrl);
    }

    public HttpConnectStack() {
        this(null);
    }

    public HttpConnectStack(UrlRewriter urlRewriter) {
        this(urlRewriter, null);
    }

    public HttpConnectStack(UrlRewriter urlRewriter, SSLSocketFactory sslSocketFactory) {
        mUrlRewriter = urlRewriter;
        if (sslSocketFactory == null) {
            try {
                Class certificateUtilsClass = Class.forName("com.kymjs.okhttp3.CertificateUtils");
                Method method = certificateUtilsClass.getDeclaredMethod("getDefaultSSLSocketFactory");
                sslSocketFactory = (SSLSocketFactory) method.invoke(null);
            } catch (Exception e) {
            } finally {
                mSslSocketFactory = sslSocketFactory;
            }
        } else {
            mSslSocketFactory = sslSocketFactory;
        }
    }

    @Override
    public URLHttpResponse performRequest(Request<?> request,
                                          ArrayList<HttpParamsEntry> additionalHeaders)
            throws IOException {
        String url = request.getUrl();
        ArrayList<HttpParamsEntry> header = new ArrayList<>();
        header.addAll(request.getHeaders());
        header.addAll(additionalHeaders);

        if (mUrlRewriter != null) {
            String rewritten = mUrlRewriter.rewriteUrl(url);
            if (rewritten == null) {
                throw new IOException("URL blocked by rewriter: " + url);
            }
            url = rewritten;
        }
        URL parsedUrl = new URL(url);
        HttpURLConnection connection = openConnection(parsedUrl, request);
        for (HttpParamsEntry entry : header) {
            connection.addRequestProperty(entry.k, entry.v);
        }
        setConnectionParametersForRequest(connection, request);
        return responseFromConnection(connection);
    }

    private URLHttpResponse responseFromConnection(HttpURLConnection connection)
            throws IOException {
        URLHttpResponse response = new URLHttpResponse();
        //contentStream
        InputStream inputStream;
        try {
            inputStream = connection.getInputStream();
        } catch (IOException ioe) {
            inputStream = connection.getErrorStream();
        }
        int responseCode = connection.getResponseCode();
        if (responseCode == -1) {
            throw new IOException(
                    "Could not retrieve response code from HttpUrlConnection.");
        }
        response.setResponseCode(responseCode);
        response.setResponseMessage(connection.getResponseMessage());

        response.setContentStream(inputStream);

        response.setContentLength(connection.getContentLength());
        response.setContentEncoding(connection.getContentEncoding());
        response.setContentType(connection.getContentType());
        //header
        HashMap<String, String> headerMap = new HashMap<>();
        for (Map.Entry<String, List<String>> header : connection.getHeaderFields()
                .entrySet()) {
            if (header.getKey() != null) {
                StringBuilder value = new StringBuilder();
                for (String v : header.getValue()) {
                    value.append(v).append(";");
                }
                headerMap.put(header.getKey(), value.toString());
            }
        }
        response.setHeaders(headerMap);
        return response;
    }

    private HttpURLConnection openConnection(URL url, Request<?> request)
            throws IOException {
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        int timeoutMs = request.getTimeoutMs();
        connection.setConnectTimeout(timeoutMs);
        connection.setReadTimeout(timeoutMs);
        connection.setUseCaches(false);
        connection.setDoInput(true);

        // use caller-provided custom SslSocketFactory, if any, for HTTPS
        if ("https".equals(url.getProtocol())) {
            if (mSslSocketFactory != null) {
                ((HttpsURLConnection) connection)
                        .setSSLSocketFactory(mSslSocketFactory);
            } else {

                HTTPSTrustManager.allowAllSSL();
            }
        }
        return connection;
    }

    /* package */
    static void setConnectionParametersForRequest(HttpURLConnection connection, Request<?> request)
            throws IOException {
        switch (request.getMethod()) {
            case RxVolley.Method.GET:
                connection.setRequestMethod("GET");
                break;
            case RxVolley.Method.DELETE:
                connection.setRequestMethod("DELETE");
                break;
            case RxVolley.Method.POST:
                connection.setRequestMethod("POST");
                addBodyIfExists(connection, request);
                break;
            case RxVolley.Method.PUT:
                connection.setRequestMethod("PUT");
                addBodyIfExists(connection, request);
                break;
            case RxVolley.Method.HEAD:
                connection.setRequestMethod("HEAD");
                break;
            case RxVolley.Method.OPTIONS:
                connection.setRequestMethod("OPTIONS");
                break;
            case RxVolley.Method.TRACE:
                connection.setRequestMethod("TRACE");
                break;
            case RxVolley.Method.PATCH:
                connection.setRequestMethod("PATCH");
                addBodyIfExists(connection, request);
                break;
            default:
                throw new IllegalStateException("Unknown method type.");
        }
    }

    /**
     *
     */
    private static void addBodyIfExists(HttpURLConnection connection, Request<?> request)
            throws IOException {
        byte[] body = request.getBody();
        if (body != null) {
            connection.setDoOutput(true);
            connection.addRequestProperty(HEADER_CONTENT_TYPE,
                    request.getBodyContentType());
            DataOutputStream out = new DataOutputStream(
                    connection.getOutputStream());
            out.write(body);
            out.close();
        }
    }
}