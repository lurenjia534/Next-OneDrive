package com.lurenjia534.nextonedrive.MediaPreview

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import kotlinx.coroutines.launch

@Composable
fun ImagePreviewScreen(imageUrl: String,navController: NavController) {
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
    ) {
        AsyncImage(
            model = ImageRequest.Builder(context)
                .data(imageUrl)
              //  .error() // 设置加载错误时的占位符
                .build(),
            contentDescription = null,
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .fillMaxSize()
                .clickable {
                    // 在这里可以处理关闭图片预览或其他操作
                    navController.popBackStack()
                },
            onError = {
                coroutineScope.launch {
                    snackbarHostState.showSnackbar("Failed to load image")
                }
            }
        )
        // download button
        ExtendedFloatingActionButton(
            text = { Text("Download") },
            icon = { Icon(Icons.Default.Download, contentDescription = "Download")  },
            onClick = {
                coroutineScope.launch {
                    // 下载逻辑
                    try {
                        snackbarHostState.showSnackbar("Download started")
                    }catch (e : Exception){
                        snackbarHostState.showSnackbar("Download failed ${e.message}")
                    }
                }
            },
            modifier = Modifier.align(Alignment.BottomCenter).padding(16.dp)
        )
        // 刷新按钮
        FloatingActionButton(
            onClick = {
                coroutineScope.launch {
                    snackbarHostState.showSnackbar("Share")
                }
            },
            modifier = Modifier.align(Alignment.BottomEnd).padding(16.dp)
        ){
            Icon(Icons.Default.Share, contentDescription = "Share")
        }
        // 删除按钮
        FloatingActionButton(
            onClick = {
                // 删除逻辑
                coroutineScope.launch {
                    snackbarHostState.showSnackbar("Delete")
                }
            },
            modifier = Modifier.align(Alignment.BottomStart).padding(16.dp)
        ) {
            Icon(Icons.Default.Delete, contentDescription = "Delete")
        }
        // snackbar host
        SnackbarHost(hostState = snackbarHostState)
    }
}

