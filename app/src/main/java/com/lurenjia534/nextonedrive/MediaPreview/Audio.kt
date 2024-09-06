package com.lurenjia534.nextonedrive.MediaPreview

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import androidx.navigation.NavController

@Composable
fun AudioPreviewScreen(audioUrl: String, navController: NavController ) {
    val context = LocalContext.current
    val exoPlayer = remember {
        ExoPlayer.Builder(context).build().apply {
            val mediaItem = MediaItem.fromUri(audioUrl)
            setMediaItem(mediaItem)
            prepare() // 准备播放
        }
    }
    // 销毁时释放资源
    DisposableEffect(
        AndroidView(
            factory = {
                PlayerView(context).apply {
                    player = exoPlayer
                    useController = true // 显示播放控制器
                }
            },
            modifier = Modifier
                .fillMaxSize()
                .clickable {
                    navController.popBackStack() // 点击关闭预览
                }
        )
    ) {
        onDispose {
            exoPlayer.release()
        }
    }
}