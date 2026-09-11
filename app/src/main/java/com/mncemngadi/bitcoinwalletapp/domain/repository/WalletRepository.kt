package com.mncemngadi.bitcoinwalletapp.domain.repository

import com.mncemngadi.bitcoinwalletapp.domain.model.CurrencyRate
import com.mncemngadi.bitcoinwalletapp.domain.util.Either
import com.mncemngadi.bitcoinwalletapp.domain.util.Failure
import kotlinx.coroutines.flow.Flow

interface WalletRepository {
    fun getBtcAmount(): Flow<Double>

    suspend fun updateBtcAmount(amount: Double)

    suspend fun getLatestRates(
        base: String,
        symbols: List<String>,
    ): Either<Failure, List<CurrencyRate>>

    suspend fun getFluctuation(
        base: String,
        symbols: List<String>,
        startDate: String,
        endDate: String,
    ): Either<Failure, Map<String, Double>>
}
