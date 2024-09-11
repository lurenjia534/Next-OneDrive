package com.lurenjia534.nextonedrive

import DriveInfoResponse
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.InsertDriveFile
import androidx.compose.material.icons.automirrored.outlined.Send
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.ArrowDropDown
import androidx.compose.material.icons.outlined.Build
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Face
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Folder
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Image
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.InsertDriveFile
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material.icons.outlined.VideoFile
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.lurenjia534.nextonedrive.Filefunction.createFolder
import com.lurenjia534.nextonedrive.ListItem.DriveItem
import com.lurenjia534.nextonedrive.ListItem.fetchDriveItemChildren
import com.lurenjia534.nextonedrive.ListItem.fetchDriveItems
import com.lurenjia534.nextonedrive.MediaPreview.AudioPreviewScreen
import com.lurenjia534.nextonedrive.MediaPreview.ImagePreviewScreen
import com.lurenjia534.nextonedrive.MediaPreview.VideoPreviewScreen
import com.lurenjia534.nextonedrive.OAuthToken.fetchAccessToken
import com.lurenjia534.nextonedrive.Profilepage.fetchDriveInfo
import com.lurenjia534.nextonedrive.ui.theme.NextOneDriveTheme
import kotlinx.coroutines.launch
import java.net.URLEncoder
import java.util.Locale

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            NextOneDriveTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    MyNavigationDrawer(innerPadding)
                }
            }
        }
    }
}

@Preview
@Composable
fun MyNavigationDrawer(
    innerPadding: PaddingValues = PaddingValues(0.dp)
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val navController = rememberNavController()
    val scope = rememberCoroutineScope()

    // 定义抽屉物品列表及其对应的路线
    val items = listOf("inbox", "outbox", "favorites", "trash", "label/1", "label/2")
    val selectedItem = remember { mutableStateOf(items[0]) }
    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Text(
                    text = "Next OneDrive",
                    modifier = Modifier.padding(16.dp),
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 24.sp
                    )
                )
                // Loop through items to create the NavigationDrawerItem for each
                items.forEach { item ->
                    NavigationDrawerItem(
                        modifier = Modifier.padding(
                            start = 16.dp,
                            top = 8.dp,
                            end = 16.dp,
                            bottom = 8.dp
                        ),
                        label = {
                            Text(item.replaceFirstChar {
                                if (it.isLowerCase()) it.titlecase(
                                    Locale.ROOT
                                ) else it.toString()
                            })
                        },
                        selected = item == selectedItem.value,
                        onClick = {
                            scope.launch {
                                drawerState.close()
                            }
                            selectedItem.value = item
                            navController.navigate(item) {
                                popUpTo(navController.graph.startDestinationId) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = {
                            Icon(
                                imageVector = when (item) {
                                    "inbox" -> Icons.Outlined.Home
                                    "outbox" -> Icons.AutoMirrored.Outlined.Send
                                    "favorites" -> Icons.Outlined.FavoriteBorder
                                    "trash" -> Icons.Outlined.Delete
                                    else -> Icons.Outlined.ArrowDropDown
                                },
                                contentDescription = null
                            )
                        }
                    )
                }
            }
        },
        content = {
            NavHost(
                navController = navController,
                startDestination = "inbox"
            ) {
                composable("inbox") { InboxScreen(navController) }
                composable("outbox") { OutboxScreen() }
                composable("favorites") { FavoritesScreen(navController) }
                composable("trash") { TrashScreen() }
                composable(
                    route = "label/{labelId}",
                    arguments = listOf(navArgument("labelId") { type = NavType.StringType })
                ) { backStackEntry ->
                    val labelId = backStackEntry.arguments?.getString("labelId") ?: "Unknown"
                    LabelScreen(label = labelId)
                }
                composable(
                    route = "image_preview/{imageUrl}",
                    arguments = listOf(navArgument("imageUrl") { type = NavType.StringType })
                ) { backStackEntry ->
                    val imageUrl = backStackEntry.arguments?.getString("imageUrl") ?: ""
                    ImagePreviewScreen(imageUrl = imageUrl, navController = navController)
                }
                composable(
                    route = "video_preview/{videoUrl}",
                    arguments = listOf(navArgument("videoUrl") { type = NavType.StringType })
                ) { backStackEntry ->
                    val videoUrl = backStackEntry.arguments?.getString("videoUrl") ?: ""
                    VideoPreviewScreen(videoUrl = videoUrl, navController = navController)
                }
                composable(
                    route = "audio_preview/{audioUrl}",
                    arguments = listOf(navArgument("audioUrl") { type = NavType.StringType })
                ){ backStackEntry ->
                    val audioUrl = backStackEntry.arguments?.getString("audioUrl") ?: ""
                    AudioPreviewScreen(audioUrl = audioUrl, navController = navController)

                }
            }
        }
    )
}

@Composable
fun InboxScreen(navController: NavController) {
    // State variables for input fields
    var tenantId by remember { mutableStateOf("") }
    var clientId by remember { mutableStateOf("") }
    var clientSecret by remember { mutableStateOf("") }
    var userId by remember { mutableStateOf("") }
    var grantType by remember { mutableStateOf("client_credentials") }
    var scope by remember { mutableStateOf("https://graph.microsoft.com/.default offline_access") }
    var isLoading by remember { mutableStateOf(false) }

    // ErrorSnackbar state
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    // Main layout
    Scaffold(
        snackbarHost = {
            SnackbarHost(snackbarHostState)
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Column(
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(16.dp),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Title
                Text(
                    text = "Next OneDrive LOGIN",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontSize = 24.sp, fontWeight = FontWeight.Bold
                    ),
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                // Animated Cards for inputs
                AnimatedCardWithInput(
                    value = tenantId,
                    onValueChange = { tenantId = it },
                    label = "Tenant ID"
                )

                AnimatedVisibility(visible = tenantId.isNotBlank()) {
                    AnimatedCardWithInput(
                        value = clientId,
                        onValueChange = { clientId = it },
                        label = "Client ID"
                    )
                }

                AnimatedVisibility(visible = clientId.isNotBlank()) {
                    AnimatedCardWithInput(
                        value = clientSecret,
                        onValueChange = { clientSecret = it },
                        label = "Client Secret",
                        isPassword = true
                    )
                }

                AnimatedVisibility(visible = clientSecret.isNotBlank()) {
                    AnimatedCardWithInput(
                        value = userId,
                        onValueChange = { userId = it },
                        label = "User ID"
                    )
                }

                AnimatedVisibility(visible = userId.isNotBlank()) {
                    AnimatedCardWithInput(
                        value = grantType,
                        onValueChange = { grantType = it },
                        label = "Grant Type"
                    )
                }

                AnimatedVisibility(
                    visible =  tenantId.isNotBlank() && clientId.isNotBlank() && clientSecret.isNotBlank() && userId.isNotBlank() && grantType.isNotBlank()
                ) {
                    AnimatedCardWithInput(
                        value = scope,
                        onValueChange = { scope = it },
                        label = "Scope"
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Login Button
                AnimatedVisibility(visible = scope.isNotBlank()) {
                    Button(
                        onClick = {
                            isLoading = true
                            fetchAccessToken(
                                context = context,
                                tenantId = tenantId,
                                clientId = clientId,
                                clientSecret = clientSecret,
                                grantType = grantType,
                                scope = scope,
                                userId = userId,
                                onError = { message ->
                                    coroutineScope.launch {
                                        snackbarHostState.showSnackbar("Error: $message")
                                    }
                                    isLoading = false
                                },
                                onSuccess = { token ->
                                    coroutineScope.launch {
                                        snackbarHostState.showSnackbar("Login Successful")
                                    }
                                    isLoading = false
                                    navController.navigate("outbox")
                                }
                            )
                        },
                        modifier = Modifier.fillMaxWidth().height(50.dp),
                        enabled = !isLoading
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(24.dp)
                            )
                        } else {
                            Text(text = "Login", color = MaterialTheme.colorScheme.onPrimary)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AnimatedCardWithInput(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    isPassword: Boolean = false
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        elevation = CardDefaults.cardElevation(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface
        )
    ) {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            label = { Text(text = label) },
            visualTransformation = if (isPassword) PasswordVisualTransformation() else VisualTransformation.None,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            singleLine = true
        )
    }
}


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
                        SettingItem(
                            icon = Icons.Outlined.Home,
                            title = "名称",
                            subtitle = driveInfo?.name ?: "N/A"
                        )
                    }
                    item {
                        SettingItem(
                            icon = Icons.Outlined.Build,
                            title = "驱动器类型",
                            subtitle = driveInfo?.driveType ?: "N/A"
                        )
                    }
                    item {
                        SettingItem(
                            icon = Icons.Outlined.AccountCircle,
                            title = "创建者",
                            subtitle = driveInfo?.createdBy?.user?.displayName ?: "N/A"
                        )
                    }
                    item {
                        SettingItem(
                            icon = Icons.Outlined.Face,
                            title = "最后修改者",
                            subtitle = driveInfo?.lastModifiedBy?.user?.displayName ?: "N/A"
                        )
                    }
                    item {
                        SettingItem(
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
                        SettingItem(
                            icon = Icons.Outlined.Delete,
                            title = "已删除存储",
                            subtitle = "${driveInfo?.quota?.deleted?.div(1024 * 1024)} MB"
                        )
                    }
                    item {
                        SettingItem(
                            icon = Icons.Outlined.Edit,
                            title = "剩余存储",
                            subtitle = "${driveInfo?.quota?.remaining?.div(1024L * 1024 * 1024 * 1024)} TB"
                        )
                    }
                    item {
                        SettingItem(
                            icon = Icons.Outlined.Info,
                            title = "总存储空间",
                            subtitle = "${driveInfo?.quota?.total?.div(1024L * 1024 * 1024 * 1024)} TB"
                        )
                    }
                    item {
                        SettingItem(
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

@Composable
fun SectionTitle(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium.copy(
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Bold
        ),
        modifier = Modifier
            .padding(vertical = 8.dp)
            .padding(horizontal = 8.dp)
    )
}

@Composable
fun SettingItem(icon: ImageVector, title: String, subtitle: String? = null) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable { /* Handle item click */ },
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface
        ),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
                if (subtitle != null) {
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoritesScreen(navController: NavController) {
    var showBottomSheet by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()
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
    // Status for holding the list of DriveItems
    var driveItems by remember { mutableStateOf<List<DriveItem>?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf("") }
    // ErrorSnackbar state
    val snackbarHostState = remember { SnackbarHostState() }
    var currentFolderId by remember { mutableStateOf<String?>(null) }
    var previousFolderId by remember { mutableStateOf<List<String>>(emptyList()) }
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
                                val currentPath = driveItems?.firstOrNull()?.parentReference?.path?.replace("/drive/root:", "/ > ") ?: "/"
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
                                            imageVector = when{
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
                                                    previousFolderId = previousFolderId + currentFolderId.orEmpty()
                                                    currentFolderId = item.id
                                                    loadItems(item.id)
                                                }
                                                item.file != null && item.downloadUrl != null -> {
                                                    // 如果是文件，先进行 URL 编码
                                                    val encodedUrl = URLEncoder.encode(item.downloadUrl, "UTF-8")
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
                                                            when{
                                                                // 视频文件类型
                                                                item.name.endsWith(".mkv", ignoreCase = true) ||
                                                                        item.name.endsWith(".mp4", ignoreCase = true) ||
                                                                        item.name.endsWith(".avi", ignoreCase = true) ||
                                                                        item.name.endsWith(".mov", ignoreCase = true) ||
                                                                        item.name.endsWith(".wmv", ignoreCase = true) ||
                                                                        item.name.endsWith(".flv", ignoreCase = true) -> {
                                                                    navController.navigate("video_preview/$encodedUrl")
                                                                }
                                                                // 音频文件类型
                                                                item.name.endsWith(".mp3", ignoreCase = true) ||
                                                                        item.name.endsWith(".flac", ignoreCase = true) ||
                                                                        item.name.endsWith(".wav", ignoreCase = true) ||
                                                                        item.name.endsWith(".aac", ignoreCase = true) ||
                                                                        item.name.endsWith(".ogg", ignoreCase = true) ||
                                                                        item.name.endsWith(".m4a", ignoreCase = true) -> {
                                                                    navController.navigate("audio_preview/$encodedUrl")
                                                                }
                                                                item.name.endsWith(".png", ignoreCase = true) ||
                                                                        item.name.endsWith(".jpg", ignoreCase = true) ||
                                                                        item.name.endsWith(".jpeg", ignoreCase = true) -> {
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
                                        Icon(
                                            imageVector = Icons.Outlined.MoreVert,
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.primary,
                                        )
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
                            horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterHorizontally)
                        ) {
                            // Share Button
                            IconButton(
                                onClick = {},
                                modifier = Modifier.size(72.dp).border(1.dp, Color.Gray, RoundedCornerShape(8.dp))
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Icon(Icons.Outlined.Share, contentDescription = "Share")
                                    Text("Share", style = MaterialTheme.typography.bodySmall, textAlign = TextAlign.Center)
                                }
                            }
                            // Upload Image Button
                            IconButton(
                                onClick = {},
                                modifier = Modifier.size(72.dp).border(1.dp, Color.Gray, RoundedCornerShape(8.dp))
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Icon(Icons.Outlined.Image, contentDescription = "Image")
                                    Text("Image", style = MaterialTheme.typography.bodySmall, textAlign = TextAlign.Center)
                                }
                            }
                            // Upload File Button
                            IconButton(
                                onClick = {},
                                modifier = Modifier.size(72.dp).border(1.dp, Color.Gray, RoundedCornerShape(8.dp))
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Icon(Icons.Outlined.InsertDriveFile, contentDescription = "File")
                                    Text("Upload File", style = MaterialTheme.typography.bodySmall, textAlign = TextAlign.Center)
                                }
                            }
                            // New Folder Button
                            var showDialog by remember { mutableStateOf(false) }
                            var folderName by remember { mutableStateOf("") }
                            IconButton(
                                onClick = {
                                        showDialog = true // 显示对话框
                                },
                                modifier = Modifier.size(72.dp).border(1.dp, Color.Gray, RoundedCornerShape(8.dp))
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
                                        Button(
                                            onClick = {
                                                if (folderName.isNotBlank()) {
                                                    coroutineScope.launch {
                                                        try {
                                                            createFolder(
                                                                context = context,
                                                                folderName = folderName,
                                                                parentFolderId = currentFolderId, // 传递当前文件夹ID
                                                                onSuccess = {
                                                                    loadItems(currentFolderId)
                                                                    showDialog = false // 关闭对话框
                                                                    folderName = "" // 清空输入框
                                                                //    snackbarHostState.showSnackbar("Folder created successfully")
                                                                },
                                                                onError = {
                                                                    showDialog = false // 关闭对话框
                                                                    folderName = "" // 清空输入框
                                                                  //  snackbarHostState.showSnackbar("Error: $it")
                                                                }
                                                            )
                                                        }catch (e: Exception){
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
                                        Button(
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

@Composable
fun TrashScreen() {
    Text(
        "Trash Screen", modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    )
}

@Composable
fun LabelScreen(label: String) {
    Text(
        "$label Screen", modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    )
}
