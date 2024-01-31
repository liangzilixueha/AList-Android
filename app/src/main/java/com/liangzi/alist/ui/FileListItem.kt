package com.liangzi.alist.ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.liangzi.alist.R
import com.liangzi.alist.data.FileItem

open class FileListItem {

    open fun click() {}
    open fun longClick() {}

    @OptIn(ExperimentalFoundationApi::class)
    @Composable
    fun Item(item: FileItem, dir: String) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(15.dp)
                .combinedClickable(
                    onClick = {
                        // 点击
                        this.click()
                    },
                    onLongClick = {
                        // 长按
                        this.longClick()
                    },
                    onDoubleClick = {
                        // 双击
                    }
                )
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
            Column(
                modifier = Modifier
                    .weight(1f)
                    .align(Alignment.CenterVertically)
            ) {
                Text(
                    text = item.name,
                    modifier = Modifier
                )
                Text(
                    text = item.created,
                    modifier = Modifier,
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }


            // 文件大小
            Spacer(modifier = Modifier.width(8.dp))
            Icon(
                imageVector = Icons.Default.List,
                contentDescription = null,
                modifier = Modifier
                    .align(Alignment.CenterVertically)
                    .clickable {
                        moreInfo()
                    },
                tint = Color.Gray
            )
        }
    }

    open fun moreInfo() {

    }

    private fun bytesToElse(size: Long): String {
        return when {
            size == 0L -> "文件夹下无内容"
            size < 1024 -> String.format("%.2f", size / 1.0) + " B"
            size < 1024 * 1024 -> String.format("%.2f", size / 1024.0) + " KB"
            size < 1024 * 1024 * 1024 -> String
                .format("%.2f", size / 1024.0 / 1024) + " MB"

            else -> String.format("%.2f", size / 1024.0 / 1024 / 1024) + " GB"
        }
    }

    @Preview
    @Composable
    fun P() {
        val item = FileItem("超级大王".repeat(10), 1024 * 96 * 1024, "", true,"2024-01-11T12:36:33Z")
        Item(item = item, dir = "")
    }
}