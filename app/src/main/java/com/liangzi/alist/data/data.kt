package com.liangzi.alist.data

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