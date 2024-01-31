package com.liangzi.alist.tool

import android.content.Context
import androidx.activity.ComponentActivity

class UserConfig {
    private val host = ""//域名
    private val account = ""//用户的账号
    private val password = ""//用户的密码
    private val token = ""//用户的token
    private val level = UserLevel.GUEST

    fun getHost(context: Context): String {
        context.getSharedPreferences("config", ComponentActivity.MODE_PRIVATE)
            .getString("host", null)
        return host
    }

    fun getAccount(context: Context): String {
        context.getSharedPreferences("config", ComponentActivity.MODE_PRIVATE)
            .getString("account", null)
        return account
    }

    fun getPassword(): String {
        return password
    }

    fun getToken(): String {
        return token
    }

    fun getLevel(): UserLevel {
        return level
    }

}

fun main() {
    val userConfig = UserConfig()
}

enum class UserLevel {
    ADMIN,
    GENERAL,
    GUEST
}