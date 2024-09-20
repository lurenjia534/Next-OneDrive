// ui/screens/login/LoginScreen.kt
package com.lurenjia534.nextonedrive.ui.screens.login

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.lurenjia534.nextonedrive.ui.screens.inbox.login.InputField
import com.lurenjia534.nextonedrive.ui.screens.inbox.login.LoadingIndicator
import com.lurenjia534.nextonedrive.ui.screens.inbox.login.LoginEvent
import com.lurenjia534.nextonedrive.ui.screens.inbox.login.LoginUiEvent
import com.lurenjia534.nextonedrive.ui.screens.inbox.login.LoginViewModel
import com.lurenjia534.nextonedrive.ui.screens.inbox.login.PasswordInputField
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(
    navController: NavController,
    viewModel: LoginViewModel = viewModel()
) {
    val state by viewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    // 监听 UI 事件
    LaunchedEffect(key1 = true) {
        viewModel.uiEvent.collect { event ->
            when (event) {
                is LoginUiEvent.ShowError -> {
                    snackbarHostState.showSnackbar(event.message)
                    viewModel.onEvent(LoginEvent.OnErrorHandled)
                }
                is LoginUiEvent.NavigateToOutbox -> {
                    navController.navigate("outbox") {
                        popUpTo("login") { inclusive = true }
                    }
                }

                is LoginEvent.OnClientIdChange -> TODO()
                is LoginEvent.OnClientSecretChange -> TODO()
                LoginEvent.OnErrorHandled -> TODO()
                is LoginEvent.OnGrantTypeChange -> TODO()
                is LoginEvent.OnScopeChange -> TODO()
                is LoginEvent.OnTenantIdChange -> TODO()
                is LoginEvent.OnUserIdChange -> TODO()
                LoginEvent.Submit -> TODO()
                LoginEvent.TogglePasswordVisibility -> TODO()
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.Center),
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
                    value = state.tenantId,
                    onValueChange = { viewModel.onEvent(LoginEvent.OnTenantIdChange(it)) },
                    label = "Tenant ID",
                    isError = state.tenantId.isBlank(),
                    errorMessage = if (state.tenantId.isBlank()) "Tenant ID 不能为空" else null
                )

                // Client ID 输入
                AnimatedVisibility(
                    visible = state.tenantId.isNotBlank(),
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    InputField(
                        value = state.clientId,
                        onValueChange = { viewModel.onEvent(LoginEvent.OnClientIdChange(it)) },
                        label = "Client ID",
                        isError = state.clientId.isBlank(),
                        errorMessage = if (state.clientId.isBlank()) "Client ID 不能为空" else null
                    )
                }

                // Client Secret 输入
                AnimatedVisibility(
                    visible = state.clientId.isNotBlank(),
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    PasswordInputField(
                        value = state.clientSecret,
                        onValueChange = { viewModel.onEvent(LoginEvent.OnClientSecretChange(it)) },
                        label = "Client Secret",
                        isError = state.clientSecret.isBlank(),
                        errorMessage = if (state.clientSecret.isBlank()) "Client Secret 不能为空" else null
                    )
                }

                // User ID 输入
                AnimatedVisibility(
                    visible = state.clientSecret.isNotBlank(),
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    InputField(
                        value = state.userId,
                        onValueChange = { viewModel.onEvent(LoginEvent.OnUserIdChange(it)) },
                        label = "User ID",
                        isError = state.userId.isBlank(),
                        errorMessage = if (state.userId.isBlank()) "User ID 不能为空" else null
                    )
                }

                // Grant Type 输入
                AnimatedVisibility(
                    visible = state.userId.isNotBlank(),
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    InputField(
                        value = state.grantType,
                        onValueChange = { viewModel.onEvent(LoginEvent.OnGrantTypeChange(it)) },
                        label = "Grant Type",
                        isError = state.grantType.isBlank(),
                        errorMessage = if (state.grantType.isBlank()) "Grant Type 不能为空" else null
                    )
                }

                // Scope 输入
                AnimatedVisibility(
                    visible = state.tenantId.isNotBlank() &&
                            state.clientId.isNotBlank() &&
                            state.clientSecret.isNotBlank() &&
                            state.userId.isNotBlank() &&
                            state.grantType.isNotBlank(),
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    InputField(
                        value = state.scope,
                        onValueChange = { viewModel.onEvent(LoginEvent.OnScopeChange(it)) },
                        label = "Scope",
                        isError = state.scope.isBlank(),
                        errorMessage = if (state.scope.isBlank()) "Scope 不能为空" else null
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // 登录按钮
                AnimatedVisibility(
                    visible = state.tenantId.isNotBlank() &&
                            state.clientId.isNotBlank() &&
                            state.clientSecret.isNotBlank() &&
                            state.userId.isNotBlank() &&
                            state.grantType.isNotBlank() &&
                            state.scope.isNotBlank(),
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    Button(
                        onClick = { viewModel.onEvent(LoginEvent.Submit) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        enabled = !state.isLoading
                    ) {
                        if (state.isLoading) {
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
            if (state.isLoading) {
                LoadingIndicator()
            }
        }
    }
}
