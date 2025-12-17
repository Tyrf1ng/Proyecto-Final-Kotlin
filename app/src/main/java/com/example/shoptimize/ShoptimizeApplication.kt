package com.example.shoptimize

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import androidx.work.*
import com.example.shoptimize.workers.CatalogSyncWorker
import com.example.shoptimize.workers.DataCleanupWorker
import com.example.shoptimize.workers.NotificationWorker
import com.example.shoptimize.workers.DarkThemeWorker
import java.time.Duration
import java.time.LocalDateTime
import java.util.concurrent.TimeUnit

class ShoptimizeApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        applySavedThemeMode()
        setupWorkManager()
    }

    private fun applySavedThemeMode() {
        val prefs = getSharedPreferences("shoptimize_prefs", MODE_PRIVATE)
        when (prefs.getString("theme_mode", "system")) {
            "night" -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            "day" -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            else -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
        }
    }

    private fun setupWorkManager() {
        val dataCleanupRequest = PeriodicWorkRequestBuilder<DataCleanupWorker>(
            1, TimeUnit.DAYS
        )
            .setConstraints(
                Constraints.Builder()
                    .setRequiresBatteryNotLow(true)
                    .build()
            )
            .build()

        val notificationRequest = PeriodicWorkRequestBuilder<NotificationWorker>(
            7, TimeUnit.DAYS
        )
            .setConstraints(
                Constraints.Builder()
                    .setRequiresBatteryNotLow(true)
                    .build()
            )
            .setInitialDelay(1, TimeUnit.HOURS)
            .build()

        val catalogSyncRequest = PeriodicWorkRequestBuilder<CatalogSyncWorker>(
            12, TimeUnit.HOURS
        )
            .setConstraints(
                Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build()
            )
            .build()

        val darkThemeRequest = PeriodicWorkRequestBuilder<DarkThemeWorker>(
            24, TimeUnit.HOURS
        )
            .setInitialDelay(calculateInitialDelayMinutes(20), TimeUnit.MINUTES)
            .build()

        WorkManager.getInstance(this).apply {
            enqueueUniquePeriodicWork(
                "data_cleanup_work",
                ExistingPeriodicWorkPolicy.KEEP,
                dataCleanupRequest
            )
            
            enqueueUniquePeriodicWork(
                "notification_work",
                ExistingPeriodicWorkPolicy.KEEP,
                notificationRequest
            )
            
            enqueueUniquePeriodicWork(
                "catalog_sync_work",
                ExistingPeriodicWorkPolicy.KEEP,
                catalogSyncRequest
            )

            enqueueUniquePeriodicWork(
                "dark_theme_work",
                ExistingPeriodicWorkPolicy.KEEP,
                darkThemeRequest
            )

            val isDebug = (applicationInfo.flags and android.content.pm.ApplicationInfo.FLAG_DEBUGGABLE) != 0
            if (isDebug) {
                val immediateNotification = OneTimeWorkRequestBuilder<NotificationWorker>()
                    .setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
                    .addTag("debug_notification_once")
                    .build()
                enqueueUniqueWork(
                    "debug_notification_once",
                    ExistingWorkPolicy.REPLACE,
                    immediateNotification
                )

                val immediateCleanup = OneTimeWorkRequestBuilder<DataCleanupWorker>()
                    .setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
                    .addTag("debug_cleanup_once")
                    .build()
                enqueueUniqueWork(
                    "debug_cleanup_once",
                    ExistingWorkPolicy.REPLACE,
                    immediateCleanup
                )

                val immediateCatalog = OneTimeWorkRequestBuilder<CatalogSyncWorker>()
                    .setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
                    .addTag("debug_catalog_once")
                    .build()
                enqueueUniqueWork(
                    "debug_catalog_once",
                    ExistingWorkPolicy.REPLACE,
                    immediateCatalog
                )

                val immediateDark = OneTimeWorkRequestBuilder<DarkThemeWorker>()
                    .setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
                    .addTag("debug_darktheme_once")
                    .build()
                enqueueUniqueWork(
                    "debug_darktheme_once",
                    ExistingWorkPolicy.REPLACE,
                    immediateDark
                )
            }
        }
    }

    private fun calculateInitialDelayMinutes(targetHour: Int): Long {
        val now = LocalDateTime.now()
        val targetToday = now.withHour(targetHour).withMinute(0).withSecond(0).withNano(0)
        val target = if (now.isAfter(targetToday)) targetToday.plusDays(1) else targetToday
        return Duration.between(now, target).toMinutes()
    }
}
