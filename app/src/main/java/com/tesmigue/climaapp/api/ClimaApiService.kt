package com.tesmigue.climaapp.api

import com.tesmigue.climaapp.model.ClimaResponse
import com.tesmigue.climaapp.model.PronosticoResponse
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.Response

interface ClimaApiService {
    @GET("weather")
    suspend fun getClimaPorCiudad(
        @Query("q") ciudad: String,
        @Query("appid") apiKey: String,
        @Query("units") units: String = "metric",
        @Query("lang") lang: String = "es"
    ): Response<ClimaResponse>

    @GET("forecast")
    suspend fun getPronosticoPorCiudad(
        @Query("q") ciudad: String,
        @Query("appid") apiKey: String,
        @Query("units") units: String = "metric",
        @Query("lang") lang: String = "es"
    ): Response<PronosticoResponse>
}