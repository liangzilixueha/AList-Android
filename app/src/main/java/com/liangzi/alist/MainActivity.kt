package com.liangzi.alist


import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
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
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import com.liangzi.alist.json.getListJson
import com.liangzi.alist.tool.POST
import com.liangzi.alist.ui.theme.AlistTheme
import okhttp3.OkHttpClient

class MainActivity : ComponentActivity() {
    private var host = ""
    private var 当前url = ""
    val 需要密码 = "password is incorrect or you have no permission"
    val fileItem = mutableStateOf(mutableListOf<FileItem>())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AlistTheme {}
        }
        host = getSharedPreferences("config", MODE_PRIVATE).getString("host", "")!!
        Thread {
            val json = POST(
                url = "$host/api/fs/list", json = Gson().toJson(请求json("/", "", 1, 100, false))
            )
            val list = Gson().fromJson(json, getListJson::class.java)
            runOnUiThread {
                if (list.message == 需要密码) {
                    Toast.makeText(this, "请输入密码", Toast.LENGTH_SHORT).show()
                    setContent {
                        AlistTheme {
                            Row {
                                MyApp()
                            }
                        }
                    }
                } else {
                    setContent {
                        AlistTheme {}
                    }
                }
            }
        }.start()
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

            // 弹窗
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
                                    setContent {
                                        文件列表显示(list)
                                    }

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

    @Composable
    fun 文件列表显示(list: getListJson) {
        AlistTheme {
            fileItem.component1().clear()
            list.data.content.forEach {
                fileItem.component1().add(
                    FileItem(
                        it.name, it.size.toString(), it.is_dir
                    )
                )
            }
            Row {
                FileList(fileItem.component1(), 当前url)
            }
        }
    }

    @Composable
    fun FileList(list: List<FileItem>, dir: String) {
        LazyColumn(content = {
            items(list.size) {
                Row {
                    Item(list[it], dir)
                }
            }
        })
    }

    @Composable
    fun Item(item: FileItem, dir: String) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(80.dp)
                .padding(15.dp)
                .clickable {
                    Toast
                        .makeText(this, 当前url, Toast.LENGTH_SHORT)
                        .show()
                    if (item.isDir) {
                        当前url += "/"+item.name
                        Thread {
                            val json = POST(
                                url = "$host/api/fs/list",
                                json = Gson().toJson(
                                    请求json(
                                        path = 当前url,
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
                            Log.d("json", 当前url+json!!)
                            val list = Gson().fromJson(json, getListJson::class.java)
                            runOnUiThread {
                                setContent {
                                    Log.d("json", "重绘")
                                    文件列表显示(list)
                                }
                            }
                        }.start()
                    } else {
                        Toast
                            .makeText(this, "该文件不是文件夹", Toast.LENGTH_SHORT)
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

data class 请求json(
    val path: String,
    val password: String,
    val page: Int,
    val per_page: Int,
    val refresh: Boolean
)

data class FileItem(
    val name: String,
    val size: String,
    val isDir: Boolean
)







