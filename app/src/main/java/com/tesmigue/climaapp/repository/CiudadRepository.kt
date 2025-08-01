package com.tesmigue.climaapp.repository

import com.tesmigue.climaapp.data.CiudadFavoritaDao
import com.tesmigue.climaapp.model.CiudadFavorita
import kotlinx.coroutines.flow.Flow

class CiudadRepository(private val dao: CiudadFavoritaDao) {
    fun obtenerCiudadesFavoritas(): Flow<List<CiudadFavorita>> = dao.obtenerTodas()
    suspend fun insertarCiudad(nombre: String) = dao.insertar(CiudadFavorita(nombre = nombre))
    suspend fun eliminarCiudad(ciudad: CiudadFavorita) = dao.eliminar(ciudad)
    fun guardar(ciudad: String) {

    }
}
