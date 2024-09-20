package com.lurenjia534.nextonedrive.ui.screens.favorites

import android.Manifest
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.InsertDriveFile
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Folder
import androidx.compose.material.icons.outlined.Image
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.InsertDriveFile
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material.icons.outlined.VideoFile
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FabPosition
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.lurenjia534.nextonedrive.ui.dialogs.ShareLinkDialog
import com.lurenjia534.nextonedrive.ui.dialogs.UploadDialog
import com.lurenjia534.nextonedrive.Filefunction.createFolder
import com.lurenjia534.nextonedrive.Filefunction.createShareableLink
import com.lurenjia534.nextonedrive.Filefunction.deleteDriveItem
import com.lurenjia534.nextonedrive.Filefunction.uploadFile
import com.lurenjia534.nextonedrive.ListItem.DriveItem
import com.lurenjia534.nextonedrive.ListItem.fetchDriveItemChildren
import com.lurenjia534.nextonedrive.ListItem.fetchDriveItems
import createNotificationChannel
import kotlinx.coroutines.launch
import showUploadCompleteNotification
import showUploadNotification
import updateNotificationProgress
import java.net.URLEncoder
import java.util.Locale

// ui/screens/favorites/FavoritesScreen.kt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoritesScreen(navController: NavController) {
    var showBottomSheet by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()
    var selectedImageUris by remember { mutableStateOf<List<Uri>>(emptyList()) }

    // Status for holding the list of DriveItems
    var driveItems by remember { mutableStateOf<List<DriveItem>?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf("") }
    // ErrorSnackbar state
    val snackbarHostState = remember { SnackbarHostState() }
    // 当前文件夹ID和上一级文件夹ID
    var currentFolderId by remember { mutableStateOf<String?>(null) }
    var previousFolderId by remember { mutableStateOf<List<String>>(emptyList()) }

    // 动画状态
    val animatedHeight by animateDpAsState(
        targetValue = if (showBottomSheet) 300.dp else 0.dp,
        animationSpec = tween(durationMillis = 300), label = ""
    )

    val animatedAlpha by animateFloatAsState(
        targetValue = if (showBottomSheet) 1f else 0f,
        animationSpec = tween(durationMillis = 300), label = ""
    )

    val buttonAlpha by animateFloatAsState(
        targetValue = if (showBottomSheet) 0f else 1f,
        animationSpec = tween(durationMillis = 300), label = ""
    )
    val context = LocalContext.current

    // Fetch DriveItems when the screen is first loaded
    fun loadItems(itemId: String? = null) {
        isLoading = true
        errorMessage = ""
        if (itemId == null) {
            fetchDriveItems(
                context,
                onSuccess = {
                    driveItems = it
                    isLoading = false
                    //   coroutineScope.launch { snackbarHostState.showSnackbar("数据加载成功") }
                },
                onError = { error ->
                    errorMessage = error
                    isLoading = false
                    coroutineScope.launch { snackbarHostState.showSnackbar("错误: $error") }
                }
            )
        } else {
            fetchDriveItemChildren(
                context,
                itemId,
                onSuccess = {
                    driveItems = it
                    isLoading = false
                    //       coroutineScope.launch { snackbarHostState.showSnackbar("数据加载成功") }
                },
                onError = { error ->
                    errorMessage = error
                    isLoading = false
                    coroutineScope.launch { snackbarHostState.showSnackbar("错误: $error") }
                }
            )
        }
    }
    LaunchedEffect(Unit) {
        println("Current Folder ID: $currentFolderId")
        println("Previous Folder IDs: $previousFolderId")
        loadItems()
    }

    var showUploadDialog by remember { mutableStateOf(false) }
    var filesUploaded by remember { mutableIntStateOf(0) }
    var totalFiles by remember { mutableIntStateOf(0) }

    // 上传文件时的对话框
    UploadDialog(
        isDialogOpen = showUploadDialog,
        filesUploaded = filesUploaded,
        totalFiles = totalFiles,
        onDismiss = {
            showUploadDialog = false // 关闭对话框
        }
    )

    // 图片选择器的 Launcher，支持多选
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickMultipleVisualMedia(),
        onResult = { uris ->
            if (uris.isNotEmpty()) {
                Log.d("ImagePicker", "Selected Image URIs: $uris")
                coroutineScope.launch {
                    // 关闭底部表单 显示上传对话框
                    showBottomSheet = false
                    showUploadDialog = true
                    createNotificationChannel(context) // 确保通知渠道已创建
                    // 设置文件总数并初始化上传的文件数
                    totalFiles = uris.size
                    filesUploaded = 0
                    // 显示 Snackbar
                    snackbarHostState.showSnackbar("Selected ${uris.size} images")
                    // 显示上传通知
                    showUploadNotification(context, totalFiles)
                    // 上传图片
                    uris.forEachIndexed { index, uri ->
                        uploadFile(
                            context = context,
                            uri = uri,  // 将 Uri 传递给上传函数
                            parentId = currentFolderId ?: "root", // 父文件夹 ID
                            onSuccess = { driveItem ->
                                Log.d("ImageUpload", "Image uploaded: ${driveItem.name}")
                                // 更新上传的文件数
                                filesUploaded = index + 1
                                // 更新通知进度
                                updateNotificationProgress(context, filesUploaded, totalFiles)
                                // 上传成功后，重新加载文件列表
                                if (filesUploaded == totalFiles) {
                                    loadItems(currentFolderId)
                                    // 关闭上传对话框
                                    showUploadDialog = false
                                    // 上传完成通知
                                    showUploadCompleteNotification(context)
                                }
                            },
                            onError = { error ->
                                Log.e("ImageUpload", "Error uploading image: $error")
                                coroutineScope.launch {
                                    snackbarHostState.showSnackbar("Error: $error")
                                    showUploadDialog = false  // 上传失败时关闭对话框
                                }
                            }
                        )
                    }
                }
                // 这里可以处理选中的图片列表 `uris`
                selectedImageUris = uris // 保存选中的 URIs
            } else {
                Log.d("ImagePicker", "No image selected")
            }
        }
    )


    // 权限请求
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            // Permission is granted
            // 如果权限被授予，启动图片选择器（多选）
            photoPickerLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        } else {
            // Permission is denied
            coroutineScope.launch {
                snackbarHostState.showSnackbar("Permission denied")
            }
        }
    }


    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Favorites") },
                navigationIcon = {
                    IconButton(onClick = {
                        if (previousFolderId.isNotEmpty()) {
                            val lastFolderId = previousFolderId.last()
                            previousFolderId = previousFolderId.dropLast(1)
                            currentFolderId = lastFolderId

                            // 确保lastFolderId不为null
                            if (previousFolderId.isEmpty()) {
                                // 如果上一级ID为空，加载根目录
                                loadItems()
                            }
                        } else {
                            // 如果没有上级目录，返回根目录
                            currentFolderId = null
                            loadItems()
                        }
                    }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = {
                    coroutineScope.launch {
                        showBottomSheet = true
                    }
                },
                icon = { Icon(Icons.Outlined.Add, contentDescription = "Add") },
                text = { Text("Add") },
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.primary,
                modifier = Modifier.alpha(buttonAlpha) // 控制按钮的透明度
            )
        },
        floatingActionButtonPosition = FabPosition.End,
        snackbarHost = {
            SnackbarHost(snackbarHostState)
        },
        content = { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                when {
                    isLoading -> {
                        CircularProgressIndicator() // 显示加载指示器
                    }

                    errorMessage.isNotEmpty() -> {
                        Text(
                            text = errorMessage
                        ) // 显示错误消息
                    }

                    driveItems != null -> {
                        LazyColumn {
                            item {
                                // 获取列表路径
                                val currentPath =
                                    driveItems?.firstOrNull()?.parentReference?.path?.replace(
                                        "/drive/root:",
                                        "/ > "
                                    ) ?: "/"
                                Text(
                                    text = currentPath,
                                    style = TextStyle(
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 24.sp
                                    ),
                                    modifier = Modifier.padding(bottom = 32.dp, start = 16.dp),
                                    color = MaterialTheme.colorScheme.primary,

                                    )
                            }
                            items(driveItems!!) { item ->
                                ListItem(
                                    headlineContent = { Text(item.name) },
                                    supportingContent = {
                                        val folderSize = item.size.toDouble() / 1024 / 1024 / 1024
                                        val fileSize = item.size.toDouble() / 1024 / 1024
                                        Text(
                                            if (item.folder != null)
                                                String.format(
                                                    Locale.getDefault(),
                                                    "%.2f GB",
                                                    folderSize
                                                )
                                            else
                                                String.format(
                                                    Locale.getDefault(),
                                                    "%.2f MB",
                                                    fileSize
                                                )
                                        )
                                    },
                                    leadingContent = {
                                        Icon(
                                            imageVector = when {
                                                item.folder != null -> Icons.Outlined.Folder
                                                item.file?.mimeType?.startsWith("image/") == true -> Icons.Outlined.Image
                                                item.file?.mimeType?.startsWith("application/octet-stream") == true -> Icons.Outlined.VideoFile
                                                else -> Icons.AutoMirrored.Outlined.InsertDriveFile
                                            },
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.primary,
                                            modifier = Modifier.size(24.dp)
                                        )
                                    },
                                    modifier = Modifier.clickable {
                                        try {
                                            when {
                                                item.folder != null -> {
                                                    // 如果是文件夹，更新目录 ID 并加载该文件夹的内容
                                                    previousFolderId =
                                                        previousFolderId + currentFolderId.orEmpty()
                                                    currentFolderId = item.id
                                                    loadItems(item.id)
                                                }

                                                item.file != null && item.downloadUrl != null -> {
                                                    // 如果是文件，先进行 URL 编码
                                                    val encodedUrl =
                                                        URLEncoder.encode(item.downloadUrl, "UTF-8")
                                                    println("Navigating to: image_preview/$encodedUrl")  // 打印 URL

                                                    // 判断 MIME 类型，决定是导航到图片预览还是视频预览
                                                    when {
                                                        item.file.mimeType.startsWith("image/") -> {
                                                            navController.navigate("image_preview/$encodedUrl")
                                                        }

                                                        item.file.mimeType.startsWith("video/") -> {
                                                            navController.navigate("video_preview/$encodedUrl")
                                                        }

                                                        item.file.mimeType == "application/octet-stream" -> {
                                                            // 处理 mkv 文件的情况
                                                            navController.navigate("video_preview/$encodedUrl")
                                                        }

                                                        item.file.mimeType.startsWith("audio/") -> {
                                                            navController.navigate("audio_preview/$encodedUrl")
                                                        }
                                                        // 如果MIME Type 为 application/octet-stream，通过文件扩展名判断
                                                        item.file.mimeType == "application/octet-stream" -> {
                                                            when {
                                                                // 视频文件类型
                                                                item.name.endsWith(
                                                                    ".mkv",
                                                                    ignoreCase = true
                                                                ) ||
                                                                        item.name.endsWith(
                                                                            ".mp4",
                                                                            ignoreCase = true
                                                                        ) ||
                                                                        item.name.endsWith(
                                                                            ".avi",
                                                                            ignoreCase = true
                                                                        ) ||
                                                                        item.name.endsWith(
                                                                            ".mov",
                                                                            ignoreCase = true
                                                                        ) ||
                                                                        item.name.endsWith(
                                                                            ".wmv",
                                                                            ignoreCase = true
                                                                        ) ||
                                                                        item.name.endsWith(
                                                                            ".flv",
                                                                            ignoreCase = true
                                                                        ) -> {
                                                                    navController.navigate("video_preview/$encodedUrl")
                                                                }
                                                                // 音频文件类型
                                                                item.name.endsWith(
                                                                    ".mp3",
                                                                    ignoreCase = true
                                                                ) ||
                                                                        item.name.endsWith(
                                                                            ".flac",
                                                                            ignoreCase = true
                                                                        ) ||
                                                                        item.name.endsWith(
                                                                            ".wav",
                                                                            ignoreCase = true
                                                                        ) ||
                                                                        item.name.endsWith(
                                                                            ".aac",
                                                                            ignoreCase = true
                                                                        ) ||
                                                                        item.name.endsWith(
                                                                            ".ogg",
                                                                            ignoreCase = true
                                                                        ) ||
                                                                        item.name.endsWith(
                                                                            ".m4a",
                                                                            ignoreCase = true
                                                                        ) -> {
                                                                    navController.navigate("audio_preview/$encodedUrl")
                                                                }

                                                                item.name.endsWith(
                                                                    ".png",
                                                                    ignoreCase = true
                                                                ) ||
                                                                        item.name.endsWith(
                                                                            ".jpg",
                                                                            ignoreCase = true
                                                                        ) ||
                                                                        item.name.endsWith(
                                                                            ".jpeg",
                                                                            ignoreCase = true
                                                                        ) -> {
                                                                    navController.navigate("image_preview/$encodedUrl")
                                                                }
                                                            }
                                                        }

                                                        else -> {
                                                            coroutineScope.launch {
                                                                snackbarHostState.showSnackbar("Unsupported file type")
                                                            }
                                                        }
                                                    }
                                                }

                                                else -> {
                                                    coroutineScope.launch {
                                                        snackbarHostState.showSnackbar("No action available")
                                                    }
                                                }
                                            }
                                        } catch (e: Exception) {
                                            // 捕获任何异常并显示错误信息
                                            coroutineScope.launch {
                                                snackbarHostState.showSnackbar("Error: ${e.message}")
                                            }
                                        }
                                    },

                                    trailingContent = {
                                        var expanded by remember { mutableStateOf(false) }
                                        var showDialog by remember { mutableStateOf(false) }
                                        val context = LocalContext.current
                                        Box {
                                            IconButton(onClick = { expanded = true }) {
                                                Icon(
                                                    imageVector = Icons.Outlined.MoreVert,
                                                    contentDescription = null,
                                                    tint = MaterialTheme.colorScheme.primary
                                                )
                                            }
                                            DropdownMenu(
                                                expanded = expanded,
                                                onDismissRequest = { expanded = false }
                                            ) {
                                                DropdownMenuItem(
                                                    leadingIcon = {
                                                        Icon(
                                                            imageVector = Icons.Outlined.Info,
                                                            contentDescription = "Info"
                                                        )
                                                    },
                                                    text = { Text("info") },
                                                    onClick = {
                                                        expanded = false
                                                        // 逻辑
                                                    }
                                                )
                                                HorizontalDivider()
                                                DropdownMenuItem(
                                                    leadingIcon = {
                                                        Icon(
                                                            imageVector = Icons.Outlined.Delete,
                                                            contentDescription = "Delete"
                                                        )
                                                    },
                                                    text = { Text("Delete") },
                                                    onClick = {
                                                        expanded = false
                                                        coroutineScope.launch {
                                                            deleteDriveItem(
                                                                context = context,
                                                                itemId = item.id, // 传递文件ID
                                                                onSuccess = {
                                                                    loadItems(currentFolderId)
                                                                    coroutineScope.launch {
                                                                        snackbarHostState.showSnackbar(
                                                                            "File deleted successfully"
                                                                        )
                                                                    }
                                                                },
                                                                onError = {
                                                                    loadItems(currentFolderId)
                                                                    coroutineScope.launch {
                                                                        snackbarHostState.showSnackbar(
                                                                            "File deleted successfully"
                                                                        )
                                                                    }
                                                                }
                                                            )
                                                        }
                                                    }
                                                )
                                                DropdownMenuItem(
                                                    leadingIcon = {
                                                        Icon(
                                                            imageVector = Icons.Outlined.Share,
                                                            contentDescription = "Share"
                                                        )
                                                    },
                                                    text = { Text("Share") },
                                                    onClick = {
                                                        expanded = false
                                                        // 分享文件的逻辑
                                                        showDialog = true
                                                    }
                                                )
                                            }
                                            // 使用 LaunchedEffect 以确保 Dialog 在 DropdownMenu 完全关闭后显示
                                            LaunchedEffect(expanded) {
                                                if (!expanded && showDialog) {
                                                    showDialog = true
                                                }
                                            }

                                            if (showDialog) {
                                                ShareLinkDialog(
                                                    onDismiss = { showDialog = false },
                                                    onShareLinkCreated = { linkType, scope ->
                                                        coroutineScope.launch {
                                                            createShareableLink(
                                                                context = context,
                                                                itemId = item.id,  // 使用当前项目的ID
                                                                linkType = linkType,
                                                                scope = scope,
                                                                onSuccess = { shareLink ->
                                                                    coroutineScope.launch {
                                                                        snackbarHostState.showSnackbar(
                                                                            "Link created: $shareLink"
                                                                        )
                                                                    }
                                                                },
                                                                onError = { error ->
                                                                    coroutineScope.launch {
                                                                        snackbarHostState.showSnackbar(
                                                                            "Error: $error"
                                                                        )
                                                                    }
                                                                }
                                                            )
                                                        }
                                                    }
                                                )
                                            }
                                        }
                                    },
                                )
                                // HorizontalDivider()
                            }
                        }
                    }

                    else -> {
                        Text(text = "No data found")
                    }
                }
            }

            if (animatedHeight > 0.dp) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = animatedAlpha * 0.5f))
                        .clickable { showBottomSheet = false } // 点击外部关闭
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(animatedHeight)
                            .background(
                                color = MaterialTheme.colorScheme.surface,
                                shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
                            )
                            .align(Alignment.BottomCenter)
                            .padding(16.dp)
                            .alpha(animatedAlpha)
                    ) {
                        Spacer(modifier = Modifier.height(16.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(
                                16.dp,
                                Alignment.CenterHorizontally
                            )
                        ) {
                            // Share Button
                            IconButton(
                                onClick = {},
                                modifier = Modifier
                                    .size(72.dp)
                                    .border(1.dp, Color.Gray, RoundedCornerShape(8.dp))
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Icon(Icons.Outlined.Share, contentDescription = "Share")
                                    Text(
                                        "Share",
                                        style = MaterialTheme.typography.bodySmall,
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }

                            // Upload Image Button
                            IconButton(
                                onClick = {
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                        // Android 13 及以上，使用 READ_MEDIA_IMAGES 权限
                                        permissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES)
                                    } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                                        // Android 10 到 Android 12，使用 READ_EXTERNAL_STORAGE 权限
                                        permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
                                    } else {
                                        // Android 9 及以下，使用常规存储权限
                                        permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
                                    }
                                },
                                modifier = Modifier
                                    .size(72.dp)
                                    .border(1.dp, Color.Gray, RoundedCornerShape(8.dp))
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Icon(Icons.Outlined.Image, contentDescription = "Image")
                                    Text(
                                        "Image",
                                        style = MaterialTheme.typography.bodySmall,
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }
                            // Upload File Button
                            IconButton(
                                onClick = {},
                                modifier = Modifier
                                    .size(72.dp)
                                    .border(1.dp, Color.Gray, RoundedCornerShape(8.dp))
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Icon(
                                        Icons.Outlined.InsertDriveFile,
                                        contentDescription = "File"
                                    )
                                    Text(
                                        "Upload File",
                                        style = MaterialTheme.typography.bodySmall,
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }
                            // New Folder Button
                            var showDialog by remember { mutableStateOf(false) }
                            var folderName by remember { mutableStateOf("") }
                            IconButton(
                                onClick = {
                                    showDialog = true // 显示对话框
                                },
                                modifier = Modifier
                                    .size(72.dp)
                                    .border(1.dp, Color.Gray, RoundedCornerShape(8.dp))
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Icon(Icons.Outlined.Folder, contentDescription = "Folder")
                                    Text("New Folder", style = MaterialTheme.typography.bodySmall)
                                }
                            }
                            // Dialog
                            if (showDialog) {
                                AlertDialog(
                                    onDismissRequest = {
                                        showDialog = false // 当对话框外部被点击时关闭对话框
                                    },
                                    title = {
                                        Text(text = "Create New Folder")
                                    },
                                    text = {
                                        Column {
                                            Text("Enter folder name:")
                                            OutlinedTextField(
                                                value = folderName,
                                                onValueChange = { folderName = it },
                                                label = { Text("Folder Name") }
                                            )
                                        }
                                    },
                                    confirmButton = {
                                        TextButton(
                                            onClick = {
                                                if (folderName.isNotBlank()) {
                                                    coroutineScope.launch {
                                                        try {
                                                            createFolder(
                                                                context = context,
                                                                folderName = folderName,
                                                                parentFolderId = currentFolderId, // 传递当前文件夹ID
                                                                onSuccess = {
                                                                    coroutineScope.launch {
                                                                        loadItems(currentFolderId)
                                                                        showDialog = false // 关闭对话框
                                                                        folderName = "" // 清空输入框
                                                                        showBottomSheet = false
                                                                        snackbarHostState.showSnackbar(
                                                                            "Folder created successfully"
                                                                        )
                                                                    }
                                                                },
                                                                onError = {
                                                                    coroutineScope.launch {
                                                                        loadItems(currentFolderId)
                                                                        showDialog = false // 关闭对话框
                                                                        folderName = "" // 清空输入框
                                                                        showBottomSheet = false
                                                                        snackbarHostState.showSnackbar(
                                                                            "Error: $it"
                                                                        )
                                                                    }
                                                                }
                                                            )
                                                        } catch (e: Exception) {
                                                            showDialog = false // 关闭对话框
                                                            folderName = "" // 清空输入框
                                                            snackbarHostState.showSnackbar("Error: ${e.message}") // 显示错误消息
                                                        }
                                                    }
                                                }
                                            }
                                        ) {
                                            Text("Create")
                                        }
                                    },
                                    dismissButton = {
                                        TextButton(
                                            onClick = {
                                                showDialog = false // 取消按钮关闭对话框
                                            }
                                        ) {
                                            Text("Cancel")
                                        }
                                    }
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = {
                                coroutineScope.launch {
                                    showBottomSheet = false
                                }
                            },
                            modifier = Modifier.align(Alignment.End)
                        ) {
                            Text("Close")
                        }
                    }
                }
            }
        }
    )
}