package com.example.shoptimize.data.repository

import androidx.lifecycle.LiveData
import com.example.shoptimize.data.ListaDeCompra
import com.example.shoptimize.data.ListaProductoCrossRef
import com.example.shoptimize.data.Producto
import com.example.shoptimize.data.dao.ListaDeCompraDao
import com.example.shoptimize.data.dao.ListaProductoCrossRefDao
import com.example.shoptimize.data.relations.ListaConProductos
import kotlinx.coroutines.flow.Flow

class ListaDeCompraRepository(
    private val listaDeCompraDao: ListaDeCompraDao,
    private val listaProductoCrossRefDao: ListaProductoCrossRefDao
) {
    
    val allListas: Flow<List<ListaDeCompra>> = listaDeCompraDao.getAllListas()
    val allListasLiveData: LiveData<List<ListaDeCompra>> = listaDeCompraDao.getAllListasLiveData()
    val allListasConProductos: Flow<List<ListaConProductos>> = listaDeCompraDao.getAllListasConProductos()
    
    suspend fun getListaById(id: Int): ListaDeCompra? {
        return listaDeCompraDao.getListaById(id)
    }
    
    suspend fun getListaConProductos(id: Int): ListaConProductos? {
        return listaDeCompraDao.getListaConProductos(id)
    }
    
    fun getListasByUsuario(usuarioId: Int): Flow<List<ListaDeCompra>> {
        return listaDeCompraDao.getListasByUsuario(usuarioId)
    }
    
    fun getListasConProductosByUsuario(usuarioId: Int): Flow<List<ListaConProductos>> {
        return listaDeCompraDao.getListasConProductosByUsuario(usuarioId)
    }
    
    fun getListasByMesAno(mesAno: String): Flow<List<ListaDeCompra>> {
        return listaDeCompraDao.getListasByMesAno(mesAno)
    }
    
    suspend fun insertLista(lista: ListaDeCompra): Long {
        return listaDeCompraDao.insertLista(lista)
    }
    
    suspend fun insertListas(listas: List<ListaDeCompra>) {
        listaDeCompraDao.insertListas(listas)
    }
    
    suspend fun updateLista(lista: ListaDeCompra) {
        listaDeCompraDao.updateLista(lista)
    }
    
    suspend fun updateListaNombre(id: Int, nombre: String) {
        listaDeCompraDao.updateListaNombre(id, nombre)
    }
    
    suspend fun deleteLista(lista: ListaDeCompra) {
        listaDeCompraDao.deleteLista(lista)
    }
    
    suspend fun deleteListaById(id: Int) {
        listaDeCompraDao.deleteListaById(id)
    }
    
    suspend fun deleteAllListas() {
        listaDeCompraDao.deleteAllListas()
    }
    
    
    suspend fun addProductoToLista(listaId: Int, productoId: Int, cantidad: Int = 1) {
        val cantidadActual = listaProductoCrossRefDao.getCantidad(listaId, productoId)
        
        if (cantidadActual != null) {
            val nuevaCantidad = cantidadActual + cantidad
            listaProductoCrossRefDao.updateCantidad(listaId, productoId, nuevaCantidad)
        } else {
            val crossRef = ListaProductoCrossRef(listaId, productoId, cantidad)
            listaProductoCrossRefDao.addProductoToLista(crossRef)
        }
        
        updateTotalLista(listaId)
    }
    
    suspend fun addProductosToLista(listaId: Int, productos: List<Producto>) {
        val crossRefs = productos.map { producto ->
            ListaProductoCrossRef(listaId, producto.id, 1)
        }
        listaProductoCrossRefDao.addProductosToLista(crossRefs)
        updateTotalLista(listaId)
    }
    
    suspend fun removeProductoFromLista(listaId: Int, productoId: Int) {
        listaProductoCrossRefDao.removeProductoFromListaById(listaId, productoId)
        updateTotalLista(listaId)
    }
    
    suspend fun removeAllProductosFromLista(listaId: Int) {
        listaProductoCrossRefDao.removeAllProductosFromLista(listaId)
        listaDeCompraDao.updateListaTotal(listaId, 0)
    }
    
    suspend fun updateCantidadProducto(listaId: Int, productoId: Int, cantidad: Int) {
        listaProductoCrossRefDao.updateCantidad(listaId, productoId, cantidad)
        updateTotalLista(listaId)
    }
    
    suspend fun getProductosCrossRef(listaId: Int): List<ListaProductoCrossRef> {
        return listaProductoCrossRefDao.getCrossRefsByLista(listaId)
    }
    
    private suspend fun updateTotalLista(listaId: Int) {
        val listaConProductos = listaDeCompraDao.getListaConProductos(listaId)
        if (listaConProductos != null) {
            val crossRefs = listaProductoCrossRefDao.getCrossRefsByLista(listaId)
            val cantidadMap = crossRefs.associate { it.productoId to it.cantidad }
            
            val total = listaConProductos.productos.sumOf { producto ->
                val cantidad = cantidadMap[producto.id] ?: 1
                producto.precio * cantidad
            }
            
            listaDeCompraDao.updateListaTotal(listaId, total)
        }
    }
}
