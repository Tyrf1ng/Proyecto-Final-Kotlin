package com.example.shoptimize.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.shoptimize.data.Historial
import kotlinx.coroutines.flow.Flow

@Dao
interface HistorialDao {
    // Obtener todos los historiales
    @Query("SELECT * FROM historiales ORDER BY mesAno DESC")
    fun getAllHistoriales(): Flow<List<Historial>>
    
    @Query("SELECT * FROM historiales ORDER BY mesAno DESC")
    fun getAllHistorialesLiveData(): LiveData<List<Historial>>
    
    // Obtener historial por id
    @Query("SELECT * FROM historiales WHERE id = :id")
    suspend fun getHistorialById(id: Int): Historial?
    
    // Obtener historial por mes/año
    @Query("SELECT * FROM historiales WHERE mesAno = :mesAno")
    suspend fun getHistorialByMesAno(mesAno: String): Historial?
    
    @Query("SELECT * FROM historiales WHERE mesAno = :mesAno")
    fun getHistorialByMesAnoFlow(mesAno: String): Flow<Historial?>
    
    // Insertar historial
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHistorial(historial: Historial): Long
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHistoriales(historiales: List<Historial>)
    
    // Actualizar historial
    @Update
    suspend fun updateHistorial(historial: Historial)
    
    @Query("UPDATE historiales SET total = :total WHERE id = :id")
    suspend fun updateHistorialTotal(id: Int, total: Int)
    
    // Eliminar historial
    @Delete
    suspend fun deleteHistorial(historial: Historial)
    
    @Query("DELETE FROM historiales WHERE id = :id")
    suspend fun deleteHistorialById(id: Int)
    
    @Query("DELETE FROM historiales")
    suspend fun deleteAllHistoriales()
}
