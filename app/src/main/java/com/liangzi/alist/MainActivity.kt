package com.liangzi.alist


import android.content.Intent
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.RecomposeScope
import androidx.compose.runtime.currentRecomposeScope
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.arialyy.aria.core.Aria
import com.arialyy.aria.core.task.DownloadTask
import com.google.gson.Gson
import com.liangzi.alist.data.API
import com.liangzi.alist.data.FileItem
import com.liangzi.alist.data.请求json
import com.liangzi.alist.json.getListJson
import com.liangzi.alist.tool.POST
import com.liangzi.alist.ui.FileListItem
import com.liangzi.alist.ui.PopDialog
import com.liangzi.alist.ui.compose.Download
import com.liangzi.alist.ui.theme.AlistTheme
import java.io.File


class MainActivity : ComponentActivity(), com.arialyy.aria.core.download.DownloadTaskListener {
    private lateinit var compose: RecomposeScope
    private var host = ""//域名
    private var 当前url = mutableStateOf("")//用于json请求中的path
    val 需要密码 = "password is incorrect or you have no permission"//密码错误时返回的判断
    val fileItem = mutableStateListOf<FileItem>()//文件列表
    val needPassword = mutableStateOf(false)//是否需要密码的视图切换
    private val whichButton = mutableIntStateOf(1) //底部导航栏的按钮
    private val download = mutableStateListOf<Download.ItemData>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        host = getSharedPreferences("config", MODE_PRIVATE).getString("host", "")!!
        Aria.download(this).register()
        Aria.get(this).downloadConfig.setConvertSpeed(false)
        //初始化下载列表
        Aria.download(this).taskList?.forEach {
            download.add(
                Download.ItemData(
                    it.fileName,
                    it.fileSize,
                    it.percent,
                    it.speed,
                    when (it.isComplete) {
                        true -> Download.State.COMPLETE
                        false -> Download.State.STOP
                    },
                    it.id,
                    it.key
                )
            )
        }
        //初始文件列表
        Thread {
            val json = POST(
                url = "$host/api/fs/list", json = Gson().toJson(
                    请求json(
                        path = "/",
                        getSharedPreferences(
                            "config",
                            MODE_PRIVATE
                        ).getString("password", "")!!
                    )
                )
            )
            val list = Gson().fromJson(json, getListJson::class.java)
            if (list.message == 需要密码) {
                needPassword.value = true
            } else {
                fileItem.clear()
                list.data.content.forEach {
                    fileItem.add(
                        FileItem(
                            it.name, it.size, it.is_dir
                        )
                    )
                }
            }
        }.start()
        setContent {
            AlistTheme {
                Column(
                    modifier = Modifier.fillMaxSize()
                ) {
                    when (whichButton.intValue) {
                        //首页
                        0 -> {
                            if (needPassword.value) {
                                MyApp()
                            } else {
                                PathBar(当前url.value)
                                Column(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .weight(1f)
                                ) {
                                    ShowFiles()
                                }
                            }
                        }
                        //下载界面
                        1 -> {
                            compose = currentRecomposeScope
                            LazyColumn(content = {
                                items(download.size) {
                                    object : Download() {
                                        override fun start() {
                                            super.start()
                                            Aria.download(this@MainActivity)
                                                .load(download[it].id)
                                                .ignoreCheckPermissions()
                                                .resume()
                                            download[it].state = State.START
                                        }

                                        override fun stop() {
                                            super.stop()
                                            Aria.download(this@MainActivity)
                                                .load(download[it].id)
                                                .ignoreCheckPermissions()
                                                .stop()
                                            download[it].state = State.STOP
                                        }

                                        override fun delete() {
                                            super.delete()
                                            Aria.download(this@MainActivity)
                                                .load(download[it].id)
                                                .ignoreCheckPermissions()
                                                .cancel(true)
                                            download.removeAt(it)
                                        }
                                    }.Item(download[it])
                                }
                            }, modifier = Modifier.weight(1f))
                        }
                        //设置界面
                        2 -> {
                            Text(
                                "设置", modifier = Modifier.weight(1f)
                            )
                        }

                    }
                    BottomBar()
                }


            }
        }
    }

    @Composable
    fun BottomBar() {
        NavigationBar(
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            containerColor = Color.White
        ) {
            NavigationBarItem(
                selected = whichButton.intValue == 0,
                onClick = {
                    whichButton.intValue = 0
                },
                icon = {
                    Icon(
                        Icons.Default.Home,
                        contentDescription = "Account",
                        modifier = Modifier.clip(CircleShape)
                    )
                },
            )
            NavigationBarItem(
                selected = whichButton.intValue == 1,
                onClick = {
                    whichButton.intValue = 1
                },
                icon = {
                    Icon(
                        Icons.Default.Notifications,
                        contentDescription = "Account",
                        modifier = Modifier.clip(CircleShape)
                    )
                },
            )
            NavigationBarItem(
                selected = whichButton.intValue == 2,
                onClick = {
                    whichButton.intValue = 2
                },
                icon = {
                    Icon(
                        Icons.Default.Settings, contentDescription = "Account", modifier = Modifier
                    )
                },
            )
        }
    }


    /**
     * 显示路径
     */
    @Composable
    fun PathBar(path: String) {
        val move = rememberScrollState()
        Row(
            modifier = Modifier.horizontalScroll(move)
        ) {
            path.split("/").forEach {
                Button(
                    onClick = {
                        当前url.value = path.substring(0, path.indexOf(it) + it.length)
                        Thread {
                            val json = POST(
                                url = "$host/api/fs/list", json = Gson().toJson(
                                    请求json(
                                        path = 当前url.value,
                                        getSharedPreferences("config", MODE_PRIVATE).getString(
                                            "password", ""
                                        )!!
                                    )
                                )
                            )
                            val list = Gson().fromJson(json, getListJson::class.java)
                            fileItem.clear()
                            list.data.content.forEach {
                                fileItem.add(
                                    FileItem(
                                        it.name, it.size, it.is_dir
                                    )
                                )
                            }

                        }.start()
                    },
                ) {
                    if (it.isEmpty()) {
                        Text("根目录")
                    } else {
                        Text("/$it")
                    }
                }
                Spacer(modifier = Modifier.width(8.dp))
            }
        }
    }


    /**
     * 显示文件列表
     */
    @Composable
    fun ShowFiles() {
        LazyColumn(content = {
            items(fileItem.size) {
                Row {
                    Item(fileItem[it], 当前url.value)
                }
            }
        })
    }

    @Composable
    fun MyApp() {
        var showDialog by remember { mutableStateOf(false) }
        Column(
            modifier = Modifier
                .fillMaxSize()
                .height(200.dp)
                .padding(16.dp)
        ) {
            Button(
                onClick = { showDialog = true },
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                Text("访问需要密码")
            }
            if (showDialog) {
                MyDialog(onDismiss = { showDialog = false })
            }
        }
    }

    @Composable
    fun MyDialog(onDismiss: () -> Unit) {
        val context = LocalContext.current
        val host = context.getSharedPreferences("config", MODE_PRIVATE).getString("host", "")!!
        object : PopDialog() {
            override fun click() {
                Thread {
                    val json = POST(
                        url = "$host/api/fs/list", json = Gson().toJson(
                            请求json(
                                "/", this.password
                            )
                        )
                    )
                    val list = Gson().fromJson(json, getListJson::class.java)
                    runOnUiThread {
                        if (list.message == 需要密码) {
                            Toast.makeText(context, "密码错误", Toast.LENGTH_SHORT).show()
                            return@runOnUiThread
                        } else {
                            Toast.makeText(context, "密码正确", Toast.LENGTH_SHORT).show()
                            getSharedPreferences("config", MODE_PRIVATE).edit()
                                .putString("password", this.password).apply()
                            fileItem.clear()
                            list.data.content.forEach {
                                fileItem.add(
                                    FileItem(
                                        it.name, it.size, it.is_dir
                                    )
                                )
                            }
                            needPassword.value = false
                        }
                    }
                }.start()
                onDismiss()
            }
        }.PassWordDialog(onDismiss = onDismiss)
    }


    /**
     * 文件列表的每一项
     */
    @Composable
    fun Item(item: FileItem, dir: String) {
        val context = LocalContext.current
        //弹出菜单
        var expanded by remember { mutableStateOf(false) }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.padding(8.dp)
        ) {
            // 选项列表
            DropdownMenuItem(text = {
                Text("下载")
            }, onClick = {
                expanded = false
                val url = 当前url.value + "/" + item.name
                Thread {
                    val json = POST(
                        "$host${API().获取文件详情}", Gson().toJson(
                            请求json(
                                url, getSharedPreferences(
                                    "config", MODE_PRIVATE
                                ).getString("password", "")!!
                            )
                        )
                    )
                    val raw_url = Gson().fromJson(
                        json, com.liangzi.alist.json.getFileJson::class.java
                    ).data.raw_url
                    //path，本地文件保存路径
                    val dir_ = File(
                        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
                        "Alist"
                    )
                    if (!dir_.exists()) {
                        dir_.mkdirs()
                    }
                    //下载
                    val taskId: Long = Aria.download(this).load(raw_url) //读取下载地址
                        .ignoreCheckPermissions()
                        .setFilePath(dir_.path + "/${item.name}") //设置文件保存的完整路径
                        .create() //创建并启动下载
                    Log.d("下载id", taskId.toString())
                    download.add(
                        Download.ItemData(
                            item.name,
                            item.size,
                            0,
                            0,
                            Download.State.START,
                            taskId,
                            raw_url
                        )
                    )

                }.start()
            }, modifier = Modifier.align(Alignment.CenterHorizontally)
            )
            DropdownMenuItem(text = {
                Text("开发中")
            }, onClick = {
                expanded = false
            }, modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        }
        object : FileListItem() {
            override fun click() {
                if (item.isDir) {
                    当前url.value += "/" + item.name
                    Thread {
                        val json = POST(
                            url = "$host/api/fs/list", json = Gson().toJson(
                                请求json(
                                    path = 当前url.value,
                                    getSharedPreferences("config", MODE_PRIVATE).getString(
                                        "password", ""
                                    )!!
                                )
                            )
                        )
                        val list = Gson().fromJson(json, getListJson::class.java)
                        Log.d("点击项目", item.name + json!!)
                        runOnUiThread {
                            fileItem.clear()
                            list.data.content.forEach {
                                fileItem.add(
                                    FileItem(
                                        it.name, it.size, it.is_dir
                                    )
                                )
                            }
                        }
                    }.start()
                } else if (item.name.endsWith(".mp4") || item.name.endsWith(".mkv")) {

                    startActivity(Intent(context, PlayerActivity::class.java).apply {
                        putExtra("url", 当前url.value + "/" + item.name)
                    })
                } else {
                    Toast.makeText(context, "暂不支持预览", Toast.LENGTH_SHORT).show()
                }
            }

            override fun longClick() {
                when (item.isDir) {
                    true -> {
                        Toast.makeText(context, "不是文件", Toast.LENGTH_SHORT).show()

                    }

                    else -> {
                        Toast.makeText(context, "是文件", Toast.LENGTH_SHORT).show()
                        expanded = true

                    }
                }

            }
        }.Item(item, dir)
    }

    override fun onWait(task: DownloadTask?) {
    }

    override fun onPre(task: DownloadTask?) {
        Log.d("下载准备", task?.taskName.toString())
    }

    override fun onTaskPre(task: DownloadTask?) {
    }

    override fun onTaskResume(task: DownloadTask?) {
        compose.invalidate()
    }

    override fun onTaskStart(task: DownloadTask) {
    }

    override fun onTaskStop(task: DownloadTask?) {
        compose.invalidate()
    }

    override fun onTaskCancel(task: DownloadTask?) {

    }

    override fun onTaskFail(task: DownloadTask?, e: Exception?) {
    }

    override fun onTaskComplete(task: DownloadTask) {
        download.forEach {
            if (it.taskKey == task.key) {
                it.state = Download.State.COMPLETE
                it.speed = 0
                it.progress = 100
            }
        }
        compose.invalidate()
    }

    override fun onTaskRunning(task: DownloadTask) {
        Log.d("下载key", task.key)
        download.forEach {
            if (it.taskKey == task.key) {
                it.progress = task.percent
                it.speed = task.speed
            }
        }
        //界面重绘
        compose.invalidate()
    }

    override fun onNoSupportBreakPoint(task: DownloadTask?) {

    }
}