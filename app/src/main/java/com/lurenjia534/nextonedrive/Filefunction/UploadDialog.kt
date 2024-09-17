package com.lurenjia534.nextonedrive.Filefunction

import android.net.Uri
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun UploadDialog(
    isDialogOpen: Boolean,
    filesUploaded: Int,
    totalFiles: Int,
    onDismiss: () -> Unit,
){
    if (isDialogOpen) {
        AlertDialog(
            onDismissRequest = { /* 阻止用户手动关闭，除非上传完成 */ },
            title = {
                Text(text = "Uploading Files")
            },
            text = {
                Column {
                    Text(text = "Uploading file $filesUploaded of $totalFiles")
                    Spacer(modifier = Modifier.height(8.dp))
                    // 使用 CircularProgressIndicator 或 LinearProgressIndicator
                    LinearProgressIndicator(
                        progress = { filesUploaded.toFloat() / totalFiles },
                    )
                }
            },
            confirmButton = {
                if (filesUploaded == totalFiles) {
                    Button(
                        onClick = onDismiss // 上传完成后允许关闭
                    ) {
                        Text("Close")
                    }
                }
            }
        )
    }
}