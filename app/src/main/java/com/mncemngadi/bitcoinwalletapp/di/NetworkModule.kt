package com.mncemngadi.bitcoinwalletapp.di

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import com.mncemngadi.bitcoinwalletapp.data.remote.AuthInterceptor
import com.mncemngadi.bitcoinwalletapp.data.remote.BitcoinWalletApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    private const val BASE_URL = "https://api.apilayer.com/fixer/"
    private const val API_KEY = "Ct2bFQpgzGya2jjzGFakyi90jWWJh7Kh"

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(AuthInterceptor(API_KEY))
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            })
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        val json = Json {
            ignoreUnknownKeys = true
            coerceInputValues = true
        }
        val contentType = "application/json".toMediaType()
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(json.asConverterFactory(contentType))
            .build()
    }

    @Provides
    @Singleton
    fun provideBitcoinWalletApiService(retrofit: Retrofit): BitcoinWalletApiService {
        return retrofit.create(BitcoinWalletApiService::class.java)
    }
}
