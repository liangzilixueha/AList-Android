package com.liangzi.alist.json;

import com.google.gson.Gson;

public class getFileJson {

    public Integer code;
    public String message;
    public Data data;

    public static class Data {
        public String name;
        public Long size;
        public Boolean is_dir;
        public String modified;
        public String created;
        public String sign;
        public String thumb;
        public Integer type;
        public String hashinfo;
        public Object hash_info;
        public String raw_url;
        public String readme;
        public String header;
        public String provider;
        public Object related;
    }

    public static getFileJson fromJson(String json) {
        Gson gson = new Gson();
        return gson.fromJson(json, getFileJson.class);
    }
}
