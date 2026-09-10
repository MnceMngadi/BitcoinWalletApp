package com.mncemngadi.bitcoinwalletapp.data.repository

import com.mncemngadi.bitcoinwalletapp.data.local.WalletPreferences
import com.mncemngadi.bitcoinwalletapp.data.mapper.toDomain
import com.mncemngadi.bitcoinwalletapp.data.remote.BitcoinWalletApiService
import com.mncemngadi.bitcoinwalletapp.domain.model.CurrencyRate
import com.mncemngadi.bitcoinwalletapp.domain.repository.WalletRepository
import com.mncemngadi.bitcoinwalletapp.domain.util.Either
import com.mncemngadi.bitcoinwalletapp.domain.util.Failure
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WalletRepositoryImpl @Inject constructor(
    private val apiService: BitcoinWalletApiService,
    private val preferences: WalletPreferences
) : WalletRepository {

    // Simple in-memory cache
    private var cachedRates: List<CurrencyRate>? = null
    private var lastFetchTime: Long = 0
    private val cacheTimeout = 10 * 60 * 1000 // 10 minutes

    override fun getBtcAmount(): Flow<Double> = preferences.btcAmount

    override suspend fun updateBtcAmount(amount: Double) {
        preferences.saveBtcAmount(amount)
    }

    override suspend fun getLatestRates(base: String, symbols: List<String>): Either<Failure, List<CurrencyRate>> {
        val currentTime = System.currentTimeMillis()
        if (cachedRates != null && (currentTime - lastFetchTime) < cacheTimeout) {
            return Either.Right(cachedRates!!)
        }

        return try {
            val response = apiService.getLatestRates()
            if (response.success && response.rates != null) {
                val allRates = response.toDomain()
                val domainRates = allRates.filter { it.code in symbols }
                cachedRates = domainRates
                lastFetchTime = currentTime
                Either.Right(domainRates)
            } else {
                Either.Left(Failure.ServerError)
            }
        } catch (e: Exception) {
            Either.Left(Failure.UnknownError(e.message))
        }
    }

    override suspend fun getFluctuation(
        base: String,
        symbols: List<String>,
        startDate: String,
        endDate: String
    ): Either<Failure, Map<String, Double>> {
        return try {
            val response = apiService.getFluctuation(base, symbols.joinToString(","), startDate, endDate)
            if (response.success && response.rates != null) {
                val changes = response.rates.mapValues { it.value.change_pct }
                Either.Right(changes)
            } else {
                Either.Left(Failure.ServerError)
            }
        } catch (e: Exception) {
            Either.Left(Failure.UnknownError(e.message))
        }
    }
}
