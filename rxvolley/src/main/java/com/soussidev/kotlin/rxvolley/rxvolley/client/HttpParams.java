package com.soussidev.kotlin.rxvolley.rxvolley.client;

import android.text.TextUtils;
import android.util.Log;

import com.kymjs.common.FileUtils;
import com.soussidev.kotlin.rxvolley.rxvolley.toolbox.HttpParamsEntry;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

/**
 * Created by Soussi on 08/10/2017.
 */

public class HttpParams {

    private final static char[] MULTIPART_CHARS =
            "-_1234567890abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();
    private String mBoundary = null;
    private static final String NEW_LINE_STR = "\r\n";
    private static final String CONTENT_TYPE = "Content-Type: ";
    private static final String CONTENT_DISPOSITION = "Content-Disposition: ";

    public static final String CHARSET = "UTF-8";


    private static final String TYPE_TEXT_CHARSET = String.format("text/plain; charset=%s",
            CHARSET);


    private static final String TYPE_OCTET_STREAM = "application/octet-stream";


    private static final byte[] BINARY_ENCODING = "Content-Transfer-Encoding: binary\r\n\r\n"
            .getBytes();

    private static final byte[] BIT_ENCODING = "Content-Transfer-Encoding: 8bit\r\n\r\n".getBytes();

    private final ArrayList<HttpParamsEntry> urlParams = new ArrayList<>(8);
    private final ArrayList<HttpParamsEntry> mHeaders = new ArrayList<>(4);

    private final ByteArrayOutputStream mOutputStream = new ByteArrayOutputStream();
    private boolean hasFile;
    private String contentType = null;

    private String jsonParams;

    public HttpParams() {
        this.mBoundary = generateBoundary();
    }

    /**
     *
     */
    private String generateBoundary() {
        final StringBuilder buf = new StringBuilder();
        final Random rand = new Random();
        for (int i = 0; i < 30; i++) {
            buf.append(MULTIPART_CHARS[rand.nextInt(MULTIPART_CHARS.length)]);
        }
        return buf.toString();
    }

    public void putHeaders(final String key, final int value) {
        this.putHeaders(key, value + "");
    }

    public void putHeaders(final String key, final String value) {
        mHeaders.add(new HttpParamsEntry(key, value));
    }

    public void put(final String key, final int value) {
        this.put(key, value + "");
    }

    public void putJsonParams(String json) {
        this.jsonParams = json;
    }

    /**
     *
     */
    public void put(final String key, final String value) {
        urlParams.add(new HttpParamsEntry(key, value));
        writeToOutputStream(key, value.getBytes(), TYPE_TEXT_CHARSET,
                BIT_ENCODING, "");
    }

    /**

     */
    public void put(String paramName, final byte[] rawData) {
        hasFile = true;
        writeToOutputStream(paramName, rawData, TYPE_OCTET_STREAM,
                BINARY_ENCODING, "RxVolleyFile");
    }

    /**
     *
     */
    public void put(final String key, final File file) {
        try {
            hasFile = true;
            writeToOutputStream(key, FileUtils.input2byte(new FileInputStream(file)),
                    TYPE_OCTET_STREAM, BINARY_ENCODING, file.getName());
        } catch (FileNotFoundException e) {
            Log.d("RxVolley", "HttpParams.put()-> file not found");
        }
    }

    /**
     *
     *
     * @param key
     * @param rawData
     * @param type
     * @param fileName
     *
     */
    public void put(final String key, final byte[] rawData, String type, String fileName) {
        hasFile = true;
        if (TextUtils.isEmpty(fileName)) {
            fileName = "RxVolleyFile";
        }
        writeToOutputStream(key, rawData, type, BINARY_ENCODING, fileName);
    }

    /**
     *
     */
    private void writeToOutputStream(String paramName, byte[] rawData,
                                     String type, byte[] encodingBytes, String fileName) {
        try {
            writeFirstBoundary();
            mOutputStream
                    .write((CONTENT_TYPE + type + NEW_LINE_STR).getBytes());
            mOutputStream
                    .write(getContentDispositionBytes(paramName, fileName));
            mOutputStream.write(encodingBytes);
            mOutputStream.write(rawData);
            mOutputStream.write(NEW_LINE_STR.getBytes());
        } catch (final IOException e) {
            e.printStackTrace();
        }
    }

    /**
     *
     *
     * @throws IOException
     */
    private void writeFirstBoundary() throws IOException {
        mOutputStream.write(("--" + mBoundary + "\r\n").getBytes());
    }

    private byte[] getContentDispositionBytes(String paramName, String fileName) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("--").append(mBoundary).append("\r\n").append(CONTENT_DISPOSITION)
                .append("form-data; name=\"").append(paramName).append("\"");
        if (!TextUtils.isEmpty(fileName)) {
            stringBuilder.append("; filename=\"").append(fileName).append("\"");
        }
        return stringBuilder.append(NEW_LINE_STR).toString().getBytes();
    }

    public long getContentLength() {
        return mOutputStream.toByteArray().length;
    }

    public String getContentType() {

        if (hasFile && contentType == null) {
            contentType = "multipart/form-data; boundary=" + mBoundary;
        }
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public boolean isChunked() {
        return false;
    }

    public boolean isRepeatable() {
        return false;
    }

    public boolean isStreaming() {
        return false;
    }

    public void writeTo(final OutputStream outstream) throws IOException {
        if (hasFile) {

            final String endString = "--" + mBoundary + "--\r\n";

            mOutputStream.write(endString.getBytes());
            //
            outstream.write(mOutputStream.toByteArray());
        } else if (!TextUtils.isEmpty(getUrlParams())) {
            outstream.write(getUrlParams().substring(1).getBytes());
        }
    }

    public void consumeContent() throws IOException,
            UnsupportedOperationException {
        if (isStreaming()) {
            throw new UnsupportedOperationException(
                    "Streaming entity does not implement #consumeContent()");
        }
    }

    public InputStream getContent() {
        return new ByteArrayInputStream(mOutputStream.toByteArray());
    }

    public StringBuilder getUrlParams() {
        StringBuilder result = new StringBuilder();
        boolean isFirst = true;
        Collections.sort(urlParams);

        for (HttpParamsEntry entry : urlParams) {
            if (!isFirst) {
                result.append("&");
            } else {
                result.append("?");
                isFirst = false;
            }
            try {
                result.append(URLEncoder.encode(entry.k, CHARSET)).append("=").
                        append(URLEncoder.encode(entry.v, CHARSET));
            } catch (UnsupportedEncodingException e) {
                result.append(entry.k).append("=").append(entry.v);
            }
        }
        return result;
    }

    public String getJsonParams() {
        return jsonParams;
    }

    public ArrayList<HttpParamsEntry> getHeaders() {
        mHeaders.add(new HttpParamsEntry("Accept-Encoding", "identity"));
        return mHeaders;
    }


}
