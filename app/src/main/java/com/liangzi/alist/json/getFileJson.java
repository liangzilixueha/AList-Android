package com.liangzi.alist.json;

public class getFileJson {

    public Integer code;
    public String message;
    public Data data;

    public static class Data {
        public String name;
        public Integer size;
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
}
