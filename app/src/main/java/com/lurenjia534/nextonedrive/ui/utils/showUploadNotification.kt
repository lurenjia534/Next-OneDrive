import android.Manifest
import android.app.Activity
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

val channelId = "upload_channel"
val notificationId = 101

// 创建通知渠道 (只在 Android 8.0+ 上需要)
fun createNotificationChannel(context: Context) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val name = "File Uploads"
        val descriptionText = "Notifications for file upload progress"
        val importance = NotificationManager.IMPORTANCE_LOW
        val channel = NotificationChannel(channelId, name, importance).apply {
            description = descriptionText
        }
        // 注册通知渠道
        val notificationManager: NotificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }
}

// 检查并请求通知权限 (针对 Android 13+)
fun checkNotificationPermission(context: Context): Boolean {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: 需要在 Activity 中处理权限请求
            ActivityCompat.requestPermissions(
                context as Activity,
                arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                1
            )
            return false
        }
    }
    return true
}

// 初始化上传通知
fun showUploadNotification(context: Context, totalFiles: Int) {
    if (!checkNotificationPermission(context)) return // 检查权限
    createNotificationChannel(context) // 确保通知渠道已创建

    val notificationBuilder = NotificationCompat.Builder(context, channelId)
        .setContentTitle("Uploading Files")
        .setContentText("Upload in progress")
        .setSmallIcon(android.R.drawable.stat_sys_upload)
        .setPriority(NotificationCompat.PRIORITY_LOW)
        .setProgress(totalFiles, 0, false)

    with(NotificationManagerCompat.from(context)) {
        notify(notificationId, notificationBuilder.build())
    }
}

// 更新通知进度
fun updateNotificationProgress(context: Context, filesUploaded: Int, totalFiles: Int) {
    if (!checkNotificationPermission(context)) return // 检查权限
    val notificationBuilder = NotificationCompat.Builder(context, channelId)
        .setContentTitle("Uploading Files")
        .setContentText("Uploaded $filesUploaded of $totalFiles")
        .setSmallIcon(android.R.drawable.stat_sys_upload)
        .setPriority(NotificationCompat.PRIORITY_LOW)
        .setProgress(totalFiles, filesUploaded, false)

    with(NotificationManagerCompat.from(context)) {
        notify(notificationId, notificationBuilder.build())
    }
}

// 上传完成后的通知
fun showUploadCompleteNotification(context: Context) {
    if (!checkNotificationPermission(context)) return // 检查权限
    val notificationBuilder = NotificationCompat.Builder(context, channelId)
        .setContentTitle("Upload Complete")
        .setContentText("All files uploaded successfully.")
        .setSmallIcon(android.R.drawable.stat_sys_upload_done)
        .setPriority(NotificationCompat.PRIORITY_LOW)
        .setProgress(0, 0, false)

    with(NotificationManagerCompat.from(context)) {
        notify(notificationId, notificationBuilder.build())
    }
}
