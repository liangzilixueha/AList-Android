package com.liangzi.alist.tool

import okhttp3.OkHttpClient

fun POST(url:String,json:String):String?{
    val request = okhttp3.Request.Builder()
        .url(url)
        .post(
            okhttp3.RequestBody.create(
                okhttp3.MediaType.parse("application/json; charset=utf-8"),
                json
            )
        )
        .build()
    return OkHttpClient().newCall(request).execute().body()?.string()

}