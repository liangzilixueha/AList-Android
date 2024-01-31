package com.liangzi.alist.ui.compose

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.liangzi.alist.R

open class BottomSheet {


    open fun download() {

    }

    @SuppressLint("NotConstructor")
    @Composable
    fun BottomSheet(data: SheetData) {
        val icon_szie = 25.dp
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Row {
                AsyncImage(
                    model = data.img_url, contentDescription = null,
                    modifier = Modifier
                        .padding(end = 10.dp)
                        .size(icon_szie)
                        .align(Alignment.CenterVertically)
                )
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .align(Alignment.CenterVertically)
                ) {
                    Text(text = data.name)
                    Row {
                        Text(
                            text = data.time,
                            modifier = Modifier,
                            fontSize = 12.sp,
                            color = Color.Gray
                        )
                        Text(
                            text = bytesToElse(data.size),
                            modifier = Modifier
                                .weight(1f),
                            fontSize = 12.sp,
                            color = Color.Gray,
                            textAlign = TextAlign.End
                        )
                    }

                }

            }
            Iconitem(action = { download() })
            Iconitem()
            Iconitem()
            Spacer(modifier = Modifier.height(70.dp))
        }
    }

    @Composable
    fun Iconitem(id: Int = R.drawable.download, text: String = "下载", action: () -> Unit = {}) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 25.dp)
                .clickable {
                    action()
                }
        ) {
            Image(
                painter = painterResource(id = id),
                contentDescription = null,
                modifier = Modifier
                    .align(Alignment.CenterVertically)
                    .padding(end = 10.dp)
                    .size(25.dp)
            )
            Text(
                text = text,
                modifier = Modifier
                    .fillMaxWidth(1f)
                    .align(Alignment.CenterVertically),
                textAlign = TextAlign.Start,
                fontSize = 18.sp
            )
        }


    }

    @Preview
    @Composable
    fun P() {
        val data = SheetData("666".repeat(20), "666", "01-30 19:03")
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Row {
                AsyncImage(
                    model = data.img_url, contentDescription = null,
                    modifier = Modifier
                        .padding(end = 10.dp)
                        .size(25.dp)
                        .align(Alignment.CenterVertically)
                )
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .align(Alignment.CenterVertically)
                ) {
                    Text(text = data.name)
                    Row {
                        Text(
                            text = data.time,
                            modifier = Modifier,
                            fontSize = 12.sp,
                            color = Color.Gray
                        )
                        Text(
                            text = data.size.toString(),
                            modifier = Modifier
                                .weight(1f),
                            fontSize = 12.sp,
                            color = Color.Gray,
                            textAlign = TextAlign.End
                        )
                    }

                }

            }
            Iconitem()
        }
    }

    @Preview
    @Composable
    fun PP() {
        Iconitem()
    }

    private fun bytesToElse(bytes: Long): String {
        return when {
            bytes < 1024 -> {
                "${bytes}B"
            }

            bytes < 1024 * 1024 -> {
                String.format("%.2f", bytes / 1024.0) + " KB"
            }

            bytes < 1024 * 1024 * 1024 -> {
                String.format("%.2f", bytes / 1024.0 / 1024) + " MB"
            }

            else -> {
                String.format("%.2f", bytes / 1024.0 / 1024 / 1024) + " GB"
            }
        }
    }
}


data class SheetData(
    val name: String = "",
    val img_url: String = "",
    val time: String = "",
    val size: Long = 10086L
)



