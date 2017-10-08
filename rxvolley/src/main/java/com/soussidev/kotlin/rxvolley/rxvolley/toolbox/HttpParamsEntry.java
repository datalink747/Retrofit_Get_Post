package com.soussidev.kotlin.rxvolley.rxvolley.toolbox;

import android.support.annotation.NonNull;

/**
 * Created by Soussi on 08/10/2017.
 */

public class HttpParamsEntry implements Comparable<HttpParamsEntry> {
    public String k;
    public String v;

    @Override
    public boolean equals(Object o) {
        if (o instanceof HttpParamsEntry) {
            return k.equals(((HttpParamsEntry) o).k);
        } else {
            return super.equals(o);
        }
    }

    @Override
    public int hashCode() {
        return k.hashCode();
    }

    public HttpParamsEntry(String key, String value) {
        k = key;
        v = value;
    }

    @Override
    public int compareTo(@NonNull HttpParamsEntry another) {
        if (k == null) {
            return -1;
        } else {
            return k.compareTo(another.k);
        }
    }
}
