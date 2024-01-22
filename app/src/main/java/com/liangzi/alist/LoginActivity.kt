package com.liangzi.alist

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import com.liangzi.alist.databinding.ActivityLoginBinding
import okhttp3.OkHttpClient
import okhttp3.Request


class LoginActivity : ComponentActivity() {
    private var binding: ActivityLoginBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding!!.root)
        val url = getSharedPreferences("config", MODE_PRIVATE).getString("host", null)
        if (url != null) {
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
                    val json = OkHttpClient().newCall(request).execute().body()?.string()
                    runOnUiThread {
                        Toast.makeText(this, json, Toast.LENGTH_SHORT).show()
                        if (json == "pong") {
                            getSharedPreferences("config", MODE_PRIVATE).edit()
                                .putString("host", host).apply()
                            startActivity(Intent(this, MainActivity::class.java))
                            finish()
                        }else{
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