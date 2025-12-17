package com.example.shoptimize.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.shoptimize.data.*
import com.example.shoptimize.data.dao.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        Usuario::class,
        Producto::class,
        ListaDeCompra::class,
        ListaProductoCrossRef::class,
        Historial::class
    ],
    version = 3,
    exportSchema = false
)
abstract class ShoptimizeDatabase : RoomDatabase() {
    abstract fun usuarioDao(): UsuarioDao
    abstract fun productoDao(): ProductoDao
    abstract fun listaDeCompraDao(): ListaDeCompraDao
    abstract fun listaProductoCrossRefDao(): ListaProductoCrossRefDao
    abstract fun historialDao(): HistorialDao

    companion object {
        @Volatile
        private var INSTANCE: ShoptimizeDatabase? = null

        fun getInstance(context: Context): ShoptimizeDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    ShoptimizeDatabase::class.java,
                    "shoptimize_database"
                )
                    .fallbackToDestructiveMigration()
                    .addCallback(DatabaseCallback())
                    .build()
                    .also { INSTANCE = it }
            }
        }

        private class DatabaseCallback : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    CoroutineScope(Dispatchers.IO).launch {
                        populateDatabase(database)
                    }
                }
            }
        }

        private suspend fun populateDatabase(database: ShoptimizeDatabase) {
            val productoDao = database.productoDao()
            
            // Rellenar la base de datos con productos iniciales
            val productos = listOf(
                Producto(nombre = "Pan", precio = 3000, categoria = "Panadería"),
                Producto(nombre = "Leche", precio = 2500, categoria = "Lácteos"),
                Producto(nombre = "Arroz", precio = 5000, categoria = "Granos"),
                Producto(nombre = "Frijoles", precio = 4500, categoria = "Granos"),
                Producto(nombre = "Aceite", precio = 6000, categoria = "Condimentos"),
                Producto(nombre = "Azúcar", precio = 1800, categoria = "Condimentos"),
                Producto(nombre = "Café", precio = 6000, categoria = "Bebidas"),
                Producto(nombre = "Manzanas", precio = 2000, categoria = "Frutas"),
                Producto(nombre = "Naranjas", precio = 1800, categoria = "Frutas"),
                Producto(nombre = "Pollo", precio = 12000, categoria = "Carnes"),
                Producto(nombre = "Carne molida", precio = 14000, categoria = "Carnes"),
                Producto(nombre = "Lechuga", precio = 3000, categoria = "Verduras"),
                Producto(nombre = "Tomates", precio = 2500, categoria = "Verduras"),
                Producto(nombre = "Cebolla", precio = 1500, categoria = "Verduras"),
                Producto(nombre = "Queso", precio = 8000, categoria = "Lácteos"),
                Producto(nombre = "Yogur", precio = 3500, categoria = "Lácteos"),
                Producto(nombre = "Mantequilla", precio = 7000, categoria = "Lácteos"),
                Producto(nombre = "Papas", precio = 1500, categoria = "Verduras"),
                Producto(nombre = "Milo", precio = 4000, categoria = "Bebidas"),
                Producto(nombre = "Trigo", precio = 2500, categoria = "Granos")
            )
            
            productoDao.insertProductos(productos)
        }
    }
}