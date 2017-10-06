package com.soussidev.kotlin.retrofit_get_post.MethodeGet;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Soussi on 04/10/2017.
 */

public class Constants {

    public static final String BASE_URL = "http://10.0.2.2:8080/";
    public static final String GET_USER_OPERATION = "getUser";
    public static final String ADD_USER_OPERATION = "add_User";

    public static final String SUCCESS = "success";
    public static final String FAILURE = "failure";

    public static final String TAG = "soussidev";


    private static Retrofit retrofit = null;

    public static Retrofit getClient() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }

}
