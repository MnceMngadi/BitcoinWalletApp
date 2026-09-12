package com.mncemngadi.bitcoinwalletapp.data.remote

import com.mncemngadi.bitcoinwalletapp.data.remote.dto.FluctuationDto
import com.mncemngadi.bitcoinwalletapp.data.remote.dto.LatestRatesDto
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface BitcoinWalletApiService {
    @GET("latest")
    suspend fun getLatestRates(
        @Query("base") base: String? = null,
        @Query("symbols") symbols: String? = null,
    ): LatestRatesDto

    @GET("{date}")
    suspend fun getHistoricalRates(
        @Path("date") date: String,
        @Query("base") base: String? = null,
        @Query("symbols") symbols: String? = null,
    ): LatestRatesDto

    @GET("fluctuation")
    suspend fun getFluctuation(
        @Query("base") base: String? = null,
        @Query("symbols") symbols: String? = null,
        @Query("start_date") startDate: String? = null,
        @Query("end_date") endDate: String? = null,
    ): FluctuationDto

    @GET("symbols")
    suspend fun getSymbols(): LatestRatesDto
}
