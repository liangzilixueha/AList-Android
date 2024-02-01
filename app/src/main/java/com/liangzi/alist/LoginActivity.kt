package com.liangzi.alist

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.ComponentActivity
import com.hjq.permissions.OnPermissionCallback
import com.hjq.permissions.Permission
import com.hjq.permissions.XXPermissions
import com.liangzi.alist.databinding.ActivityLoginBinding
import com.liangzi.alist.json.LoginJson
import com.liangzi.alist.tool.POST
import com.liangzi.alist.tool.UserConfig
import com.liangzi.alist.tool.inThread
import okhttp3.OkHttpClient
import okhttp3.Request


class LoginActivity : ComponentActivity() {
    private lateinit var binding: ActivityLoginBinding

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
        val data = listOf("https://", "http://")
        val adapte = ArrayAdapter(this, android.R.layout.simple_spinner_item, data)

        // 设置下拉列表的样式
        adapte.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinner.apply {
            adapter = adapte
            setSelection(0, false)
            // 设置选择监听器
            onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parentView: AdapterView<*>?,
                    selectedItemView: View?,
                    position: Int,
                    id: Long
                ) {
                    binding.spinner.setSelection(position)
                }

                override fun onNothingSelected(parentView: AdapterView<*>?) {
                    // 处理未选中任何项的操作
                }
            }

        }
        binding.apply {
            account.visibility = View.GONE
            pwd.visibility = View.GONE
            radioGroup.check(R.id.guest)
        }
        binding.radioGroup.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.admin -> {
                    binding.apply {
                        account.visibility = View.VISIBLE
                        pwd.visibility = View.VISIBLE
                    }
                }

                R.id.user -> {
                    binding.apply {
                        account.visibility = View.VISIBLE
                        pwd.visibility = View.VISIBLE
                    }

                }

                R.id.guest -> {
                    binding.apply {
                        account.visibility = View.GONE
                        pwd.visibility = View.GONE
                    }
                }
            }
        }
        binding.btn.setOnClickListener {
            val host = binding.spinner.selectedItem.toString() + binding.host.text.toString()
            if (host.isEmpty()) {
                Toast.makeText(this, "域名不能为空", Toast.LENGTH_SHORT).show()
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
                inThread {
                    val json = OkHttpClient().newCall(request).execute().body?.string()
                    runOnUiThread {
                        if (json == "pong") {
                            if (binding.radioGroup.checkedRadioButtonId == R.id.guest) {
                                Toast.makeText(this, json, Toast.LENGTH_SHORT).show()
                                getSharedPreferences("config", MODE_PRIVATE).edit()
                                    .putString("host", host).apply()
                                UserConfig.init(this)
                                startActivity(Intent(this, MainActivity::class.java))
                                finish()
                            } else {
                                inThread {
                                    val json2 = POST(
                                        "$host/api/auth/login",
                                        LoginData(
                                            binding.account.text.toString(),
                                            binding.pwd.text.toString()
                                        )
                                    )
                                    val data2 = LoginJson.fromJson(json2).data
                                    if (data2 == null) {
                                        runOnUiThread {
                                            Toast.makeText(
                                                this,
                                                "登录失败",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                    } else {
                                        getSharedPreferences("config", MODE_PRIVATE).edit()
                                            .putString("host", host)
                                            .putString("account", binding.account.text.toString())
                                            .putString("password", binding.pwd.text.toString())
                                            .putString("token", data2.token)
                                            .apply()
                                        UserConfig.init(this)
                                        startActivity(Intent(this, MainActivity::class.java))
                                        finish()
                                    }
                                }
                            }
                        } else {
                            Toast.makeText(this, "该域名未使用原生AList", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            } catch (e: Exception) {
                Toast.makeText(this, "域名不合法", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

        }

    }

    data class LoginData(
        val username: String,
        val password: String
    )
}