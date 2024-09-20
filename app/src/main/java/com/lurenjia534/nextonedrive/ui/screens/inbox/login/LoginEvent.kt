package com.lurenjia534.nextonedrive.ui.screens.inbox.login

sealed class LoginEvent {
 data class OnTenantIdChange(val tenantId: String) : LoginEvent()
 data class OnClientIdChange(val clientId: String) : LoginEvent()
 data class OnClientSecretChange(val clientSecret: String) : LoginEvent()
 data class OnUserIdChange(val userId: String) : LoginEvent()
 data class OnGrantTypeChange(val grantType: String) : LoginEvent()
 data class OnScopeChange(val scope: String) : LoginEvent()
 object TogglePasswordVisibility : LoginEvent()
 object Submit : LoginEvent()
 object OnErrorHandled : LoginEvent()
}