package com.soussidev.kotlin.retrofit_get_post.MethodeGet;

import com.soussidev.kotlin.retrofit_get_post.model.User;

import java.util.List;

/**
 * Created by Soussi on 04/10/2017.
 */

public class ServerResponse {

    private String result;
    private String message;
    private List<User> user;

    public String getResult() {
        return result;
    }

    public String getMessage() {
        return message;
    }

    public List<User> getUser() {
        return user;
    }
}
