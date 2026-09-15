package com.mncemngadi.bitcoinwalletapp.domain.repository

import com.mncemngadi.bitcoinwalletapp.domain.model.CurrencyRate
import com.mncemngadi.bitcoinwalletapp.domain.util.Either
import com.mncemngadi.bitcoinwalletapp.domain.util.Failure
import kotlinx.coroutines.flow.Flow

/**
 * Interface for wallet data operations.
 */
interface WalletRepository {
    /** Gets the saved BTC amount. */
    fun getBtcAmount(): Flow<Double>

    /** Saves a new BTC amount. */
    suspend fun updateBtcAmount(amount: Double)

    /** Fetches latest exchange rates. */
    suspend fun getLatestRates(
        base: String,
        symbols: List<String>,
    ): Either<Failure, List<CurrencyRate>>

    /** Fetches rates for a specific date. */
    suspend fun getHistoricalRates(
        date: String,
        base: String,
        symbols: List<String>,
    ): Either<Failure, List<CurrencyRate>>

    /** Fetches price fluctuation between two dates. */
    suspend fun getFluctuation(
        base: String,
        symbols: List<String>,
        startDate: String,
        endDate: String,
    ): Either<Failure, Map<String, Double>>
}
