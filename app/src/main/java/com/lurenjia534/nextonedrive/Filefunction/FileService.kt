package com.lurenjia534.nextonedrive.Filefunction

import android.content.Context
import android.content.SharedPreferences
import com.lurenjia534.nextonedrive.ListItem.DriveItem

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

suspend fun createFolder(
    context: Context,
    folderName: String,
    onSuccess: (DriveItem) -> Unit,
    onError: (String) -> Unit,
    parentFolderId: String? = null
) {
    val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("OAuthPrefs", Context.MODE_PRIVATE)
    val userId = sharedPreferences.getString("userId", null)
    val token = sharedPreferences.getString("token", null)

    if (userId != null && token != null) {
        val apiService = FileRetrofitClient.instance
        val requestBody = CreateFolderRequest(
            name = folderName,
            folder = emptyMap()  // 这是一个空的 JSON 对象，表示要创建文件夹
        )

        val call = if (parentFolderId != null) {
            apiService.createFolderInSubFolder(
                userId = userId,
                parentItemId = parentFolderId,  // 使用父文件夹 ID
                authorization = "Bearer $token",
                requestBody = requestBody
            )
        } else {
            apiService.createFolder(
                userId = userId,
                authorization = "Bearer $token",
                requestBody = requestBody
            )
        }


        call.enqueue(object : Callback<DriveItem> {
            override fun onResponse(call: Call<DriveItem>, response: Response<DriveItem>) {
                if (response.isSuccessful) {
                    response.body()?.let { onSuccess(it) }
                } else {
                    onError("Error: ${response.errorBody()?.string() ?: "Unknown error"}")
                }
            }

            override fun onFailure(call: Call<DriveItem>, t: Throwable) {
                onError("Network Error: ${t.message}")
            }
        })
    }else{
        onError("No credentials found")
    }
}