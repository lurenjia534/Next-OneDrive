package com.lurenjia534.nextonedrive.Filefunction

import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import android.provider.OpenableColumns
import com.lurenjia534.nextonedrive.ListItem.DriveItem
import java.io.File
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.HttpException
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

        try {
            // 处理父文件夹存在和不存在的情况
            val driveItem = if (parentFolderId != null) {
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

            // 由于createFolderInSubFolder 和 createFolder 是suspend 函数，直接返回driveItem
            onSuccess(driveItem)

        } catch (e: Exception) {
            onError("Error: ${e.message}")
        }
    } else {
        onError("No credentials found")
    }
}



// 上传文件
suspend fun uploadFile(
    context: Context,
    uri: Uri,
    parentId: String,
    onSuccess: (DriveItem) -> Unit,
    onError: (String) -> Unit
) {
    val sharedPreferences: SharedPreferences = context.getSharedPreferences("OAuthPrefs", Context.MODE_PRIVATE)
    val userId = sharedPreferences.getString("userId", null)
    val token = sharedPreferences.getString("token", null)

    if (userId != null && token != null) {
        val contentResolver = context.contentResolver
        val inputStream = contentResolver.openInputStream(uri)
        val fileBytes = inputStream?.readBytes() ?: byteArrayOf()

        // 设置媒体类型
        val mediaType = "application/octet-stream".toMediaTypeOrNull()
        val requestBody = fileBytes.toRequestBody(mediaType)

        val apiService = FileRetrofitClient.instance
        try {
            val driveItem = apiService.uploadFile(userId, parentId, getFileName(context, uri), "Bearer $token", requestBody)
            onSuccess(driveItem)
        } catch (e: Exception) {
            onError(e.message ?: "Unknown error")
        }
    } else {
        onError("No credentials found")
    }
}

private fun getFileName(context: Context, uri: Uri): String {
    var fileName: String? = null
    val cursor = context.contentResolver.query(uri, null, null, null, null)

    cursor?.use {
        if (it.moveToFirst()) {
            val nameIndex = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            if (nameIndex != -1) {
                fileName = it.getString(nameIndex)
            }
        }
    }

    return fileName ?: "uploaded_image" // 如果无法获取文件名，返回默认名称
}

// 删除文件
suspend fun deleteDriveItem(
    context: Context,
    itemId: String,
    onSuccess: () -> Unit,
    onError: (String) -> Unit
){
    val sharedPreferences: SharedPreferences = context.getSharedPreferences("OAuthPrefs", Context.MODE_PRIVATE)
    val userId = sharedPreferences.getString("userId", null)
    val token = sharedPreferences.getString("token", null)

    if (userId != null && token != null) {
        val apiService = FileRetrofitClient.instance
        try {
            val response = apiService.deleteDriveItem(
                userId = userId,
                itemId = itemId,
                authorization = "Bearer $token"
            )
            if (response.isSuccessful){
                onSuccess()
            }else{
                onError("Error: ${response.code()}")
            }
        }catch (e: Exception){
            onError("Error: ${e.message}")
        }
    }else{
        onError("No credentials found")
    }
}

// 创建共享链接
suspend fun createShareableLink(
    context: Context,
    itemId: String,
    linkType: String, // "view", "edit", or "embed"
    scope : String? = null, // "anonymous" or "organization"
    onSuccess: (String) -> Unit,
    onError: (String) -> Unit
) {
    val sharedPreferences: SharedPreferences = context.getSharedPreferences("OAuthPrefs", Context.MODE_PRIVATE)
    val userId = sharedPreferences.getString("userId", null)
    val token = sharedPreferences.getString("token", null)
    if (userId != null && token != null) {
        val apiService = FileRetrofitClient.instance
        val createLinkRequest = CreateLinkRequest(type = linkType, scope = scope)

        try {
            val response = apiService.createShareableLink(
                userId = userId,
                itemId = itemId,
                authorization = "Bearer $token",
                requestBody = createLinkRequest
            )
            onSuccess(response.link.webUrl) // 返回共享链接
        }catch (e: HttpException) {
            onError("Http Error: ${e.message}")
        }catch (e: Exception) {
            onError("Error: ${e.message}")
        }
    }else
        onError("No credentials found")
}
