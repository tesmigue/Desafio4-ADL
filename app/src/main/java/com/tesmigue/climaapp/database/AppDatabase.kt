package com.tesmigue.climaapp.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.tesmigue.climaapp.model.CiudadFavorita

@Database(entities = [CiudadFavorita::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun ciudadDao(): CiudadFavoritaDao

    companion object {
        @Volatile private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "clima_db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
