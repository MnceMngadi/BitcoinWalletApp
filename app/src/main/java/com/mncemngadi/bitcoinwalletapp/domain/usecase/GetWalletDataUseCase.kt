package com.mncemngadi.bitcoinwalletapp.domain.usecase

import com.mncemngadi.bitcoinwalletapp.domain.model.WalletData
import com.mncemngadi.bitcoinwalletapp.domain.repository.WalletRepository
import com.mncemngadi.bitcoinwalletapp.domain.util.Either
import com.mncemngadi.bitcoinwalletapp.domain.util.Failure
import com.mncemngadi.bitcoinwalletapp.domain.util.map
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import javax.inject.Inject

class GetWalletDataUseCase
    @Inject
    constructor(
        private val repository: WalletRepository,
    ) {
        operator fun invoke(): Flow<Either<Failure, WalletData>> {
            val btcAmountFlow = repository.getBtcAmount()

            // Static symbols for the API
            val symbols = listOf("BTC", "ZAR", "USD", "AUD")

            // Base currency for the API
            val apiBase = "BTC"

            // Create a single-shot flow that fetches network rates independently once
            val ratesFlow =
                flow {
                    val result = repository.getLatestRates(apiBase, symbols)

                    val today = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Calendar.getInstance().time)
                    val yesterday = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, -1) }.time
                    val yesterdayStr = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(yesterday)

                    val fluctuationResult = repository.getFluctuation(apiBase, symbols, yesterdayStr, today)

                    val finalResult =
                        result.map { currentRates ->
                            val btcInEurToday = currentRates.find { it.code == "BTC" }?.rate ?: 1.0
                            val fluctuations = (fluctuationResult as? Either.Right)?.b ?: emptyMap()

                            currentRates.map { rateToday ->
                                val rateInBtcToday = rateToday.rate / btcInEurToday

                                rateToday.copy(
                                    rate = rateInBtcToday,
                                    fluctuation = fluctuations[rateToday.code] ?: 0.0,
                                )
                            }
                        }
                    emit(finalResult)
                }

            // Combine the local amount stream with the single static network emission
            return combine(btcAmountFlow, ratesFlow) { amount, ratesEither ->
                ratesEither.map { rates ->
                    WalletData(amount, rates)
                }
            }
        }
    }
