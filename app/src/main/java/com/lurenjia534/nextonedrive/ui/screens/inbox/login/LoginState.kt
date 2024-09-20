package com.lurenjia534.nextonedrive.ui.screens.inbox.login

data class LoginState(
    val tenantId: String = "",
    val clientId: String = "",
    val clientSecret: String = "",
    val userId: String = "",
    val grantType: String = "client_credentials",
    val scope: String = "https://graph.microsoft.com/.default offline_access",
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isPasswordVisible: Boolean = false
)
