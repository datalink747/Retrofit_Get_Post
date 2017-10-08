package com.soussidev.kotlin.rxvolley.rxvolley.client;

import com.soussidev.kotlin.rxvolley.rxvolley.http.DefaultRetryPolicy;
import com.soussidev.kotlin.rxvolley.rxvolley.http.RetryPolicy;
import com.soussidev.kotlin.rxvolley.rxvolley.rx.Result;

import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.Subject;

/**
 * Created by Soussi on 08/10/2017.
 */

public class RequestConfig {

    public int mTimeout = 0;

    public int mDelayTime = 0;

    public int mCacheTime = 5;

    public boolean mUseServerControl;

    public int mMethod;

    public Boolean mShouldCache = null;

    public String mUrl;

    public RetryPolicy mRetryPolicy = new DefaultRetryPolicy();

    public String mEncoding = "UTF-8";

    public Object mTag;

    public final Subject<Result> mSubject = PublishSubject.<Result>create().toSerialized();
}
