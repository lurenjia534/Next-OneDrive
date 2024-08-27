package com.lurenjia534.nextonedrive.OAuthToken

import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST
import retrofit2.http.Path

interface OAuthApiService {
    @FormUrlEncoded
    @POST("{tenantId}/oauth2/v2.0/token")
    fun getAccessToken(
        @Path("tenantId") tenantId: String,
        @Field("client_id") clientId: String,
        @Field("scope") scope: String,
        @Field("client_secret") clientSecret: String,
        @Field("grant_type") grantType: String,
    ): Call<OAuthTokenResponse>
}