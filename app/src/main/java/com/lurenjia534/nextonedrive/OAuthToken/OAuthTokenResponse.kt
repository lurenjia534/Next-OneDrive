package com.lurenjia534.nextonedrive.OAuthToken

data class OAuthTokenResponse(
    val token_type: String,
    val expires_in: Int,
    val ext_expires_in: Int,
    val access_token: String,
)