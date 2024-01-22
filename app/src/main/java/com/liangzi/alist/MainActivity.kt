package com.liangzi.alist


import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.google.gson.Gson
import com.liangzi.alist.json.getListJson
import com.liangzi.alist.ui.FileItem
import com.liangzi.alist.ui.FileList
import com.liangzi.alist.ui.theme.AlistTheme
import okhttp3.OkHttpClient
import kotlin.math.log

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AlistTheme {

            }
        }
    }
}



