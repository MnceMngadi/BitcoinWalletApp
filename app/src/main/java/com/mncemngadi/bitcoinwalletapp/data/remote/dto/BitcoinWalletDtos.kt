package com.mncemngadi.bitcoinwalletapp.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class LatestRatesDto(
    val success: Boolean,
    val timestamp: Long? = null,
    val base: String? = null,
    val date: String? = null,
    val rates: Map<String, Double>? = null,
    val error: FixerErrorDto? = null,
)

@Serializable
data class FluctuationDto(
    val success: Boolean,
    val fluctuation: Boolean? = null,
    val start_date: String? = null,
    val end_date: String? = null,
    val base: String? = null,
    val rates: Map<String, FluctuationRateDto>? = null,
    val error: FixerErrorDto? = null,
)

@Serializable
data class FluctuationRateDto(
    val start_rate: Double,
    val end_rate: Double,
    val change: Double,
    val change_pct: Double,
)

@Serializable
data class FixerErrorDto(
    val code: Int,
    val type: String,
    val info: String? = null,
)
