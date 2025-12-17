package com.example.shoptimize.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.shoptimize.data.ListaDeCompra
import com.example.shoptimize.data.relations.ListaConProductos
import kotlinx.coroutines.flow.Flow

@Dao
interface ListaDeCompraDao {
    // Obtener todas las listas de compra
    @Query("SELECT * FROM listas_de_compra ORDER BY fecha DESC")
    fun getAllListas(): Flow<List<ListaDeCompra>>
    
    @Query("SELECT * FROM listas_de_compra ORDER BY fecha DESC")
    fun getAllListasLiveData(): LiveData<List<ListaDeCompra>>
    
    // Obtener lista por id
    @Query("SELECT * FROM listas_de_compra WHERE id = :id")
    suspend fun getListaById(id: Int): ListaDeCompra?
    
    // Obtener listas por usuario
    @Query("SELECT * FROM listas_de_compra WHERE usuarioId = :usuarioId ORDER BY fecha DESC")
    fun getListasByUsuario(usuarioId: Int): Flow<List<ListaDeCompra>>
    
    // Obtener listas por fecha
    @Query("SELECT * FROM listas_de_compra WHERE fecha LIKE :mesAno || '%' ORDER BY fecha DESC")
    fun getListasByMesAno(mesAno: String): Flow<List<ListaDeCompra>>
    
    // Obtener lista con productos (relación)
    @Transaction
    @Query("SELECT * FROM listas_de_compra WHERE id = :id")
    suspend fun getListaConProductos(id: Int): ListaConProductos?
    
    @Transaction
    @Query("SELECT * FROM listas_de_compra ORDER BY fecha DESC")
    fun getAllListasConProductos(): Flow<List<ListaConProductos>>
    
    @Transaction
    @Query("SELECT * FROM listas_de_compra WHERE usuarioId = :usuarioId ORDER BY fecha DESC")
    fun getListasConProductosByUsuario(usuarioId: Int): Flow<List<ListaConProductos>>
    
    // Insertar lista
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLista(lista: ListaDeCompra): Long
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertListas(listas: List<ListaDeCompra>)
    
    // Actualizar lista
    @Update
    suspend fun updateLista(lista: ListaDeCompra)
    
    @Query("UPDATE listas_de_compra SET nombre = :nombre WHERE id = :id")
    suspend fun updateListaNombre(id: Int, nombre: String)
    
    @Query("UPDATE listas_de_compra SET total = :total WHERE id = :id")
    suspend fun updateListaTotal(id: Int, total: Int)
    
    // Eliminar lista
    @Delete
    suspend fun deleteLista(lista: ListaDeCompra)
    
    @Query("DELETE FROM listas_de_compra WHERE id = :id")
    suspend fun deleteListaById(id: Int)
    
    @Query("DELETE FROM listas_de_compra")
    suspend fun deleteAllListas()
}
