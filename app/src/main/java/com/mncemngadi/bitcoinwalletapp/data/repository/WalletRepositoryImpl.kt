package com.mncemngadi.bitcoinwalletapp.data.repository

import com.mncemngadi.bitcoinwalletapp.data.local.WalletPreferences
import com.mncemngadi.bitcoinwalletapp.data.mapper.toDomain
import com.mncemngadi.bitcoinwalletapp.data.remote.BitcoinWalletApiService
import com.mncemngadi.bitcoinwalletapp.domain.model.CurrencyRate
import com.mncemngadi.bitcoinwalletapp.domain.repository.WalletRepository
import com.mncemngadi.bitcoinwalletapp.domain.util.Either
import com.mncemngadi.bitcoinwalletapp.domain.util.Failure
import kotlinx.coroutines.flow.Flow
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WalletRepositoryImpl
    @Inject
    constructor(
        private val apiService: BitcoinWalletApiService,
        private val preferences: WalletPreferences,
    ) : WalletRepository {
        // Simple in-memory cache
        private var cachedRates: List<CurrencyRate>? = null
        private var lastFetchTime: Long = 0
        private val cacheTimeout = 10 * 60 * 1000 // 10 minutes

        override fun getBtcAmount(): Flow<Double> = preferences.btcAmount

        override suspend fun updateBtcAmount(amount: Double) {
            preferences.saveBtcAmount(amount)
        }

        override suspend fun getLatestRates(
            base: String,
            symbols: List<String>,
        ): Either<Failure, List<CurrencyRate>> {
            val currentTime = System.currentTimeMillis()
            if (cachedRates != null && (currentTime - lastFetchTime) < cacheTimeout) {
                return Either.Right(cachedRates!!)
            }

            return try {
                val response =
                    apiService.getLatestRates(
                        base = base,
                        symbols = symbols.joinToString(","),
                    )
                if (response.success && response.rates != null) {
                    val allRates = response.toDomain()
                    val domainRates = allRates.filter { it.code in symbols }
                    cachedRates = domainRates
                    lastFetchTime = currentTime
                    Either.Right(domainRates)
                } else {
                    val apiMessage = response.error?.info ?: response.error?.type ?: "Server Error"
                    Either.Left(Failure.ServerError(apiMessage))
                }
            } catch (e: HttpException) {
                if (e.code() == 429) {
                    Either.Left(Failure.ServerError("API Rate limit exceeded. Please try again later or update your API access key."))
                } else {
                    val errorBody = e.response()?.errorBody()?.string() ?: ""
                    val errorDetail =
                        if (errorBody.contains("info")) {
                            // Extract basic info sentence if present in the json
                            errorBody.substringAfter("\"info\":\"").substringBefore("\"")
                        } else if (errorBody.contains("message")) {
                            errorBody.substringAfter("\"message\":\"").substringBefore("\"")
                        } else {
                            e.message() ?: "HTTP ${e.code()}"
                        }
                    Either.Left(Failure.ServerError("Server Error (${e.code()}): $errorDetail"))
                }
            } catch (e: IOException) {
                Either.Left(Failure.NetworkConnection)
            } catch (e: Exception) {
                Either.Left(Failure.UnknownError(e.message))
            }
        }

        override suspend fun getHistoricalRates(
            date: String,
            base: String,
            symbols: List<String>,
        ): Either<Failure, List<CurrencyRate>> {
            return try {
                val response =
                    apiService.getHistoricalRates(
                        date = date,
                        base = base,
                        symbols = symbols.joinToString(","),
                    )
                if (response.success && response.rates != null) {
                    val allRates = response.toDomain()
                    val domainRates = allRates.filter { it.code in symbols }
                    Either.Right(domainRates)
                } else {
                    val apiMessage = response.error?.info ?: response.error?.type ?: "Server Error"
                    Either.Left(Failure.ServerError(apiMessage))
                }
            } catch (e: HttpException) {
                val errorBody = e.response()?.errorBody()?.string() ?: ""
                val errorDetail =
                    if (errorBody.contains("info")) {
                        errorBody.substringAfter("\"info\":\"").substringBefore("\"")
                    } else if (errorBody.contains("message")) {
                        errorBody.substringAfter("\"message\":\"").substringBefore("\"")
                    } else {
                        e.message() ?: "HTTP ${e.code()}"
                    }
                Either.Left(Failure.ServerError("Server Error (${e.code()}): $errorDetail"))
            } catch (e: IOException) {
                Either.Left(Failure.NetworkConnection)
            } catch (e: Exception) {
                Either.Left(Failure.UnknownError(e.message))
            }
        }

        override suspend fun getFluctuation(
            base: String,
            symbols: List<String>,
            startDate: String,
            endDate: String,
        ): Either<Failure, Map<String, Double>> {
            return try {
                val response = apiService.getFluctuation(base, symbols.joinToString(","), startDate, endDate)
                if (response.success && response.rates != null) {
                    val changes = response.rates.mapValues { it.value.change_pct }
                    Either.Right(changes)
                } else {
                    val apiMessage = response.error?.info ?: response.error?.type ?: "Server Error"
                    Either.Left(Failure.ServerError(apiMessage))
                }
            } catch (e: HttpException) {
                if (e.code() == 429) {
                    Either.Left(Failure.ServerError("API Rate limit exceeded. Please try again later or update your API access key."))
                } else {
                    val errorBody = e.response()?.errorBody()?.string() ?: ""
                    val errorDetail =
                        if (errorBody.contains("info")) {
                            errorBody.substringAfter("\"info\":\"").substringBefore("\"")
                        } else if (errorBody.contains("message")) {
                            errorBody.substringAfter("\"message\":\"").substringBefore("\"")
                        } else {
                            e.message() ?: "HTTP ${e.code()}"
                        }
                    Either.Left(Failure.ServerError("Server Error (${e.code()}): $errorDetail"))
                }
            } catch (e: IOException) {
                Either.Left(Failure.NetworkConnection)
            } catch (e: Exception) {
                Either.Left(Failure.UnknownError(e.message))
            }
        }
    }
