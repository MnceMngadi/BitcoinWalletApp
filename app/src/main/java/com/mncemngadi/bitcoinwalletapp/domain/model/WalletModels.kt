package com.mncemngadi.bitcoinwalletapp.domain.model

/** Exchange rate and calculated value for a currency. */
data class CurrencyRate(
    val code: String,
    val rate: Double,
    val name: String,
    val totalValue: Double = 0.0,
    // Change percentage for the last 24 hours
    val fluctuation: Double? = null,
)

/** Combined data structure for the wallet screen state. */
data class WalletData(
    val btcAmount: Double,
    val rates: List<CurrencyRate>,
)
