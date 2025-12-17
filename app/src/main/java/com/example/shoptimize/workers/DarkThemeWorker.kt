package com.example.shoptimize.workers

import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters

class DarkThemeWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        return try {
            // Cambiar a modo oscuro 
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            val prefs = applicationContext.getSharedPreferences("shoptimize_prefs", Context.MODE_PRIVATE)
            prefs.edit().putString("theme_mode", "night").apply()
            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }
}
