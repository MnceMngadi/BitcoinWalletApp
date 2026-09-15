package com.mncemngadi.bitcoinwalletapp.data.remote.dto

import kotlinx.serialization.Serializable

/**
 * Data Transfer Objects (DTOs) for Fixer API responses.
 * These classes are important as they define the structure for parsing raw JSON data from the API.
 */

@Serializable
/** Response for the latest exchange rates. */
data class LatestRatesDto(
    val success: Boolean,
    val timestamp: Long? = null,
    val base: String? = null,
    val date: String? = null,
    val rates: Map<String, Double>? = null,
    val error: FixerErrorDto? = null,
)

@Serializable
/** Response for price fluctuations. */
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
/** Details for a specific currency's fluctuation. */
data class FluctuationRateDto(
    val start_rate: Double,
    val end_rate: Double,
    val change: Double,
    val change_pct: Double,
)

@Serializable
/** Error structure returned by the Fixer API. */
data class FixerErrorDto(
    val code: Int,
    val type: String,
    val info: String? = null,
)
