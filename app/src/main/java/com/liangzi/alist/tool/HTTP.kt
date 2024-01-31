package com.liangzi.alist.tool

import com.google.gson.Gson
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient

fun <T> POST(url: String, data: T): String? {
    if(data is String){
        throw Exception("使用错误，已在请求内json化，无需传入json数据")
    }
    val request = okhttp3.Request.Builder()
        .url(url)
        .post(
            okhttp3.RequestBody.create(
                "application/json; charset=utf-8".toMediaTypeOrNull(),
                Gson().toJson(data)
            )
        )
        .build()
    return OkHttpClient().newCall(request).execute().body?.string()

}