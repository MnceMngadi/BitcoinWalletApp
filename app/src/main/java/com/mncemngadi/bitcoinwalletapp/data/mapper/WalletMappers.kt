package com.mncemngadi.bitcoinwalletapp.data.mapper

import com.mncemngadi.bitcoinwalletapp.data.remote.dto.LatestRatesDto
import com.mncemngadi.bitcoinwalletapp.domain.model.CurrencyRate

fun LatestRatesDto.toDomain(): List<CurrencyRate> {
    return rates?.map { (code, rate) ->
        CurrencyRate(
            code = code,
            rate = rate,
            name = getCurrencyName(code),
        )
    } ?: emptyList()
}

private fun getCurrencyName(code: String): String {
    return when (code) {
        "BTC" -> "Bitcoin"
        "ZAR" -> "South African Rand"
        "USD" -> "US Dollar"
        "AUD" -> "Australian Dollar"
        else -> code
    }
}
