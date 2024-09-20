package com.lurenjia534.nextonedrive.ui.screens.outbox

// ui/screens/outbox/OutboxScreen.kt

import DriveInfoResponse
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.Build
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Face
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.lurenjia534.nextonedrive.Profilepage.fetchDriveInfo
import com.lurenjia534.nextonedrive.ui.components.SectionTitle
import com.lurenjia534.nextonedrive.ui.components.ProfileItem
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OutboxScreen() {
    var driveInfo by remember { mutableStateOf<DriveInfoResponse?>(null) }
    var errorMessage by remember { mutableStateOf("") }
    val snackbarHostState = remember { SnackbarHostState() }
    var showSuccessSnackbar by remember { mutableStateOf(false) }
    var showErrorSnackbar by remember { mutableStateOf(false) }

    val context = LocalContext.current
    //  Fetch DriveInfo when the screen is first loaded
    LaunchedEffect(Unit) {
        fetchDriveInfo(
            context = context,
            onSuccess = { info ->
                driveInfo = info
                showSuccessSnackbar = true // 重置状态
            },
            onError = { error ->
                errorMessage = error
                showErrorSnackbar = true // 重置状态
            }
        )
    }
    // Handle success and error Snackbar
    LaunchedEffect(showSuccessSnackbar, showErrorSnackbar) {
        if (showSuccessSnackbar) {
            snackbarHostState.showSnackbar("数据加载成功")
            showSuccessSnackbar = false // 重置状态
        }
        if (showErrorSnackbar) {
            snackbarHostState.showSnackbar("错误: $errorMessage")
            showErrorSnackbar = false // 重置状态
        }
    }
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("设置") },
                navigationIcon = {
                    IconButton(onClick = { /* Handle back button press */ }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
            )
        },
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        },
        content = { paddingValues ->
            if (driveInfo != null) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .padding(horizontal = 16.dp),
                    contentPadding = PaddingValues(vertical = 16.dp)
                ) {
                    item {
                        SectionTitle(title = "OneDrive 信息")
                    }
                    item {
                        ProfileItem(
                            icon = Icons.Outlined.Home,
                            title = "名称",
                            subtitle = driveInfo?.name ?: "N/A"
                        )
                    }
                    item {
                        ProfileItem(
                            icon = Icons.Outlined.Build,
                            title = "驱动器类型",
                            subtitle = driveInfo?.driveType ?: "N/A"
                        )
                    }
                    item {
                        ProfileItem(
                            icon = Icons.Outlined.AccountCircle,
                            title = "创建者",
                            subtitle = driveInfo?.createdBy?.user?.displayName ?: "N/A"
                        )
                    }
                    item {
                        ProfileItem(
                            icon = Icons.Outlined.Face,
                            title = "最后修改者",
                            subtitle = driveInfo?.lastModifiedBy?.user?.displayName ?: "N/A"
                        )
                    }
                    item {
                        ProfileItem(
                            icon = Icons.Outlined.Email,
                            title = "邮箱",
                            subtitle = driveInfo?.owner?.user?.email ?: "N/A"
                        )
                    }

                    item {
                        Spacer(modifier = Modifier.height(16.dp))
                        SectionTitle(title = "存储信息")
                    }
                    item {
                        ProfileItem(
                            icon = Icons.Outlined.Delete,
                            title = "已删除存储",
                            subtitle = "${driveInfo?.quota?.deleted?.div(1024 * 1024)} MB"
                        )
                    }
                    item {
                        ProfileItem(
                            icon = Icons.Outlined.Edit,
                            title = "剩余存储",
                            subtitle = "${driveInfo?.quota?.remaining?.div(1024L * 1024 * 1024 * 1024)} TB"
                        )
                    }
                    item {
                        ProfileItem(
                            icon = Icons.Outlined.Info,
                            title = "总存储空间",
                            subtitle = "${driveInfo?.quota?.total?.div(1024L * 1024 * 1024 * 1024)} TB"
                        )
                    }
                    item {
                        ProfileItem(
                            icon = Icons.Outlined.CheckCircle,
                            title = "已用存储空间",
                            subtitle = "${driveInfo?.quota?.used?.div(1024L * 1024 * 1024)} GB"
                        )
                    }
                }
            }
        }
    )
}