package com.mncemngadi.bitcoinwalletapp.domain.usecase

import com.mncemngadi.bitcoinwalletapp.domain.repository.WalletRepository
import javax.inject.Inject

class UpdateBtcAmountUseCase @Inject constructor(
    private val repository: WalletRepository
) {
    suspend operator fun invoke(amount: Double) {
        repository.updateBtcAmount(amount)
    }
}
