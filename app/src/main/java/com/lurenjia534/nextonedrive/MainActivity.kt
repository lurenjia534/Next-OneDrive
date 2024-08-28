package com.lurenjia534.nextonedrive

import DriveInfoResponse
import android.graphics.drawable.Icon
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Send
import androidx.compose.material.icons.filled.ArrowBack

import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.ArrowDropDown
import androidx.compose.material.icons.outlined.Build
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.DateRange
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Face
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Send
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavHost
import androidx.navigation.compose.rememberNavController
import com.lurenjia534.nextonedrive.ui.theme.NextOneDriveTheme
import kotlinx.coroutines.launch
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.lurenjia534.nextonedrive.OAuthToken.fetchAccessToken
import com.lurenjia534.nextonedrive.Profilepage.fetchDriveInfo
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
                composable("favorites") { FavoritesScreen() }
                composable("trash") { TrashScreen() }
                composable(
                    route = "label/{labelId}",
                    arguments = listOf(navArgument("labelId") { type = NavType.StringType })
                ) { backStackEntry ->
                    val labelId = backStackEntry.arguments?.getString("labelId") ?: "Unknown"
                    LabelScreen(label = labelId)
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
    // ErrorSnackbar state
    val snackbarHostState = remember { SnackbarHostState() }
    var showErrorSnackbar by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }
    // onSurface Snackbar Status
    var showSuccessSnackbar by remember { mutableStateOf(false) }
    var successMessage by remember { mutableStateOf("") }
    // Main layout
    Scaffold(
        snackbarHost = {
            SnackbarHost(snackbarHostState) { data ->
                Snackbar(
                    snackbarData = data,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                    containerColor = if (showSuccessSnackbar) Color.Black else Color.Black,  // 判断是否为成功 Snackbar 来设置背景颜色
                    shape = MaterialTheme.shapes.extraSmall
                )
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .padding(paddingValues),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Title
            Text(
                text = "Next OneDrive LOGIN",
                style = MaterialTheme.typography.headlineMedium.copy(fontSize = 24.sp),
                modifier = Modifier.padding(bottom = 32.dp)
            )
            // Tenant ID TextField
            OutlinedTextField(
                value = tenantId,
                onValueChange = { tenantId = it },
                label = { Text("Tenant ID") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            )
            // Client ID TextField
            OutlinedTextField(
                value = clientId,
                onValueChange = { clientId = it },
                label = { Text("Client ID") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            )
            // Client Secret TextField
            OutlinedTextField(
                value = clientSecret,
                onValueChange = { clientSecret = it },
                label = { Text("Client Secret") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            )
            // User ID TextField
            OutlinedTextField(
                value = userId,
                onValueChange = { userId = it },
                label = { Text("User ID") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            )
            // Grant Type TextField
            OutlinedTextField(
                value = grantType,
                onValueChange = { grantType = it },
                label = { Text("Grant Type") },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 32.dp)
            )
            // Scope TextField
            OutlinedTextField(
                value = scope,
                onValueChange = { scope = it },
                label = { Text("Scope") },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 32.dp)
            )
            val context = LocalContext.current
            // Login Button
            OutlinedButton(
                onClick = {
                    // 这里你可以保存输入内容，或者将它们用于后续的 OAuth2 请求
                    fetchAccessToken(
                        context = context,
                        tenantId = tenantId,
                        clientId = clientId,
                        clientSecret = clientSecret,
                        grantType = grantType,
                        scope = scope,
                        userId = userId,
                        onError = { message ->
                            errorMessage = message
                            showErrorSnackbar = true // 重置状态
                        },
                        onSuccess = { token ->
                            // 成功后的操作
                            successMessage = "Login Success"
                            println("Token $token")
                            showSuccessSnackbar = true // 重置状态
                            navController.navigate("outbox")
                        }
                    )
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color.Black),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
            ) {
                Text("Login", color = Color.White)
            }
        }
        // Show error snackbar
        if (showErrorSnackbar) {
            LaunchedEffect(snackbarHostState) {
                snackbarHostState.showSnackbar(errorMessage)
                showErrorSnackbar = false // 重置状态
            }
        }
        // Show success snackbar
        if (showSuccessSnackbar) {
            LaunchedEffect(snackbarHostState) {
                snackbarHostState.showSnackbar(successMessage)
                showSuccessSnackbar = false // 重置状态
                print("Token: $successMessage")
            }
        }
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
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
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


@Composable
fun FavoritesScreen() {
    Text(
        "Favorites Screen", modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
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
