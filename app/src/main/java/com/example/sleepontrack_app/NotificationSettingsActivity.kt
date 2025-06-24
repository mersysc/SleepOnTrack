package com.example.sleepontrack_app

import android.app.*
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.appcompat.widget.SwitchCompat
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.sleepontrack_app.firestore.FirestoreManagement
import com.google.firebase.auth.FirebaseAuth
import java.util.*

class NotificationSettingsActivity : AppCompatActivity() {

    private lateinit var timeTextView: TextView
    private lateinit var notificationSwitch: SwitchCompat
    private lateinit var saveButton: Button

    private var selectedHour = -1
    private var selectedMinute = -1
    private var selectedTime = ""

    private val userId by lazy { FirebaseAuth.getInstance().currentUser?.uid }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.notifications_settings_activity)

        timeTextView = findViewById(R.id.timeTextView)
        notificationSwitch = findViewById(R.id.notificationSwitch)
        saveButton = findViewById(R.id.saveButton)
        val pickTimeButton = findViewById<Button>(R.id.pickTimeButton)

        // Ukryj przycisk na start
        saveButton.visibility = View.GONE

        pickTimeButton.setOnClickListener {
            val now = Calendar.getInstance()
            TimePickerDialog(
                this,
                { _, hourOfDay, minute ->
                    selectedHour = hourOfDay
                    selectedMinute = minute
                    selectedTime = String.format("%02d:%02d", hourOfDay, minute)
                    timeTextView.text = selectedTime
                    saveButton.visibility = View.VISIBLE
                },
                now.get(Calendar.HOUR_OF_DAY),
                now.get(Calendar.MINUTE),
                true
            ).show()
        }

        notificationSwitch.setOnCheckedChangeListener { _, _ ->
            saveButton.visibility = View.VISIBLE
        }

        userId?.let { uid ->
            FirestoreManagement().loadNotificationSettings(uid) { savedTime, isEnabled ->
                if (savedTime != null) {
                    selectedTime = savedTime
                    timeTextView.text = savedTime
                    val (h, m) = savedTime.split(":").map { it.toInt() }
                    selectedHour = h
                    selectedMinute = m
                    notificationSwitch.isChecked = isEnabled
                }
            }
        }

        saveButton.setOnClickListener {
            if (userId == null || selectedTime.isBlank()) {
                Toast.makeText(this, "Please select a time first", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            FirestoreManagement().saveNotificationSettings(
                userId!!,
                selectedTime,
                notificationSwitch.isChecked
            ) { success ->
                if (success) {
                    if (notificationSwitch.isChecked) {
                        scheduleNotification(selectedHour, selectedMinute)
                        Toast.makeText(this, "Notifications enabled", Toast.LENGTH_SHORT).show()
                    } else {
                        cancelNotification()
                        Toast.makeText(this, "Notifications disabled", Toast.LENGTH_SHORT).show()
                    }

                    val intent = Intent(this, MainPageActivity::class.java)
                    startActivity(intent)
                    finish()

                } else {
                    Toast.makeText(this, "Failed to update settings", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun scheduleNotification(hour: Int, minute: Int) {
        val intent = Intent(applicationContext, NotificationReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            applicationContext, 1001, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            if (before(Calendar.getInstance())) {
                add(Calendar.DAY_OF_MONTH, 1)
            }
        }

        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.setRepeating(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            AlarmManager.INTERVAL_DAY,
            pendingIntent
        )
    }

    private fun cancelNotification() {
        val intent = Intent(applicationContext, NotificationReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            applicationContext, 1001, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.cancel(pendingIntent)
    }
}
