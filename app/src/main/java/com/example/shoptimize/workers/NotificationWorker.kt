package com.example.shoptimize.workers

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.shoptimize.R
import com.example.shoptimize.data.database.ShoptimizeDatabase
import kotlinx.coroutines.flow.first
import java.text.SimpleDateFormat
import java.util.*

// Genera notificaciones con resumen mensual de gastos
class NotificationWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        return try {
            val database = ShoptimizeDatabase.getInstance(applicationContext)
            val listaDao = database.listaDeCompraDao()
            
            val calendar = Calendar.getInstance()
            val dateFormat = SimpleDateFormat("yyyy-MM", Locale.getDefault())
            val mesActual = dateFormat.format(calendar.time)
            
            val listasConProductos = listaDao.getAllListasConProductos().first()
            val listasMesActual = listasConProductos.filter { 
                it.lista.fecha.startsWith(mesActual)
            }
            
            val totalMes = listasMesActual.sumOf { it.calculateTotal() }
            
            showNotification(listasMesActual.size, totalMes)
            
            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }

    private fun showNotification(cantidadListas: Int, total: Int) {
        val notificationManager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelId = "shoptimize_channel"
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Shoptimize Notificaciones",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Notificaciones de estadísticas de compras"
            }
            notificationManager.createNotificationChannel(channel)
        }
        
        val notification = NotificationCompat.Builder(applicationContext, channelId)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("Resumen mensual de Shoptimize")
            .setContentText("Has realizado $cantidadListas compras por un total de \$$total")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .build()
        
        notificationManager.notify(1, notification)
    }
}
