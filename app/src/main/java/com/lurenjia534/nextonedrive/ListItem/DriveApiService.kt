package com.lurenjia534.nextonedrive.ListItem

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path

interface DriveApiService {
    @GET("users/{userId}/drive/root/children")
    fun getDriveItems(
        @Path("userId") userId: String,
        @Header("Authorization") authorization: String,
    ): Call<DriveItemResponse>
    @GET("users/{userId}/drive/items/{itemId}/children")
    fun getDriveItemChildren(
        @Path("userId") userId: String,
        @Path("itemId") itemId: String,
        @Header("Authorization") authorization: String
    ):Call<DriveItemResponse>
}