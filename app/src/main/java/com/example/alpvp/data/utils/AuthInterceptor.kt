package com.example.alpvp.data.utils

import android.content.Context
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(private val context: Context) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        
        // Get token from TokenManager
        val token = TokenManager.getToken(context)

        // If no token, proceed without auth header
        if (token == null) {
            return chain.proceed(originalRequest)
        }

        // Add Authorization header with Bearer token
        val authenticatedRequest = originalRequest.newBuilder()
            .header("Authorization", "Bearer $token")
            .build()

        return chain.proceed(authenticatedRequest)
    }
}
