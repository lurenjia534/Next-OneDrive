package com.lurenjia534.nextonedrive.ui.screens.inbox.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lurenjia534.nextonedrive.OAuthToken.fetchAccessToken
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

sealed class LoginUiEvent {
    object NavigateToOutbox : LoginEvent()
    data class ShowError(val message: String) : LoginEvent()
}

class LoginViewModel : ViewModel() {
    private val _state = MutableStateFlow(LoginState())
    val state: StateFlow<LoginState> = _state.asStateFlow()

    private val _uiEvent = Channel<LoginEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()

    fun onEvent(event: LoginEvent) {
        when(event) {
            is LoginEvent.OnTenantIdChange -> {
                _state.value = _state.value.copy(tenantId = event.tenantId)
            }
            is LoginEvent.OnClientIdChange -> {
                _state.value = _state.value.copy(clientId = event.clientId)
            }
            is LoginEvent.OnClientSecretChange -> {
                _state.value = _state.value.copy(clientSecret = event.clientSecret)
            }
            is LoginEvent.OnUserIdChange -> {
                _state.value = _state.value.copy(userId = event.userId)
            }
            is LoginEvent.OnGrantTypeChange -> {
                _state.value = _state.value.copy(grantType = event.grantType)
            }
            is LoginEvent.OnScopeChange -> {
                _state.value = _state.value.copy(scope = event.scope)
            }
            is LoginEvent.TogglePasswordVisibility -> {
                _state.value = _state.value.copy(isPasswordVisible = !_state.value.isPasswordVisible)
            }
            is LoginEvent.Submit -> {
                submitLogin()
            }
            is LoginEvent.OnErrorHandled -> {
                _state.value = _state.value.copy(errorMessage = null)
            }

            LoginUiEvent.NavigateToOutbox -> TODO()
            is LoginUiEvent.ShowError -> TODO()
        }
    }
    private fun submitLogin() {
        val currentState = _state.value
        // 输入校验
        if (
            currentState.tenantId.isBlank() ||
            currentState.clientId.isBlank() ||
            currentState.clientSecret.isBlank() ||
            currentState.userId.isBlank() ||
            currentState.grantType.isBlank() ||
            currentState.scope.isBlank()
        ){
            viewModelScope.launch {
                _uiEvent.send(LoginUiEvent.ShowError("所有字段均为必填项"))
            }
            return
        }
        _state.value = currentState.copy(isLoading = true, errorMessage = null)
        // 发送登录请求
        viewModelScope.launch {
//            fetchAccessToken(
//                context = context,
//                tenantId = currentState.tenantId,
//                clientId = currentState.clientId,
//                clientSecret = currentState.clientSecret,
//                grantType = currentState.grantType,
//                scope = currentState.scope,
//                userId = currentState.userId,
//                onError = { message ->
//                    viewModelScope.launch {
//                        _state.value = currentState.copy(isLoading = false, errorMessage = message)
//                    }
//                }
//            )
            try {
                _state.value = _state.value.copy(isLoading = false)
                _uiEvent.send(LoginUiEvent.NavigateToOutbox)
            } catch (e: Exception) {
                _state.value = _state.value.copy(isLoading = false, errorMessage = e.message)
                _uiEvent.send(LoginUiEvent.ShowError(e.message ?: "未知错误"))
            }
        }
    }
}