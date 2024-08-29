package com.lurenjia534.nextonedrive.ListItem

import android.content.Context
import android.content.SharedPreferences
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
fun fetchDriveItems(
    context: Context,
    onSuccess: (List<DriveItem>) -> Unit,
    onError: (String) -> Unit
) {
    val sharedPreferences: SharedPreferences = context.getSharedPreferences("OAuthPrefs", Context.MODE_PRIVATE)
    val userId = sharedPreferences.getString("userId", null)
    val token = sharedPreferences.getString("token", null)

    if (userId != null && token != null) {
        val apiService = DriveRetrofitClient.instance
        val call = apiService.getDriveItems(userId, "Bearer $token")

        call.enqueue(object : Callback<DriveItemResponse> {
            override fun onResponse(call: Call<DriveItemResponse>, response: Response<DriveItemResponse>) {
                if (response.isSuccessful) {
                    response.body()?.let { onSuccess(it.value) }
                } else {
                    onError("Error: ${response.errorBody()?.string() ?: "Unknown error"}")
                }
            }

            override fun onFailure(call: Call<DriveItemResponse>, t: Throwable) {
                onError("Network Error: ${t.message}")
            }
        })
    } else {
        onError("No credentials found")
    }
}
// 二级目录逻辑
fun fetchDriveItemChildren(
    context: Context,
    itemId: String,
    onSuccess: (List<DriveItem>) -> Unit,
    onError: (String) -> Unit
){
    val sharedPreferences: SharedPreferences = context.getSharedPreferences("OAuthPrefs", Context.MODE_PRIVATE)
    val userId = sharedPreferences.getString("userId", null)
    val token = sharedPreferences.getString("token", null)

    if (userId != null && token != null){
        val apiService = DriveRetrofitClient.instance
        val call = apiService.getDriveItemChildren(userId, itemId, "Bearer $token")

        call.enqueue(object : Callback<DriveItemResponse> {
            override fun onResponse(call: Call<DriveItemResponse>,response: Response<DriveItemResponse>){
                if (response.isSuccessful){
                    response.body()?.let { onSuccess(it.value) }
                }else{
                    onError("Error: ${response.errorBody()?.string() ?: "Unknown error"}")
                }
            }
            override fun onFailure(call: Call<DriveItemResponse>,t: Throwable){
                onError("Network Error: ${t.message}")
            }
        })
    } else {
        onError("No credentials found")
    }
}
