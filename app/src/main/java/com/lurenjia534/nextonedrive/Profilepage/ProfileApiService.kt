package com.lurenjia534.nextonedrive.Profilepage

import DriveInfoResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path

interface ProfileApiService {
    @GET("users/{userId}/drive")
    fun getDriveInfo(
        @Path("userId") userId: String,
        @Header("Authorization") authorization: String,
    ): Call<DriveInfoResponse>
}