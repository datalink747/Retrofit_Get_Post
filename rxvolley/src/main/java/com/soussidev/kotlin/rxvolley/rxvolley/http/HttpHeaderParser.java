package com.soussidev.kotlin.rxvolley.rxvolley.http;

import android.util.Log;

import com.soussidev.kotlin.rxvolley.rxvolley.interf.ICache;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Map;

/**
 * Created by Soussi on 08/10/2017.
 */

public class HttpHeaderParser {


    public static ICache.Entry parseCacheHeaders(boolean useServerControl, int cacheTime,
                                                 NetworkResponse response) {
        long now = System.currentTimeMillis();

        Map<String, String> headers = response.headers;
        long serverDate = 0;
        long maxAge = 0;
        boolean hasCacheControl = false;
        String serverEtag = null;
        String tempStr;

        tempStr = headers.get("Date");
        if (tempStr != null) {
            serverDate = parseDateAsEpoch(tempStr);
        }


        tempStr = headers.get("Cache-Control");
        if (tempStr != null) {
            hasCacheControl = true;
            String[] tokens = tempStr.split(",");
            for (String token : tokens) {
                token = token.trim();

                if (token.equals("no-cache") || token.equals("no-store")) {
                    return null;
                } else if (token.startsWith("max-age=")) {
                    try {

                        maxAge = Long.parseLong(token.substring(8));
                    } catch (Exception e) {
                        Log.d("RxVolley", HttpHeaderParser.class.getName() + e.getMessage());
                    }
                } else if (token.equals("must-revalidate") || token.equals("proxy-revalidate")) {

                    maxAge = 0;
                }
            }
        }

        long serverExpires = 0;
        tempStr = headers.get("Expires");
        if (tempStr != null) {
            serverExpires = parseDateAsEpoch(tempStr);
        }

        long softExpire = 0;
        serverEtag = headers.get("ETag");
        if (hasCacheControl) {
            softExpire = now + maxAge * 1000;
        } else if (serverDate > 0 && serverExpires >= serverDate) {
            softExpire = now + (serverExpires - serverDate);
        }

        ICache.Entry entry = new ICache.Entry();
        entry.data = response.data;

        if (useServerControl) {
            entry.ttl = softExpire;
        } else {
            entry.ttl = now + cacheTime * 60000L;
        }
        entry.etag = serverEtag;
        entry.serverDate = serverDate;
        entry.responseHeaders = headers;
        return entry;
    }

    /**
     *
     *
     * @return
     */
    public static long parseDateAsEpoch(String dateStr) {
        SimpleDateFormat sdf = new SimpleDateFormat();
        try {
            return sdf.parse(dateStr).getTime();
        } catch (ParseException e) {
            return 0;
        }
    }

    /**
     *
     */
    public static String parseCharset(Map<String, String> headers, String defaultCharset) {
        String contentType = headers.get("Content-Type");
        if (contentType != null) {
            String[] params = contentType.split(";");
            for (int i = 1; i < params.length; i++) {
                String[] pair = params[i].trim().split("=");
                if (pair.length == 2) {
                    if (pair[0].equals("charset")) {
                        return pair[1];
                    }
                }
            }
        }
        return defaultCharset;
    }

    /**
     * Returns the charset specified in the Content-Type of this header,
     * or the HTTP default (ISO-8859-1) if none can be found.
     */
    public static String parseCharset(Map<String, String> headers) {
        return parseCharset(headers, "ISO-8859-1");
    }


}
