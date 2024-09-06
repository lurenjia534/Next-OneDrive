package com.lurenjia534.nextonedrive.MediaPreview

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import androidx.navigation.NavController
@Composable
fun VideoPreviewScreen(
    videoUrl: String,
    navController: NavController,
){
    val context = LocalContext.current
    var videoProgress by rememberSaveable { mutableLongStateOf(0L) }
    // 创建 ExoPlayer 实例
    val exoPlayer = ExoPlayer.Builder(context).build().apply {
        // 设置播放地址
        setMediaItem(MediaItem.fromUri(Uri.parse(videoUrl)))
        // 准备播放
        prepare()
        // 恢复播放进度
        seekTo(videoProgress)
        // 开始播放
        playWhenReady = true
    }
    Box(
        modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.primaryContainer)
    ) {
        AndroidView(
            factory = {ctx ->
                PlayerView(ctx).apply {
                    // 设置 ExoPlayer 实例
                    player = exoPlayer
                }
            },
            modifier = Modifier.fillMaxSize()
        )
        // other code
    }
    DisposableEffect(Unit) {
        // 在 Composable 销毁时释放资源
        onDispose {
            // 保存播放进度
            videoProgress = exoPlayer.currentPosition
            exoPlayer.release()
        }
    }
}