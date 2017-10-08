package com.soussidev.kotlin.rxvolley.rxvolley.interf;

import com.soussidev.kotlin.rxvolley.rxvolley.http.Request;
import com.soussidev.kotlin.rxvolley.rxvolley.http.URLHttpResponse;
import com.soussidev.kotlin.rxvolley.rxvolley.toolbox.HttpParamsEntry;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Soussi on 08/10/2017.
 */

public interface IHttpStack {


    URLHttpResponse performRequest(Request<?> request, ArrayList<HttpParamsEntry> additionalHeaders)
            throws IOException;

}
