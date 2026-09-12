package com.mncemngadi.bitcoinwalletapp.presentation.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mncemngadi.bitcoinwalletapp.domain.model.CurrencyRate
import com.mncemngadi.bitcoinwalletapp.domain.usecase.GetWalletDataUseCase
import com.mncemngadi.bitcoinwalletapp.domain.usecase.UpdateBtcAmountUseCase
import com.mncemngadi.bitcoinwalletapp.domain.util.Either
import com.mncemngadi.bitcoinwalletapp.domain.util.Failure
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class WalletUiState(
    val btcAmount: String = "0.0",
    val rates: List<CurrencyRate> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
)

@HiltViewModel
class WalletViewModel
    @Inject
    constructor(
        private val getWalletDataUseCase: GetWalletDataUseCase,
        private val updateBtcAmountUseCase: UpdateBtcAmountUseCase,
    ) : ViewModel() {
        private val _uiState = MutableStateFlow(WalletUiState())
        val uiState: StateFlow<WalletUiState> = _uiState.asStateFlow()

        init {
            loadWalletData()
        }

        private fun loadWalletData() {
            _uiState.update { it.copy(isLoading = true, error = null) }
            viewModelScope.launch {
                getWalletDataUseCase().collect { result ->
                    when (result) {
                        is Either.Left -> {
                            val errorMessage =
                                when (val failure = result.a) {
                                    is Failure.NetworkConnection -> "Network error. Please check your connection."
                                    is Failure.ServerError -> failure.message ?: "Server error. Please try again later."
                                    is Failure.UnknownError -> failure.message ?: "An unknown error occurred."
                                    else -> "An error occurred."
                                }
                            _uiState.update { it.copy(isLoading = false, error = errorMessage) }
                        }
                        is Either.Right -> {
                            _uiState.update {
                                it.copy(
                                    isLoading = false,
                                    btcAmount = result.b.btcAmount.toString(),
                                    rates = result.b.rates,
                                    error = null,
                                )
                            }
                        }
                    }
                }
            }
        }

        fun onBtcAmountChange(newAmount: String) {
            // Validation: only numeric and decimal point
            if (newAmount.isEmpty() || newAmount.toDoubleOrNull() != null) {
                _uiState.update { it.copy(btcAmount = newAmount) }
                viewModelScope.launch {
                    val amount = newAmount.toDoubleOrNull() ?: 0.0
                    updateBtcAmountUseCase(amount)
                }
            }
        }

        fun refresh() {
            loadWalletData()
        }

        fun clearError() {
            _uiState.update { it.copy(error = null) }
        }
    }
