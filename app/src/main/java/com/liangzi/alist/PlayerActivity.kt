package com.liangzi.alist

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import cn.jzvd.Jzvd
import com.liangzi.alist.data.API
import com.liangzi.alist.data.请求json
import com.liangzi.alist.databinding.ActivityPlayerBinding
import com.liangzi.alist.json.getFileJson
import com.liangzi.alist.tool.POST
import com.liangzi.alist.tool.UserConfig
import com.liangzi.alist.tool.inThread


class PlayerActivity : AppCompatActivity(){
    private lateinit var binding: ActivityPlayerBinding
    private var host = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.layout_std_speed)
        binding = ActivityPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        host = getSharedPreferences("config", MODE_PRIVATE).getString("host", "")!!
        val url = intent.getStringExtra("url")
        inThread {
            val json = POST(
                "$host${API().获取文件详情}",
                请求json(
                    url!!,
                    UserConfig().getLock(this)!!
                )
            )
            Log.d("json", json!!)
            val raw_url =
                getFileJson.fromJson(json).data.raw_url
            runOnUiThread {
                binding.apply {
//                    player.setUrl(raw_url) //设置视频地址
//                    val controller = StandardVideoController(this@PlayerActivity)
//                    controller.addDefaultControlComponent("标题", false)
//                    player.setVideoController(controller) //设置控制器
//                    player.start() //开始播放，不调用则不自动播放
                    player.setUp(raw_url, "标题")
                }
            }
        }
    }

    override fun onBackPressed() {
        if (Jzvd.backPress()) {
            return
        }
        super.onBackPressed()
    }

    override fun onPause() {
        super.onPause()
        Jzvd.releaseAllVideos()
    }
}