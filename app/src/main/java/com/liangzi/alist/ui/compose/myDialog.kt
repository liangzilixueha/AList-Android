package com.liangzi.alist.ui.compose

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.tooling.preview.Preview

class MyDialog {

    fun confirmButtonOnClick(){

    }

    @Composable
    fun Dialog(
        title: String = "标题",
        content: String = "内容",
        confirm: String = "确定",
        dismiss: String = "取消"
    ) {
        val openDialog = remember { mutableStateOf(true) }
        if (openDialog.value) {
            AlertDialog(
                onDismissRequest = {
                    openDialog.value = false
                },
                title = {
                    Text(text = title)
                },
                text = {
                    Text(text = content)
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            openDialog.value = false
                            this.confirmButtonOnClick()
                        }
                    ) {
                        Text(confirm)
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = {
                            openDialog.value = false
                        }
                    ) {
                        Text(dismiss)
                    }
                }
            )
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun ButtonsDialog(){
    }

    @Preview
    @Composable
    fun P() {
        ButtonsDialog()
    }

    companion object
}

