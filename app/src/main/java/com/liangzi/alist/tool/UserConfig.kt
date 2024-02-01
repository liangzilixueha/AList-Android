package com.liangzi.alist.tool

import android.content.Context
import android.content.Context.MODE_PRIVATE

object UserConfig {
    var host = ""//域名
    var account = ""//用户的账号
    var password = ""//用户的密码
    var token = ""//用户的token
    val level = UserLevel.GUEST
    var lock: String = ""

    fun init(context: Context) {
        host = context.getSharedPreferences("config", MODE_PRIVATE).getString("host", "")!!
        account = context.getSharedPreferences("config", MODE_PRIVATE).getString("account", "")!!
        password = context.getSharedPreferences("config", MODE_PRIVATE).getString("password", "")!!
        token = context.getSharedPreferences("config", MODE_PRIVATE).getString("token", "")!!
        lock = context.getSharedPreferences("config", MODE_PRIVATE).getString("lock", "")!!

    }
}

enum class UserLevel {
    ADMIN,
    GENERAL,
    GUEST
}

