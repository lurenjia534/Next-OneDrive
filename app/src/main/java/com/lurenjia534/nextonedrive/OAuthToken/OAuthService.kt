package com.lurenjia534.nextonedrive.OAuthToken

import android.content.Context
import android.content.SharedPreferences
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.LaunchedEffect
import androidx.work.ListenableWorker.Result.Success
import retrofit2.Call
import retrofit2.Response

//OAuthService.kt
fun fetchAccessToken(
    context: Context,
    tenantId: String,
    clientId: String,
    clientSecret: String,
    grantType: String,
    scope: String,
    userId: String,
    onError: (String) -> Unit,
    onSuccess: (String) -> Unit, // 接收token
) {
    val apiService = RetrofitClient.instance
    val call = apiService.getAccessToken(
        tenantId = tenantId,
        clientId = clientId,
        scope = scope,
        clientSecret = clientSecret,
        grantType = grantType,
    )

    call.enqueue(object : retrofit2.Callback<OAuthTokenResponse> {
        override fun onResponse(
            call: Call<OAuthTokenResponse>,
            resopnse: Response<OAuthTokenResponse>,
        ){
            if (resopnse.isSuccessful){
                val tokenResponse = resopnse.body()
                tokenResponse?.let {
                    saveCredentials(
                        context = context,
                        tenantId = tenantId,
                        clientId = clientId,
                        clientSecret = clientSecret,
                        userId = userId,
                        grantType = grantType,
                        scope = scope,
                        token = it.access_token,
                    )
                    onSuccess(it.access_token)
                }
            }else {
                // 处理错误
                val response = resopnse
                val errorBody = resopnse.errorBody()?.string()
                println("Raw Error Body: $errorBody")  // 打印原始错误体
                if (!errorBody.isNullOrEmpty()){
                    onError("Error: $errorBody")
                }else{
                    onError("Unknown error occurred, response code: ${response.code()}")
                }
            }
        }

        override fun onFailure(call: Call<OAuthTokenResponse>, t: Throwable) {
            // 处理错误
            onError("Network Error: ${t.message}")
        }
    })
}
// Save the credentials to SharedPreferences
fun saveCredentials(
    context: Context,
    tenantId: String,
    clientId: String,
    clientSecret: String,
    userId:String,
    grantType: String,
    scope: String,
    token:String
){
    val sharedPreferences:SharedPreferences = context.getSharedPreferences("OAuthPrefs", Context.MODE_PRIVATE)
    val editor = sharedPreferences.edit()
    editor.putString("tenantId", tenantId)
    editor.putString("clientId", clientId)
    editor.putString("clientSecret", clientSecret)
    editor.putString("userId", userId)
    editor.putString("grantType", grantType)
    editor.putString("scope", scope)
    editor.putString("token", token)
    editor.apply()
}