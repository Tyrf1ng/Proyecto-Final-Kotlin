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
    version = 5,
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
                Producto(nombre = "Pan", precio = 3000, categoria = "Panadería", imagenUrl = "https://images.unsplash.com/photo-1509440159596-0249088772ff?w=400"),
                Producto(nombre = "Leche", precio = 2500, categoria = "Lácteos", imagenUrl = "https://images.unsplash.com/photo-1550583724-b2692b85b150?w=400"),
                Producto(nombre = "Arroz", precio = 5000, categoria = "Granos", imagenUrl = "https://thumbs.dreamstime.com/b/granos-de-arroz-blanco-en-cuchara-madera-sobre-fondo-crudo-org%C3%A1nicos-sanos-para-cocinar-comida-asi%C3%A1tica-popular-la-cocina-385065462.jpg"),
                Producto(nombre = "Frijoles", precio = 4500, categoria = "Granos", imagenUrl = "https://images.cookforyourlife.org/wp-content/uploads/2018/08/Healthy-Baked-Beans.jpg"),
                Producto(nombre = "Aceite", precio = 6000, categoria = "Condimentos", imagenUrl = "https://www.academianutricionydietetica.org/wp-content/uploads/2025/04/aceite-de-oliva-pexels-pixabay-33783-copia.jpg"),
                Producto(nombre = "Azúcar", precio = 1800, categoria = "Condimentos", imagenUrl = "https://gnc.com.gt/cdn/shop/articles/unnamed_ef009e72-783c-47de-a59f-546ef2a91fc6_800x.jpg?v=1666883182"),
                Producto(nombre = "Café", precio = 6000, categoria = "Bebidas", imagenUrl = "https://elcomercio.pe/resizer/v2/EJFQLH5PVFGN3LSXS54VX34C34.png?auth=34b508e33a928c80a760dd453afc2814e8a3c84e2cca5e756ffe5a19144af338&width=980&height=528&quality=75&smart=true"),
                Producto(nombre = "Manzanas", precio = 2000, categoria = "Frutas", imagenUrl = "https://images.unsplash.com/photo-1568702846914-96b305d2aaeb?w=400"),
                Producto(nombre = "Naranjas", precio = 1800, categoria = "Frutas", imagenUrl = "https://images.unsplash.com/photo-1547514701-42782101795e?w=400"),
                Producto(nombre = "Pollo", precio = 12000, categoria = "Carnes", imagenUrl = "https://images.unsplash.com/photo-1587593810167-a84920ea0781?w=400"),
                Producto(nombre = "Carne molida", precio = 14000, categoria = "Carnes", imagenUrl = "https://editorialtelevisa.brightspotcdn.com/dims4/default/935c135/2147483647/strip/true/crop/600x338+0+31/resize/1000x563!/quality/90/?url=https%3A%2F%2Fk2-prod-editorial-televisa.s3.us-east-1.amazonaws.com%2Fbrightspot%2Fwp-content%2Fuploads%2F2021%2F03%2Fcarne-molida.jpg"),
                Producto(nombre = "Lechuga", precio = 3000, categoria = "Verduras", imagenUrl = "https://images.unsplash.com/photo-1622206151226-18ca2c9ab4a1?w=400"),
                Producto(nombre = "Tomates", precio = 2500, categoria = "Verduras", imagenUrl = "https://imag.bonviveur.com/racimos-de-tomates-frescos-vendidos-como-verdura.jpg"),
                Producto(nombre = "Cebolla", precio = 1500, categoria = "Verduras", imagenUrl = "https://images.unsplash.com/photo-1508747703725-719777637510?w=400"),
                Producto(nombre = "Queso", precio = 8000, categoria = "Lácteos", imagenUrl = "https://images.unsplash.com/photo-1486297678162-eb2a19b0a32d?w=400"),
                Producto(nombre = "Yogur", precio = 3500, categoria = "Lácteos", imagenUrl = "https://images.unsplash.com/photo-1488477181946-6428a0291777?w=400"),
                Producto(nombre = "Mantequilla", precio = 7000, categoria = "Lácteos", imagenUrl = "https://images.unsplash.com/photo-1589985270826-4b7bb135bc9d?w=400"),
                Producto(nombre = "Papas", precio = 1500, categoria = "Verduras", imagenUrl = "https://images.unsplash.com/photo-1518977676601-b53f82aba655?w=400"),
                Producto(nombre = "Milo", precio = 4000, categoria = "Bebidas", imagenUrl = "https://dojiw2m9tvv09.cloudfront.net/42730/product/7613034635525-alimentos-milo-700g-puntobarato7865.jpg"),
                Producto(nombre = "Trigo", precio = 2500, categoria = "Granos", imagenUrl = "https://images.unsplash.com/photo-1574323347407-f5e1ad6d020b?w=400")
            )
            
            productoDao.insertProductos(productos)
        }
    }
}