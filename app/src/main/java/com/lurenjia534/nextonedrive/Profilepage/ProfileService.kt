package com.lurenjia534.nextonedrive.Profilepage

import DriveInfoResponse
import android.content.Context
import android.content.SharedPreferences
import androidx.work.ListenableWorker.Result.Success
import retrofit2.Call
import retrofit2.Response

fun fetchDriveInfo(
    context: Context,
    onSuccess: (DriveInfoResponse) -> Unit,
    onError: (String) -> Unit,
){
    val sharedPreferences: SharedPreferences = context.getSharedPreferences("OAuthPrefs", Context.MODE_PRIVATE)
    val userId = sharedPreferences.getString("userId", null)
    val token = sharedPreferences.getString("token", null)

    if (userId != null && token != null){
        val apiService = ProfileRetrofitClient.instance
        val call = apiService.getDriveInfo(userId,"Bearer $token")

        call.enqueue(object:  retrofit2.Callback<DriveInfoResponse> {
            override fun onResponse(
                call: Call<DriveInfoResponse>,
                response: Response<DriveInfoResponse>,
            ){
                if (response.isSuccessful){
                    response.body()?.let { onSuccess(it) }
                }else {
                    onError("Error: ${response.errorBody()?.string() ?: "Unknown error"}")
                }
            }
            override fun onFailure(call: Call<DriveInfoResponse>,t: Throwable){
                onError("Network Error: ${t.message}")
        }
    })
    }else{
        onError("No credentials found")
    }
}