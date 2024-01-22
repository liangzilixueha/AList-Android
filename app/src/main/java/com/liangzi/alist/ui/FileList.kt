package com.liangzi.alist.ui

import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.motionEventSpy
import androidx.compose.ui.modifier.modifierLocalConsumer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat.startActivity
import com.liangzi.alist.LoginActivity
import com.liangzi.alist.R


@Composable
fun FileList(list: List<FileItem>) {
    LazyColumn(content = {
        items(list.size) {
            Row {
                Item(list[it])
            }
        }
    })
}

@Composable
fun Item(item: FileItem) {
    val context = androidx.compose.ui.platform.LocalContext.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp)
            .padding(15.dp)
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
                .clickable {
                    //前往LoginActivity
                    val intent = Intent(context, LoginActivity::class.java)
                    context.startActivity(intent)
                }
        )
    }
}

@Preview
@Composable
fun PItem() {
    Item(FileItem("电影", "6.3GB", true))
}

@Preview
@Composable
fun PList() {
    LazyColumn(content = {
        items(20) {
            Card {
                Item(FileItem("电影", "6.3GB", true))
            }
        }
    })
}

data class FileItem(val name: String, val size: String, val isDir: Boolean) {
}
