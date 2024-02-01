package com.liangzi.alist

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import com.hjq.permissions.OnPermissionCallback
import com.hjq.permissions.Permission
import com.hjq.permissions.XXPermissions
import com.liangzi.alist.databinding.ActivityLoginBinding
import com.liangzi.alist.tool.UserConfig
import okhttp3.OkHttpClient
import okhttp3.Request


class LoginActivity : ComponentActivity() {
    private var binding: ActivityLoginBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding!!.root)
        UserConfig.init(this)
        XXPermissions.with(this)
            // 申请单个权限
            .permission(Permission.WRITE_EXTERNAL_STORAGE)
            .permission(Permission.READ_MEDIA_AUDIO)
            .permission(Permission.READ_MEDIA_IMAGES)
            .permission(Permission.READ_MEDIA_VIDEO)
            .request(object : OnPermissionCallback {

                override fun onGranted(permissions: MutableList<String>, allGranted: Boolean) {
                    if (!allGranted) {
                        Toast.makeText(baseContext, "获取存储权限失败", Toast.LENGTH_SHORT).show()
                        return
                    }
                    Toast.makeText(baseContext, "获取存储权限成功", Toast.LENGTH_SHORT).show()
                }

                override fun onDenied(permissions: MutableList<String>, doNotAskAgain: Boolean) {
                    if (doNotAskAgain) {
                        Toast.makeText(
                            baseContext,
                            "获取存储权限失败，请手动授予权限",
                            Toast.LENGTH_SHORT
                        ).show()
                        // 如果是被永久拒绝就跳转到应用权限系统设置页面
                        XXPermissions.startPermissionActivity(baseContext, permissions)
                    } else {
                        Toast.makeText(baseContext, "获取存储权限成功", Toast.LENGTH_SHORT).show()
                    }
                }
            })
        val url = UserConfig.host
        if (url != "") {
            Toast.makeText(this, "已登录", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
        binding!!.btn.setOnClickListener {
            val host = binding!!.host.text.toString()
            if (host.isEmpty()) {
                Toast.makeText(this, "域名不能为空", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (!host.startsWith("http")) {
                Toast.makeText(this, "域名必须以http(s)://开头", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (host.endsWith("/")) {
                Toast.makeText(this, "域名不能以/结尾", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val request: Request?
            try {
                request = Request.Builder()
                    .url("$host/ping")
                    .get()
                    .build()
                Thread {
                    val json = OkHttpClient().newCall(request).execute().body?.string()
                    runOnUiThread {
                        if (json == "pong") {
                            Toast.makeText(this, json, Toast.LENGTH_SHORT).show()
                            getSharedPreferences("config", MODE_PRIVATE).edit()
                                .putString("host", host).apply()
                            startActivity(Intent(this, MainActivity::class.java))
                            finish()
                        } else {
                            Toast.makeText(this, "该域名未使用原生AList", Toast.LENGTH_SHORT).show()
                        }
                    }
                }.start()
            } catch (e: Exception) {
                Toast.makeText(this, "域名不合法", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

        }

    }
}