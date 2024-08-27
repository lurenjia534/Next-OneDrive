package com.lurenjia534.nextonedrive.TokenRefresh

import android.content.Context
import android.content.SharedPreferences
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.Worker
import androidx.work.WorkerParameters
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.lurenjia534.nextonedrive.OAuthToken.fetchAccessToken
import java.util.concurrent.TimeUnit

class TokenRefreshWorker(
    context: Context,
    workerParams: WorkerParameters
): Worker(context, workerParams){
    override fun doWork(): Result {
        val sharedPreferences: SharedPreferences = applicationContext.getSharedPreferences("OAuthPrefs",Context.MODE_PRIVATE)
        val tenantId = sharedPreferences.getString("tenantId",null)
        val clientId = sharedPreferences.getString("clientId",null)
        val clientSecret = sharedPreferences.getString("clientSecret",null)
        val userId = sharedPreferences.getString("userId",null)
        val grantType = sharedPreferences.getString("grantType",null)
        val scope = sharedPreferences.getString("scope",null)

        if (
            tenantId != null &&
            clientId != null &&
            clientSecret != null &&
            userId != null &&
            grantType != null &&
            scope != null
        ){
            fetchAccessToken(
                context = applicationContext,
                tenantId = tenantId,
                clientId = clientId,
                clientSecret = clientSecret,
                grantType = grantType,
                scope = scope,
                userId = userId,
                onError = { errorMessage ->
                    println("Error: $errorMessage")
                },
                onSuccess = {
                    scheduleTokenRefresh(context = applicationContext)
                }
            )
        }
        return Result.success()
    }
}

// 在应用启动时或其他适当时机调用此函数，以确保令牌在过期之前刷新
fun scheduleTokenRefresh(context: Context) {
    val tokenRefreshRequest = PeriodicWorkRequestBuilder<TokenRefreshWorker>(
        repeatInterval = 1,
        repeatIntervalTimeUnit = TimeUnit.HOURS)
        .build()

    WorkManager.getInstance(context).enqueueUniquePeriodicWork(
        "TokenRefreshWork",
        ExistingPeriodicWorkPolicy.UPDATE,
        tokenRefreshRequest
    )
}