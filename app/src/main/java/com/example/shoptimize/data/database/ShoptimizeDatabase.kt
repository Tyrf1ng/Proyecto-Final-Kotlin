package com.example.shoptimize.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.shoptimize.data.dao.UsuarioDao
import com.example.shoptimize.data.Usuario

@Database(entities = [Usuario::class], version = 1)
abstract class ShoptimizeDatabase: RoomDatabase() {
    abstract fun usuarioDao(): UsuarioDao
    companion object{
        @Volatile
        private var INSTANCE: ShoptimizeDatabase? = null
        fun getInstance(context: Context): ShoptimizeDatabase{
            return INSTANCE ?: synchronized(this) {
                INSTANCE?: Room.databaseBuilder(
                    context.applicationContext,
                    ShoptimizeDatabase::class.java,
                    "shoptimize_database"
                )
                .fallbackToDestructiveMigration()
                .build()
                .also { INSTANCE = it }
            }
        }
    }
}