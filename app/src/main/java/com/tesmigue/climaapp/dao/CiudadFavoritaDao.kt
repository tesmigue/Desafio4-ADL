package com.tesmigue.climaapp.data

import androidx.room.*
import com.tesmigue.climaapp.model.CiudadFavorita
import kotlinx.coroutines.flow.Flow

@Dao
interface CiudadFavoritaDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertar(ciudad: CiudadFavorita)

    @Query("SELECT * FROM ciudades_favoritas")
    fun obtenerTodas(): Flow<List<CiudadFavorita>>

    @Delete
    suspend fun eliminar(ciudad: CiudadFavorita)
}