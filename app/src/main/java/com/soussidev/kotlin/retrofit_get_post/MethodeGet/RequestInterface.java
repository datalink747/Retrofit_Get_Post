package com.soussidev.kotlin.retrofit_get_post.MethodeGet;

import com.soussidev.kotlin.retrofit_get_post.model.User;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;

/**
 * Created by Soussi on 04/10/2017.
 */

public interface RequestInterface {

    @POST("soussidev/")
    Call<ServerResponse> operation(@Body ServerRequest request);

    @POST("soussidev/api/db_post_user.php/")
    Call<ServerResponse> addUser(@Body ServerRequest request);

    /*@POST("soussidev/")
    @FormUrlEncoded
    Call<User> saveUser(@Field("NomUser") String NomUser,
                        @Field("PrenomUser") String PrenomUser,
                        @Field("CinUser") int CinUser);*/
}
