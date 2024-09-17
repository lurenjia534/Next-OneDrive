package com.lurenjia534.nextonedrive.Filefunction

import com.lurenjia534.nextonedrive.ListItem.DriveItem
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
// FileApiService.kt
interface FileApiService {
    @POST("users/{userId}/drive/root/children")
    suspend fun createFolder(
        @Header("Authorization") authorization: String,
        @Path("userId") userId: String,
        @Body requestBody : CreateFolderRequest
    ): Call<DriveItem>
    @POST("users/{userId}/drive/items/{parentItemId}/children")
    suspend fun createFolderInSubFolder(
        @Path("userId") userId: String,
        @Path("parentItemId") parentItemId: String,
        @Header("Authorization") authorization: String,
        @Body requestBody: CreateFolderRequest
    ): Call<DriveItem>
    // 上传文件（新文件）
    @PUT("users/{userId}/drive/items/{parentId}:/{filename}:/content")
    suspend fun uploadFile(
        @Path("userId") userId: String,
        @Path("parentId") parentId: String,
        @Path("filename") filename: String,
        @Header("Authorization") authorization: String,
        @Body fileContent: RequestBody
    ): DriveItem

    // 替换现有文件
    @PUT("users/{userId}/drive/items/{itemId}/content")
    suspend fun updateFileContent(
        @Path("userId") userId: String,
        @Path("itemId") itemId: String,
        @Header("Authorization") authorization: String,
        @Body fileContent: RequestBody
    ): Call<DriveItem>
}