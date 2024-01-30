package com.liangzi.alist.data

data class 请求json(
    val path: String,
    val password: String,
    val page: Int = 1,
    val per_page: Int = 100,
    val refresh: Boolean = false
)

class FileItem(val name: String, val size: Long, val isDir: Boolean)


data class API(
    val host: String = "",
    val 获取文件详情: String = "/api/fs/get"
)