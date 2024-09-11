package com.lurenjia534.nextonedrive.Filefunction

import com.lurenjia534.nextonedrive.ListItem.DriveItem
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path

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
}