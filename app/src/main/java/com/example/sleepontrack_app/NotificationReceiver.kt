package com.example.sleepontrack_app

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat

class NotificationReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val channelId = "sleep_reminder_channel"
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Tworzenie kanału powiadomień (dla Android 8+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Sleep Reminders",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Reminders to log your sleep."
            }
            notificationManager.createNotificationChannel(channel)
        }

        // Tworzenie powiadomienia
        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.mipmap.img) // zmień na swoją ikonę!
            .setContentTitle("Sleep Reminder")
            .setContentText("Don't forget to log your sleep today!")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()

        notificationManager.notify(1001, notification)
    }


}
