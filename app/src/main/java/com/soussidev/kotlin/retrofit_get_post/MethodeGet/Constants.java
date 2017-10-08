package com.soussidev.kotlin.retrofit_get_post.MethodeGet;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Soussi on 04/10/2017.
 */

public class Constants {

    public static final String BASE_URL = "http://10.0.2.2:8080/";
    public static final String BASE_URL_GET_USER = "http://10.0.2.2:8080/soussidev/api/db_post_user.php";
    public static final String GET_USER_OPERATION = "getUser";
    public static final String ADD_USER_OPERATION = "add_User";

    public static final String SUCCESS = "success";
    public static final String FAILURE = "failure";

    public static final String TAG = "soussidev";


    private static Retrofit retrofit = null;



    public static Retrofit getClient() {

        OkHttpClient client = new OkHttpClient.Builder()
                //.addInterceptor(new BasicAuthInterceptor("myUserName", "myPassword"))
                .build();

        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build();
        }
        return retrofit;
    }




}
