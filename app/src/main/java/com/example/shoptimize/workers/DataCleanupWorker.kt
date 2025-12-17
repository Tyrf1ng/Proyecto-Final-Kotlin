package com.example.shoptimize.workers

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.shoptimize.data.database.ShoptimizeDatabase
import kotlinx.coroutines.flow.first
import java.text.SimpleDateFormat
import java.util.*

// Limpia listas de compra con más de 5 años de antigüedad
class DataCleanupWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        return try {
            val database = ShoptimizeDatabase.getInstance(applicationContext)
            val listaDao = database.listaDeCompraDao()
            
            val calendar = Calendar.getInstance()
            calendar.add(Calendar.YEAR, -5)
            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val fechaLimite = dateFormat.format(calendar.time)
            
            val listas = listaDao.getAllListas().first()
            
            var deletedCount = 0
            listas.forEach { lista ->
                if (lista.fecha < fechaLimite) {
                    listaDao.deleteLista(lista)
                    deletedCount++
                }
            }
            
            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }
}
