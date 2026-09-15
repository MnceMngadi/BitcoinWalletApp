package com.mncemngadi.bitcoinwalletapp.domain.usecase

import com.mncemngadi.bitcoinwalletapp.domain.model.WalletData
import com.mncemngadi.bitcoinwalletapp.domain.repository.WalletRepository
import com.mncemngadi.bitcoinwalletapp.domain.util.Either
import com.mncemngadi.bitcoinwalletapp.domain.util.Failure
import com.mncemngadi.bitcoinwalletapp.domain.util.map
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import javax.inject.Inject

/**
 * Use case to fetch and calculate wallet data.
 */
class GetWalletDataUseCase
    @Inject
    constructor(
        private val repository: WalletRepository,
    ) {
        operator fun invoke(): Flow<Either<Failure, WalletData>> {
            // Get saved BTC amount
            val btcAmountFlow = repository.getBtcAmount()

            // Symbols to fetch from API
            val symbols = listOf("BTC", "ZAR", "USD", "AUD")
            val apiBase = "BTC"

            // Fetch rates and fluctuation from API
            val ratesFlow =
                flow {
                    val result = repository.getLatestRates(apiBase, symbols)

                    // Get dates for fluctuation
                    val today = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Calendar.getInstance().time)
                    val yesterday = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, -1) }.time
                    val yesterdayStr = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(yesterday)

                    // Fetch price fluctuation
                    val fluctuationResult = repository.getFluctuation(apiBase, symbols, yesterdayStr, today)

                    val finalResult =
                        result.map { currentRates ->
                            // Get current BTC rate as base for conversion
                            val btcToday = currentRates.find { it.code == "BTC" }?.rate ?: 1.0
                            val fluctuations = (fluctuationResult as? Either.Right)?.b ?: emptyMap()

                            currentRates.map { rateToday ->
                                // Convert each rate to be relative to 1 BTC
                                val rateInBtcToday = rateToday.rate / btcToday

                                rateToday.copy(
                                    rate = rateInBtcToday,
                                    fluctuation = fluctuations[rateToday.code] ?: 0.0,
                                )
                            }
                        }
                    emit(finalResult)
                }.flowOn(Dispatchers.IO)

            // Combine saved amount with network rates and calculate totals
            return combine(btcAmountFlow, ratesFlow) { amount, ratesEither ->
                ratesEither.map { rates ->
                    val calculatedRates = rates.map { it.copy(totalValue = it.rate * amount) }
                    WalletData(amount, calculatedRates)
                }
            }.flowOn(Dispatchers.Default)
        }
    }
