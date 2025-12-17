package com.example.shoptimize.data.dao

import androidx.room.*
import com.example.shoptimize.data.ListaProductoCrossRef
import com.example.shoptimize.data.Producto

@Dao
interface ListaProductoCrossRefDao {
    // Agregar producto a lista
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addProductoToLista(crossRef: ListaProductoCrossRef)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addProductosToLista(crossRefs: List<ListaProductoCrossRef>)
    
    // Eliminar producto de lista
    @Delete
    suspend fun removeProductoFromLista(crossRef: ListaProductoCrossRef)
    
    @Query("DELETE FROM lista_producto_cross_ref WHERE listaId = :listaId AND productoId = :productoId")
    suspend fun removeProductoFromListaById(listaId: Int, productoId: Int)
    
    // Eliminar todos los productos de una lista
    @Query("DELETE FROM lista_producto_cross_ref WHERE listaId = :listaId")
    suspend fun removeAllProductosFromLista(listaId: Int)
    
    // Actualizar cantidad
    @Query("UPDATE lista_producto_cross_ref SET cantidad = :cantidad WHERE listaId = :listaId AND productoId = :productoId")
    suspend fun updateCantidad(listaId: Int, productoId: Int, cantidad: Int)
    
    // Obtener cantidad de un producto en una lista
    @Query("SELECT cantidad FROM lista_producto_cross_ref WHERE listaId = :listaId AND productoId = :productoId")
    suspend fun getCantidad(listaId: Int, productoId: Int): Int?
    
    // Obtener todos los cross refs de una lista
    @Query("SELECT * FROM lista_producto_cross_ref WHERE listaId = :listaId")
    suspend fun getCrossRefsByLista(listaId: Int): List<ListaProductoCrossRef>
}
