package com.mncemngadi.bitcoinwalletapp.domain.model

data class BitcoinBalance(
    val amount: Double,
)

data class CurrencyRate(
    val code: String,
    val rate: Double,
    val name: String,
    // Change percentage for the last 24 hours
    val fluctuation: Double? = null,
)

data class WalletData(
    val btcAmount: Double,
    val rates: List<CurrencyRate>,
)
