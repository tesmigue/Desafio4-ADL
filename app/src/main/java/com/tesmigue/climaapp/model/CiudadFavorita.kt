package com.tesmigue.climaapp.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "ciudades_favoritas")
data class CiudadFavorita(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val nombre: String
)
