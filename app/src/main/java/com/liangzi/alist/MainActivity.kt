package com.liangzi.alist


import android.content.Intent
import android.os.Bundle
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
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.gson.Gson
import com.liangzi.alist.data.FileItem
import com.liangzi.alist.data.请求json
import com.liangzi.alist.json.getListJson
import com.liangzi.alist.tool.POST
import com.liangzi.alist.ui.FileListItem
import com.liangzi.alist.ui.PopDialog
import com.liangzi.alist.ui.theme.AlistTheme

class MainActivity : ComponentActivity() {
    private var host = ""//域名
    private var 当前url = mutableStateOf("")//用于json请求中的path
    val 需要密码 = "password is incorrect or you have no permission"//密码错误时返回的判断
    val fileItem = mutableStateListOf<FileItem>()//文件列表
    val needPassword = mutableStateOf(false)//是否需要密码的视图切换
    private val whichButton = mutableIntStateOf(0) //底部导航栏的按钮

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        host = getSharedPreferences("config", MODE_PRIVATE).getString("host", "")!!
        Thread {
            val json = POST(
                url = "$host/api/fs/list",
                json = Gson().toJson(
                    请求json(
                        path = "/",
                        getSharedPreferences("config", MODE_PRIVATE).getString("password", "")!!,
                        1,
                        100,
                        false
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
                if (needPassword.value) {
                    MyApp()
                } else {
                    Column {
                        PathBar(当前url.value)
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .weight(1f)
                        ) {
                            ShowFiles()
                        }
                        BottomBar()
                    }
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
            containerColor = Color.White,
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
                        modifier = Modifier
                            .clip(CircleShape)
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
                        modifier = Modifier
                            .clip(CircleShape)
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
                        Icons.Default.Settings,
                        contentDescription = "Account",
                        modifier = Modifier
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
            modifier = Modifier
                .horizontalScroll(move)
        ) {
            path.split("/").forEach {
                Button(
                    onClick = {
                        当前url.value = path.substring(0, path.indexOf(it) + it.length)
                        Thread {
                            val json = POST(
                                url = "$host/api/fs/list",
                                json = Gson().toJson(
                                    请求json(
                                        path = 当前url.value,
                                        getSharedPreferences("config", MODE_PRIVATE).getString(
                                            "password",
                                            ""
                                        )!!,
                                        1,
                                        100,
                                        false
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

    @Preview
    @Composable
    fun P() {
        val s = "/微软/你好/212/ppp"
        PathBar(s)
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
                        url = "$host/api/fs/list",
                        json = Gson().toJson(
                            请求json(
                                "/",
                                this.password,
                                1,
                                100,
                                false
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
        object : FileListItem() {
            override fun click() {
                if (item.isDir) {
                    当前url.value += "/" + item.name
                    Thread {
                        val json = POST(
                            url = "$host/api/fs/list",
                            json = Gson().toJson(
                                请求json(
                                    path = 当前url.value,
                                    getSharedPreferences("config", MODE_PRIVATE).getString(
                                        "password",
                                        ""
                                    )!!,
                                    1,
                                    100,
                                    false
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
                    startActivity(
                        Intent(context, PlayerActivity::class.java).apply {
                            putExtra("url", 当前url.value + "/" + item.name)
                        }
                    )
                } else {
                    Toast
                        .makeText(context, "暂不支持预览", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }.Item(item, dir)
    }
}









