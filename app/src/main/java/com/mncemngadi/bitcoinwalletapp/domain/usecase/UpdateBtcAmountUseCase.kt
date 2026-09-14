package com.mncemngadi.bitcoinwalletapp.domain.usecase

import com.mncemngadi.bitcoinwalletapp.domain.repository.WalletRepository
import javax.inject.Inject

/**
 * Use case to update the saved Bitcoin amount.
 */
class UpdateBtcAmountUseCase
    @Inject
    constructor(
        private val repository: WalletRepository,
    ) {
        /** Updates the BTC balance in the repository. */
        suspend operator fun invoke(amount: Double) {
            repository.updateBtcAmount(amount)
        }
    }
