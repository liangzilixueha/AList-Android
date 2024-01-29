package com.liangzi.alist.ui.compose

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

class Download {

    private var str=""

    @SuppressLint("NotConstructor")
    @Composable
    fun DownloadView() {
        Row {
            Text(str)
        }
    }



    @com.arialyy.annotations.Download.onTaskRunning
    fun running(task: com.arialyy.aria.core.task.DownloadTask) {
        Log.d("下载进度", task.percent.toString())
    }

    @com.arialyy.annotations.Download.onTaskComplete
    fun complete(task: com.arialyy.aria.core.task.DownloadTask) {
        Log.d("下载完成", task.percent.toString())
    }

}
