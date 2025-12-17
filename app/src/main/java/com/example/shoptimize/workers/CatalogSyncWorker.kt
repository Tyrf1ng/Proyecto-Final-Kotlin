package com.example.shoptimize.workers

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.shoptimize.data.Producto
import com.example.shoptimize.data.database.ShoptimizeDatabase

class CatalogSyncWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        return try {
            val database = ShoptimizeDatabase.getInstance(applicationContext)
            val productoDao = database.productoDao()
            
            val nuevosProductos = listOf(
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
                Producto(nombre = "Mantequilla", precio = 7000, categoria = "Lácteos")
            )
            
            val productosAInsertar = nuevosProductos.filter { nuevo ->
                productoDao.getProductoByNombre(nuevo.nombre) == null
            }
            if (productosAInsertar.isNotEmpty()) {
                productoDao.insertProductos(productosAInsertar)
            }
            
            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }
}
