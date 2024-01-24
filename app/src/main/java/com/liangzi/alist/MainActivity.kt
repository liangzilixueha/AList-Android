package com.liangzi.alist


import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.google.gson.Gson
import com.liangzi.alist.data.FileItem
import com.liangzi.alist.data.请求json
import com.liangzi.alist.json.getListJson
import com.liangzi.alist.tool.POST
import com.liangzi.alist.ui.theme.AlistTheme

class MainActivity : ComponentActivity() {
    private var host = ""//域名
    private var 当前url = mutableStateOf("")//用于json请求中的path
    val 需要密码 = "password is incorrect or you have no permission"//密码错误时返回的判断
    val fileItem = mutableStateListOf<FileItem>()//文件列表
    val needPassword = mutableStateOf(false)//是否需要密码的视图切换

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
        Row {
            Button(onClick = { /*TODO*/ }, modifier = Modifier.weight(1f)) {
                Text(text = "首页")
            }
            Button(onClick = { /*TODO*/ }, modifier = Modifier.weight(1f)) {
                Text(text = "传输")
            }
            Button(onClick = { /*TODO*/ }, modifier = Modifier.weight(1f)) {
                Text(text = "更多设置")
            }
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
                Button(onClick = {
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
                }) {
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
        var password by remember { mutableStateOf("") }
        val context = LocalContext.current
        val host = context.getSharedPreferences("config", MODE_PRIVATE).getString("host", "")!!
        Dialog(
            onDismissRequest = { onDismiss() },
            properties = DialogProperties(dismissOnClickOutside = false)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                TextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("密码") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = {
                        Thread {
                            val json = POST(
                                url = "$host/api/fs/list",
                                json = Gson().toJson(
                                    请求json(
                                        "/",
                                        password,
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
                                        .putString("password", password).apply()
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
                    }, modifier = Modifier.align(Alignment.CenterHorizontally)
                ) {
                    Text("确认")
                }
            }
        }
    }


    /**
     * 文件列表的每一项
     */
    @Composable
    fun Item(item: FileItem, dir: String) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(80.dp)
                .padding(15.dp)
                .clickable {
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
                            Log.d("点击项目", item.name+json!!)
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
                    } else if(item.name.endsWith(".mp4")||item.name.endsWith(".mkv")){
                        startActivity(
                            Intent(this, PlayerActivity::class.java).apply {
                                putExtra("url", 当前url.value + "/" + item.name)
                            }
                        )
                    }else{
                        Toast
                            .makeText(this, "暂不支持预览", Toast.LENGTH_SHORT)
                            .show()
                    }
                }
        ) {
            // 左侧图片
            Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = null,
                modifier = Modifier
                    .size(35.dp)
                    .clip(MaterialTheme.shapes.small)
                    .align(Alignment.CenterVertically)
            )

            // 文件名
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = item.name,
                modifier = Modifier
                    .weight(1f)
                    .align(Alignment.CenterVertically)
            )

            // 文件大小
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = item.size,
                modifier = Modifier
                    .align(Alignment.CenterVertically)
            )
        }
    }
}









