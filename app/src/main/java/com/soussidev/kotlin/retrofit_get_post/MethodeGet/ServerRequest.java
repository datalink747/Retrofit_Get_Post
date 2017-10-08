package com.soussidev.kotlin.retrofit_get_post.MethodeGet;

import com.google.gson.annotations.SerializedName;
import com.soussidev.kotlin.retrofit_get_post.model.User;

/**
 * Created by Soussi on 04/10/2017.
 */

public class ServerRequest {

    private String operation;
    @SerializedName(value="user")
    private User user;


    public void setOperation(String operation) {
        this.operation = operation;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
