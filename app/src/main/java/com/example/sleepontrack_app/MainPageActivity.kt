package com.example.sleepontrack_app

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class MainPageActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("WelcomeActivity", "WelcomeActivity started")
        setContentView(R.layout.main_page)

        val logSleep = findViewById<Button>(R.id.logSleepButton)
        val historyOfSleepButton = findViewById<Button>(R.id.recent_logs_button)
        val notificationsButton = findViewById<Button>(R.id.notifications_button)
        val logoutButton= findViewById<Button>(R.id.logout_button)

        logSleep.setOnClickListener {
            val intent = Intent(this, ClockActivity::class.java)
            startActivity(intent)
        }

        historyOfSleepButton.setOnClickListener{
            val intent = Intent(this, SleepListActivity::class.java)
            startActivity(intent)
        }

        notificationsButton.setOnClickListener{
            val intent = Intent(this, ClockActivity::class.java)
            startActivity(intent)
        }

        logoutButton.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
    }

}