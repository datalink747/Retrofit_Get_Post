package com.soussidev.kotlin.rxvolley.rxvolley.interf;

import java.util.Collections;
import java.util.Map;

/**
 * Created by Soussi on 08/10/2017.
 */

public interface ICache {
    Entry get(String key);

    void put(String key, Entry entry);

    void remove(String key);

    void clear();

    /**
     *
     */
    void initialize();

    /**
     *
     *
     * @param key        Cache key
     * @param fullExpire True to fully expire the entry, false to soft expire
     */
    void invalidate(String key, boolean fullExpire);

    /**
     * cache
     */
    class Entry {
        public byte[] data;
        public String etag;

        public long serverDate;
        public long ttl; //System.currentTimeMillis()

        public Map<String, String> responseHeaders = Collections.emptyMap();

        /**
         *
         */
        public boolean isExpired() {
            return this.ttl < System.currentTimeMillis();
        }
    }
}

