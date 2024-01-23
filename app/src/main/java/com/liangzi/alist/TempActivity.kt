package com.liangzi.alist

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.liangzi.alist.ui.theme.AlistTheme

class TempActivity : ComponentActivity() {


    val list = mutableStateListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AlistTheme {
                Column {
                    Btn()
                    LazyColumn {
                        items(list.size) {
                            Text(list[it])
                        }
                    }
                }
            }
        }
    }

    @Composable
    private fun Btn() {
        Button(onClick = {
            list.add("4")
        }) {
            Text("加一个")
        }
    }
}
