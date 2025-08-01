package com.tesmigue.climaapp.repository

import android.util.Log
import com.tesmigue.climaapp.api.ClimaApiService
import com.tesmigue.climaapp.api.RetrofitClient
import com.tesmigue.climaapp.model.ClimaResponse
import com.tesmigue.climaapp.model.PronosticoResponse

class ClimaRepository {
    private val api: ClimaApiService = RetrofitClient.instance.create(ClimaApiService::class.java)
    private val apiKey = "PON_LA_API_KEY_AQUI"

    companion object {
        private const val TAG = "ClimaRepository"
    }

    suspend fun obtenerClima(ciudad: String): ClimaResponse {
        Log.d(TAG, "Obteniendo clima para: $ciudad")

        try {
            val response = api.getClimaPorCiudad(ciudad, apiKey)

            if (response.isSuccessful) {
                Log.d(TAG, "Clima obtenido exitosamente para: $ciudad")
                return response.body() ?: throw Exception("Respuesta vacía del servidor")
            } else {
                Log.e(TAG, "Error en API: ${response.code()} - ${response.message()}")
                when (response.code()) {
                    404 -> throw Exception("Ciudad no encontrada")
                    401 -> throw Exception("Error de autenticación API")
                    429 -> throw Exception("Demasiadas solicitudes, intente más tarde")
                    500 -> throw Exception("Error interno del servidor")
                    else -> throw Exception("Error del servidor: ${response.code()}")
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Excepción al obtener clima", e)
            throw e
        }
    }

    suspend fun obtenerPronostico(ciudad: String): PronosticoResponse {
        Log.d(TAG, "Obteniendo pronóstico para: $ciudad")

        try {
            val response = api.getPronosticoPorCiudad(ciudad, apiKey)

            if (response.isSuccessful) {
                Log.d(TAG, "Pronóstico obtenido exitosamente para: $ciudad")
                return response.body() ?: throw Exception("Respuesta vacía del servidor")
            } else {
                Log.e(TAG, "Error en API pronóstico: ${response.code()} - ${response.message()}")
                when (response.code()) {
                    404 -> throw Exception("Ciudad no encontrada")
                    401 -> throw Exception("Error de autenticación API")
                    429 -> throw Exception("Demasiadas solicitudes, intente más tarde")
                    500 -> throw Exception("Error interno del servidor")
                    else -> throw Exception("Error del servidor: ${response.code()}")
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Excepción al obtener pronóstico", e)
            throw e
        }
    }

    suspend fun obtenerClimaPorCoordenadas(lat: Double, lon: Double): ClimaResponse {
        val response = api.getClimaPorCoordenadas(lat, lon, apiKey)
        if (response.isSuccessful) {
            return response.body() ?: throw Exception("Respuesta vacía")
        } else {
            throw Exception("Error: ${response.code()}")
        }
    }
}