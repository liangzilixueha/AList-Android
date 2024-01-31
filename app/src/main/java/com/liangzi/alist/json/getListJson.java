package com.liangzi.alist.json;


import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class getListJson {

    public Integer code;
    public String message;
    public Data data;

    public static class Data {
        public List<Content> content;
        public Integer total;
        public String readme;
        public String header;
        public Boolean write;
        public String provider;

        public static class Content {
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
        }
    }

    public static getListJson fromJson(String json) {
        Gson gson = new Gson();
        return gson.fromJson(json, getListJson.class);
    }
}
