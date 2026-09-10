package com.mncemngadi.bitcoinwalletapp.domain.model

data class BitcoinBalance(
    val amount: Double
)

data class CurrencyRate(
    val code: String,
    val rate: Double,
    val name: String,
    val fluctuation: Double? = null // Change percentage
)

data class WalletData(
    val btcAmount: Double,
    val rates: List<CurrencyRate>
)
