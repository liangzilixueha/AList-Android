package com.liangzi.alist.data

data class 请求json(
    val path: String,
    val password: String,
    val page: Int,
    val per_page: Int,
    val refresh: Boolean
)

class FileItem(val name: String, size: Long, val isDir: Boolean) {
    val size: String

    init {
        when {
            size == 0L -> this.size = "文件夹下无内容"
            size < 1024 -> this.size = String.format("%.2f", size / 1.0) + " B"
            size < 1024 * 1024 -> this.size = String.format("%.2f", size / 1024.0) + " KB"
            size < 1024 * 1024 * 1024 -> this.size =
                String.format("%.2f", size / 1024.0 / 1024) + " MB"

            else -> this.size = String.format("%.2f", size / 1024.0 / 1024 / 1024) + " GB"
        }
    }
}


data class API(
    val host: String = "",
    val 获取文件详情: String = "/api/fs/get"
)