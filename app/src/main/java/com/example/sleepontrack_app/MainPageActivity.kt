package com.example.sleepontrack_app

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class MainPageActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("WelcomeActivity", "WelcomeActivity started")
        setContentView(R.layout.main_page)

        val logSleep = findViewById<Button>(R.id.logSleepButton)
        val historyOfSleepButton = findViewById<Button>(R.id.recent_logs_button)


        logSleep.setOnClickListener {
            val intent = Intent(this, ClockActivity::class.java)
            startActivity(intent)
        }

        historyOfSleepButton.setOnClickListener{
            val intent = Intent(this, SleepListActivity::class.java)
            startActivity(intent)
        }

    }

}