package com.mncemngadi.bitcoinwalletapp.domain.usecase

import com.mncemngadi.bitcoinwalletapp.domain.model.CurrencyRate
import com.mncemngadi.bitcoinwalletapp.domain.repository.WalletRepository
import com.mncemngadi.bitcoinwalletapp.domain.util.Either
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class GetWalletDataUseCaseTest {
    private lateinit var repository: WalletRepository
    private lateinit var getWalletDataUseCase: GetWalletDataUseCase

    @Before
    fun setUp() {
        repository = mockk()
        getWalletDataUseCase = GetWalletDataUseCase(repository)
    }

    @Test
    fun `invoke should return combined wallet data when repository returns success`() =
        runTest {
            // Given
            val btcAmount = 0.5
            val rates =
                listOf(
                    CurrencyRate("BTC", 0.000015, "Bitcoin"),
                    CurrencyRate("ZAR", 18.5, "South African Rand"),
                )

            every { repository.getBtcAmount() } returns flowOf(btcAmount)
            coEvery { repository.getLatestRates(any(), any()) } returns Either.Right(rates)
            coEvery { repository.getFluctuation(any(), any(), any(), any()) } returns Either.Right(emptyMap())

            // When
            val result = getWalletDataUseCase().first()

            // Then
            assert(result is Either.Right)
            val walletData = (result as Either.Right).b
            assertEquals(btcAmount, walletData.btcAmount, 0.0)

            // 1 BTC today = (rateToday / btcToday) = 18.5 / 0.000015 = 1,233,333.33
            // totalValue = 0.5 * 1,233,333.33 = 616,666.66
            val zarRate = walletData.rates.find { it.code == "ZAR" }
            assertEquals(616666.666, zarRate?.totalValue ?: 0.0, 0.001)
        }
}
