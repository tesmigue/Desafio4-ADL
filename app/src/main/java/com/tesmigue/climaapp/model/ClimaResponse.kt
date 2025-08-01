package com.tesmigue.climaapp.model

import com.google.gson.annotations.SerializedName

data class ClimaResponse(
    @SerializedName("name") val nombre: String,
    val weather: List<Weather>,
    val main: Main
)

data class  Main(
    val temp: Double,
    val pressure: Int,
    val humidity: Int,
    @SerializedName("temp_min") val tempMin: Double,
    @SerializedName("temp_max") val temp_max: Double
)

data class Weather(
    val id: Int,
    val main: String,
    val description: String,
    val icon: String
)