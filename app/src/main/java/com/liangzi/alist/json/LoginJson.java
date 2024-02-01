package com.liangzi.alist.json;

import com.google.gson.Gson;

public class LoginJson {

    public Integer code;
    public String message;
    public Data data;

    public static class Data {
        public String token;
    }

    public static LoginJson fromJson(String json) {
        Gson gson = new Gson();
        return gson.fromJson(json, LoginJson.class);
    }
}
