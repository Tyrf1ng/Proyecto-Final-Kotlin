package com.example.shoptimize.data.repository

import androidx.lifecycle.LiveData
import com.example.shoptimize.data.Historial
import com.example.shoptimize.data.dao.HistorialDao
import kotlinx.coroutines.flow.Flow

class HistorialRepository(private val historialDao: HistorialDao) {
    
    // Obtener todos los historiales
    val allHistoriales: Flow<List<Historial>> = historialDao.getAllHistoriales()
    val allHistorialesLiveData: LiveData<List<Historial>> = historialDao.getAllHistorialesLiveData()
    
    // Obtener historial por id
    suspend fun getHistorialById(id: Int): Historial? {
        return historialDao.getHistorialById(id)
    }
    
    // Obtener historial por mes/año
    suspend fun getHistorialByMesAno(mesAno: String): Historial? {
        return historialDao.getHistorialByMesAno(mesAno)
    }
    
    fun getHistorialByMesAnoFlow(mesAno: String): Flow<Historial?> {
        return historialDao.getHistorialByMesAnoFlow(mesAno)
    }
    
    // Insertar historial
    suspend fun insertHistorial(historial: Historial): Long {
        return historialDao.insertHistorial(historial)
    }
    
    suspend fun insertHistoriales(historiales: List<Historial>) {
        historialDao.insertHistoriales(historiales)
    }
    
    // Actualizar historial
    suspend fun updateHistorial(historial: Historial) {
        historialDao.updateHistorial(historial)
    }
    
    suspend fun updateHistorialTotal(id: Int, total: Int) {
        historialDao.updateHistorialTotal(id, total)
    }
    
    // Eliminar historial
    suspend fun deleteHistorial(historial: Historial) {
        historialDao.deleteHistorial(historial)
    }
    
    suspend fun deleteHistorialById(id: Int) {
        historialDao.deleteHistorialById(id)
    }
    
    suspend fun deleteAllHistoriales() {
        historialDao.deleteAllHistoriales()
    }
}
