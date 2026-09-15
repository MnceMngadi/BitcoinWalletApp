package com.mncemngadi.bitcoinwalletapp.presentation.ui.viewmodel

import com.mncemngadi.bitcoinwalletapp.domain.model.WalletData
import com.mncemngadi.bitcoinwalletapp.domain.usecase.GetWalletDataUseCase
import com.mncemngadi.bitcoinwalletapp.domain.usecase.UpdateBtcAmountUseCase
import com.mncemngadi.bitcoinwalletapp.domain.util.Either
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class WalletViewModelTest {
    private val testDispatcher = UnconfinedTestDispatcher()
    private lateinit var getWalletDataUseCase: GetWalletDataUseCase
    private lateinit var updateBtcAmountUseCase: UpdateBtcAmountUseCase
    private lateinit var viewModel: WalletViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        getWalletDataUseCase = mockk()
        updateBtcAmountUseCase = mockk()

        // Default behavior for init block
        every { getWalletDataUseCase() } returns flowOf(Either.Right(WalletData(0.0, emptyList())))
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `loadWalletData should update state with btcAmount and rates on success`() =
        runTest {
            // Given
            val btcAmount = 1.0
            val walletData = WalletData(btcAmount, emptyList())
            every { getWalletDataUseCase() } returns flowOf(Either.Right(walletData))

            // When
            viewModel = WalletViewModel(getWalletDataUseCase, updateBtcAmountUseCase)

            // Then
            assertEquals("1", viewModel.uiState.value.btcAmount)
            assertEquals(false, viewModel.uiState.value.isLoading)
            assertEquals(null, viewModel.uiState.value.error)
        }

    @Test
    fun `onBtcAmountChange should update btcAmount in state`() =
        runTest {
            // Given
            viewModel = WalletViewModel(getWalletDataUseCase, updateBtcAmountUseCase)
            coEvery { updateBtcAmountUseCase(any()) } returns Unit

            // When
            viewModel.onBtcAmountChange("0.5")

            // Then
            assertEquals("0.5", viewModel.uiState.value.btcAmount)
        }
}
