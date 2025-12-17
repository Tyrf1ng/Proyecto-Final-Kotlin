package com.example.shoptimize.data.repository

import androidx.lifecycle.LiveData
import com.example.shoptimize.data.Producto
import com.example.shoptimize.data.dao.ProductoDao
import kotlinx.coroutines.flow.Flow

class ProductoRepository(private val productoDao: ProductoDao) {
    
    val allProductos: Flow<List<Producto>> = productoDao.getAllProductos()
    val allProductosLiveData: LiveData<List<Producto>> = productoDao.getAllProductosLiveData()
    
    suspend fun getProductoById(id: Int): Producto? {
        return productoDao.getProductoById(id)
    }
    
    fun searchProductos(query: String): Flow<List<Producto>> {
        return productoDao.searchProductos(query)
    }
    
    fun getProductosByCategoria(categoria: String): Flow<List<Producto>> {
        return productoDao.getProductosByCategoria(categoria)
    }
    
    suspend fun insertProducto(producto: Producto): Long {
        return productoDao.insertProducto(producto)
    }
    
    suspend fun insertProductos(productos: List<Producto>) {
        productoDao.insertProductos(productos)
    }
    
    suspend fun updateProducto(producto: Producto) {
        productoDao.updateProducto(producto)
    }
    
    suspend fun updateProductoPrecio(id: Int, precio: Int) {
        productoDao.updateProductoPrecio(id, precio)
    }
    
    suspend fun deleteProducto(producto: Producto) {
        productoDao.deleteProducto(producto)
    }
    
    suspend fun deleteProductoById(id: Int) {
        productoDao.deleteProductoById(id)
    }
    
    suspend fun deleteAllProductos() {
        productoDao.deleteAllProductos()
    }
}
