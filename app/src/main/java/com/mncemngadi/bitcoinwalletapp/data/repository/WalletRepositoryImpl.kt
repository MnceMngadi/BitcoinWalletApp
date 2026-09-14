package com.mncemngadi.bitcoinwalletapp.data.repository

import com.mncemngadi.bitcoinwalletapp.data.local.WalletPreferences
import com.mncemngadi.bitcoinwalletapp.data.mapper.toDomain
import com.mncemngadi.bitcoinwalletapp.data.remote.BitcoinWalletApiService
import com.mncemngadi.bitcoinwalletapp.domain.model.CurrencyRate
import com.mncemngadi.bitcoinwalletapp.domain.repository.WalletRepository
import com.mncemngadi.bitcoinwalletapp.domain.util.Either
import com.mncemngadi.bitcoinwalletapp.domain.util.Failure
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of the WalletRepository.
 * Handles data fetching, caching, and error mapping.
 */
@Singleton
class WalletRepositoryImpl
    @Inject
    constructor(
        private val apiService: BitcoinWalletApiService,
        private val preferences: WalletPreferences,
    ) : WalletRepository {
        // In-memory cache variables
        private var cachedRates: List<CurrencyRate>? = null
        private var lastFetchTime: Long = 0
        private val cacheTimeout = 10 * 60 * 1000 // 10 minutes

        /** Gets the observed BTC amount from local storage. */
        override fun getBtcAmount(): Flow<Double> = preferences.btcAmount

        /** Saves the updated BTC amount to local storage. */
        override suspend fun updateBtcAmount(amount: Double) {
            preferences.saveBtcAmount(amount)
        }

        /** Fetches current rates with a 10-minute in-memory cache. */
        override suspend fun getLatestRates(
            base: String,
            symbols: List<String>,
        ): Either<Failure, List<CurrencyRate>> {
            val currentTime = System.currentTimeMillis()
            // Return cached data if still valid
            if (cachedRates != null && (currentTime - lastFetchTime) < cacheTimeout) {
                return Either.Right(cachedRates!!)
            }

            return safeApiCall {
                val response =
                    apiService.getLatestRates(
                        base = base,
                        symbols = symbols.joinToString(","),
                    )
                if (response.success && response.rates != null) {
                    val allRates = response.toDomain()
                    val domainRates = allRates.filter { it.code in symbols }
                    // Update cache
                    cachedRates = domainRates
                    lastFetchTime = currentTime
                    Either.Right(domainRates)
                } else {
                    val apiMessage = response.error?.info ?: response.error?.type ?: "Server Error"
                    Either.Left(Failure.ServerError(apiMessage))
                }
            }
        }

        /** Fetches historical exchange rates for a specific date. */
        override suspend fun getHistoricalRates(
            date: String,
            base: String,
            symbols: List<String>,
        ): Either<Failure, List<CurrencyRate>> {
            return safeApiCall {
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
            }
        }

        /** Fetches fluctuation data from the API. */
        override suspend fun getFluctuation(
            base: String,
            symbols: List<String>,
            startDate: String,
            endDate: String,
        ): Either<Failure, Map<String, Double>> {
            return safeApiCall {
                val response = apiService.getFluctuation(base, symbols.joinToString(","), startDate, endDate)
                if (response.success && response.rates != null) {
                    val changes = response.rates.mapValues { it.value.change_pct }
                    Either.Right(changes)
                } else {
                    val apiMessage = response.error?.info ?: response.error?.type ?: "Server Error"
                    Either.Left(Failure.ServerError(apiMessage))
                }
            }
        }

        /**
         * Generic helper to wrap API calls with error handling for HTTP, Network, and unknown issues.
         */
        private suspend fun <T> safeApiCall(call: suspend () -> Either<Failure, T>): Either<Failure, T> =
            withContext(Dispatchers.IO) {
                try {
                    call()
                } catch (e: HttpException) {
                    if (e.code() == 429) {
                        Either.Left(Failure.ServerError("API Rate limit exceeded."))
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
