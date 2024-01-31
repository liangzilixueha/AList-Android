package com.liangzi.alist.ui.compose

import android.annotation.SuppressLint
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

open class Download {

    private var str = ""

    @SuppressLint("NotConstructor")
    @Composable
    fun DownloadView() {
        Row {
            Text(str)
        }
    }

    open fun start() {

    }

    open fun stop() {

    }

    open fun delete() {

    }

    @Composable
    fun Item(data: ItemData) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            colors = CardDefaults.cardColors(
                containerColor = when (data.state) {
                    State.START -> Color(0xFF49B7FF)
                    State.STOP -> Color(0xFFAFAFAF)
                    State.COMPLETE -> Color(0xFF9CDD91)
                    else -> Color(0xFFE0E0E0)
                },
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp)

            ) {
                Row {
                    Text(
                        data.name,
                        modifier = Modifier
                            .weight(1f)
                            .align(Alignment.CenterVertically)
                    )
                    when (data.state) {
                        State.START -> Icon(
                            imageVector =
                            Icons.Default.PlayArrow, contentDescription = null,
                            modifier = Modifier
                                .clickable {
                                    stop()
                                }

                        )

                        State.STOP -> Icon(
                            imageVector =
                            Icons.Default.PlayArrow, contentDescription = null,
                            modifier = Modifier
                                .clickable {
                                    start()
                                }

                        )

                        State.COMPLETE -> Icon(
                            imageVector =
                            Icons.Default.Check, contentDescription = null,
                            modifier = Modifier
                                .clickable {

                                }

                        )

                        else -> {

                        }
                    }
                    Icon(
                        imageVector =
                        Icons.Default.Delete, contentDescription = null,
                        modifier = Modifier
                            .clickable {
                                delete()
                            }

                    )
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 10.dp)
                ) {
                    Text(text = bytesToElse(data.size * data.progress / 100))
                    Text("/")
                    Text(text = bytesToElse(data.size))
                    Text(
                        modifier = Modifier
                            .weight(1f),
                        text = bytesToElse(data.speed) + "/s",
                        textAlign = TextAlign.End
                    )

                }
                LinearProgressIndicator(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 10.dp)
                        .clip(CircleShape),
                    progress = data.progress / 100f
                )

            }
        }

    }

    @Preview
    @Composable
    fun P() {
        Column {
            val data = ItemData("超级大王", 1024 * 96, 66, 1024, State.START)
            Item(data)
            val data1 = ItemData("超级大王".repeat(10), 1024 * 1024 * 6, 25, 20, State.COMPLETE)
            Item(data1)
            val data2 = ItemData("超级大王".repeat(10), 108, 25, 20, State.STOP)
            Item(data2)
        }

    }

    enum class State {
        START,
        STOP,
        COMPLETE
    }

    data class ItemData(
        val name: String,
        val size: Long,
        var progress: Int,
        var speed: Long,
        var state: State,
        val id: Long = 0,
        val taskKey: String = ""
    )
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

