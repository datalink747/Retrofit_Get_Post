package com.soussidev.kotlin.rxvolley.rxvolley.interf;

import com.soussidev.kotlin.rxvolley.rxvolley.http.NetworkResponse;
import com.soussidev.kotlin.rxvolley.rxvolley.http.Request;
import com.soussidev.kotlin.rxvolley.rxvolley.http.VolleyError;

/**
 * Created by Soussi on 08/10/2017.
 */

public interface INetwork {

    NetworkResponse performRequest(Request<?> request) throws VolleyError;

}
