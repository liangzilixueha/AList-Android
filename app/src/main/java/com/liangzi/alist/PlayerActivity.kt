package com.liangzi.alist

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.liangzi.alist.data.API
import com.liangzi.alist.databinding.ActivityPlayerBinding
import com.liangzi.alist.tool.POST
import xyz.doikki.videocontroller.StandardVideoController

class PlayerActivity : AppCompatActivity() {
    private var host = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player)
        val binding = ActivityPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        host = getSharedPreferences("config", MODE_PRIVATE).getString("host", "")!!
        val url = intent.getStringExtra("url")
        Thread {
            val json = POST(
                "$host${API().获取文件详情}",
                com.liangzi.alist.data.请求json(
                    url!!,
                    getSharedPreferences("config", MODE_PRIVATE).getString("password", "")!!
                )
            )
            Log.d("json", json!!)
            val raw_url =
                Gson().fromJson(json, com.liangzi.alist.json.getFileJson::class.java).data.raw_url
            runOnUiThread {
                binding.apply {
                    player.setUrl(raw_url) //设置视频地址
                    val controller = StandardVideoController(this@PlayerActivity)
                    controller.addDefaultControlComponent("标题", false)
                    player.setVideoController(controller) //设置控制器
                    player.start() //开始播放，不调用则不自动播放
                }
            }
        }.start()

    }
}