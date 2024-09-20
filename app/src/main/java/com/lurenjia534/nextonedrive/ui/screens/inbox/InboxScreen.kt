package com.lurenjia534.nextonedrive.ui.screens.inbox

// ui/screens/inbox/InboxScreen.kt
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.ui.unit.sp
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import com.lurenjia534.nextonedrive.ui.components.InputField
import androidx.compose.material3.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import com.lurenjia534.nextonedrive.ui.theme.NextOneDriveTheme
import kotlinx.coroutines.launch
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import com.lurenjia534.nextonedrive.OAuthToken.fetchAccessToken

@Composable
fun InboxScreen(navController: NavController) {
    // 将原来的 InboxScreen 的内容移动到这里
    // 例如登录表单等
    // 可能还需要移动辅助 Composable，如 InputField

    // 状态变量
    var tenantId by remember { mutableStateOf("") }
    var clientId by remember { mutableStateOf("") }
    var clientSecret by remember { mutableStateOf("") }
    var userId by remember { mutableStateOf("") }
    var grantType by remember { mutableStateOf("client_credentials") }
    var scope by remember { mutableStateOf("https://graph.microsoft.com/.default offline_access") }
    var isLoading by remember { mutableStateOf(false) }

    // 错误消息
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    // 密码可见性
    var isPasswordVisible by remember { mutableStateOf(false) }

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
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // 标题
                Text(
                    text = "Next OneDrive LOGIN",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )
                )

                // Tenant ID 输入
                InputField(
                    value = tenantId,
                    onValueChange = { tenantId = it },
                    label = "Tenant ID",
                    isError = tenantId.isBlank(),
                    errorMessage = if (tenantId.isBlank()) "Tenant ID 不能为空" else null
                )

                // Client ID 输入
                AnimatedVisibility(
                    visible = tenantId.isNotBlank(),
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    InputField(
                        value = clientId,
                        onValueChange = { clientId = it },
                        label = "Client ID",
                        isError = clientId.isBlank(),
                        errorMessage = if (clientId.isBlank()) "Client ID 不能为空" else null
                    )
                }

                // Client Secret 输入
                AnimatedVisibility(
                    visible = clientId.isNotBlank(),
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    InputField(
                        value = clientSecret,
                        onValueChange = { clientSecret = it },
                        label = "Client Secret",
                        isPassword = !isPasswordVisible,
                        trailingIcon = {
                            IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                                Icon(
                                    imageVector = if (isPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                    contentDescription = if (isPasswordVisible) "隐藏密码" else "显示密码"
                                )
                            }
                        },
                        isError = clientSecret.isBlank(),
                        errorMessage = if (clientSecret.isBlank()) "Client Secret 不能为空" else null
                    )
                }

                // User ID 输入
                AnimatedVisibility(
                    visible = clientSecret.isNotBlank(),
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    InputField(
                        value = userId,
                        onValueChange = { userId = it },
                        label = "User ID",
                        isError = userId.isBlank(),
                        errorMessage = if (userId.isBlank()) "User ID 不能为空" else null
                    )
                }

                // Grant Type 输入
                AnimatedVisibility(
                    visible = userId.isNotBlank(),
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    InputField(
                        value = grantType,
                        onValueChange = { grantType = it },
                        label = "Grant Type",
                        isError = grantType.isBlank(),
                        errorMessage = if (grantType.isBlank()) "Grant Type 不能为空" else null
                    )
                }

                // Scope 输入
                AnimatedVisibility(
                    visible = tenantId.isNotBlank() && clientId.isNotBlank() && clientSecret.isNotBlank() && userId.isNotBlank() && grantType.isNotBlank(),
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    InputField(
                        value = scope,
                        onValueChange = { scope = it },
                        label = "Scope",
                        isError = scope.isBlank(),
                        errorMessage = if (scope.isBlank()) "Scope 不能为空" else null
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // 登录按钮
                AnimatedVisibility(
                    visible = tenantId.isNotBlank() &&
                            clientId.isNotBlank() &&
                            clientSecret.isNotBlank() &&
                            userId.isNotBlank() &&
                            grantType.isNotBlank() &&
                            scope.isNotBlank(),
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
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
                                        snackbarHostState.showSnackbar("错误: $message")
                                    }
                                    isLoading = false
                                },
                                onSuccess = { token ->
                                    coroutineScope.launch {
                                        snackbarHostState.showSnackbar("登录成功")
                                    }
                                    isLoading = false
                                    navController.navigate("outbox")
                                }
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        enabled = !isLoading
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                color = MaterialTheme.colorScheme.onPrimary,
                                modifier = Modifier.size(24.dp)
                            )
                        } else {
                            Text(text = "登录", color = MaterialTheme.colorScheme.onPrimary)
                        }
                    }
                }
            }

            // 全屏加载指示器
            if (isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.5f)),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                }
            }
        }
    }
}
