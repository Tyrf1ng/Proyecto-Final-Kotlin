package com.example.shoptimize.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.shoptimize.data.Producto
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductoDao {
    @Query("SELECT * FROM productos ORDER BY nombre ASC")
    fun getAllProductos(): Flow<List<Producto>>
    
    @Query("SELECT * FROM productos ORDER BY nombre ASC")
    fun getAllProductosLiveData(): LiveData<List<Producto>>
    
    @Query("SELECT * FROM productos WHERE id = :id")
    suspend fun getProductoById(id: Int): Producto?
    
    @Query("SELECT * FROM productos WHERE nombre LIKE '%' || :query || '%' OR categoria LIKE '%' || :query || '%'")
    fun searchProductos(query: String): Flow<List<Producto>>
    
    @Query("SELECT * FROM productos WHERE categoria = :categoria ORDER BY nombre ASC")
    fun getProductosByCategoria(categoria: String): Flow<List<Producto>>
    
    @Query("SELECT * FROM productos WHERE nombre = :nombre")
    suspend fun getProductoByNombre(nombre: String): Producto?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProducto(producto: Producto): Long
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProductos(productos: List<Producto>)
    
    @Update
    suspend fun updateProducto(producto: Producto)
    
    @Query("UPDATE productos SET precio = :precio WHERE id = :id")
    suspend fun updateProductoPrecio(id: Int, precio: Int)
    
    @Delete
    suspend fun deleteProducto(producto: Producto)
    
    @Query("DELETE FROM productos WHERE id = :id")
    suspend fun deleteProductoById(id: Int)
    
    @Query("DELETE FROM productos")
    suspend fun deleteAllProductos()
}
