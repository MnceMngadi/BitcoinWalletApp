package com.mncemngadi.bitcoinwalletapp.data.remote

import okhttp3.Interceptor
import okhttp3.Response

/**
 * Interceptor that adds the API key to every outgoing network request header.
 * This is important for security: it ensures authentication without exposing the key in URLs.
 */
class AuthInterceptor(private val apiKey: String) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val newRequest =
            originalRequest.newBuilder()
                .header("apikey", apiKey)
                .build()
        return chain.proceed(newRequest)
    }
}
