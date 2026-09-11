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

            // We fetch rates and fluctuation for ZAR, USD, AUD
            // Fixer free tier might only allow EUR as base.
            // If so, we need to convert from EUR -> BTC and then EUR -> Other currencies.
            // But for this project we'll assume we can use BTC as base or convert accordingly.

            val symbols = listOf("BTC", "ZAR", "USD", "AUD")

            return combine(
                btcAmountFlow,
                flow {
                    val result = repository.getLatestRates("EUR", symbols)

                    // Fetch fluctuation as well
                    val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Calendar.getInstance().time)
                    val yesterday = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, -1) }.time
                    val yesterdayStr = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(yesterday)

                    val fluctuationResult = repository.getFluctuation("BTC", symbols, yesterdayStr, today)

                    val finalResult =
                        result.map { rates ->
                            // rates contains values relative to EUR
                            // We need BTC price in EUR to convert to other currencies relative to BTC
                            val btcInEur = rates.find { it.code == "BTC" }?.rate ?: 1.0

                            val fluctuations = (fluctuationResult as? Either.Right)?.b ?: emptyMap()

                            val btcBaseRates =
                                rates.map { rate ->
                                    // rate is 1 EUR = X code
                                    // btcInEur is 1 EUR = Y BTC
                                    // 1 BTC = (1/Y) EUR = (1/Y) * X code
                                    val rateInBtc = rate.rate / btcInEur
                                    rate.copy(
                                        rate = rateInBtc,
                                        fluctuation = fluctuations[rate.code],
                                    )
                                }
                            btcBaseRates
                        }

                    emit(finalResult)
                },
            ) { amount, ratesEither ->
                ratesEither.map { rates ->
                    WalletData(amount, rates)
                }
            }
        }
    }
