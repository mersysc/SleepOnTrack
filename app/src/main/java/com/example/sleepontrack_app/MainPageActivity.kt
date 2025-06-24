package com.example.sleepontrack_app

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import androidx.appcompat.widget.Toolbar
import androidx.appcompat.app.ActionBarDrawerToggle
import com.example.sleepontrack_app.firestore.FirestoreManagement
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.concurrent.TimeUnit


class MainPageActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_drawer)

        loadSleepSummary()

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        val drawerLayout = findViewById<DrawerLayout>(R.id.drawer_layout)
        val navView = findViewById<NavigationView>(R.id.nav_view)

        // Ikona hamburgera
        val toggle = ActionBarDrawerToggle(
            this, drawerLayout, toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        // Obsługa kliknięć w menu
        navView.setNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_notifications -> {
                    startActivity(Intent(this, NotificationSettingsActivity::class.java))
                    finishAffinity()
                    true
                }

                R.id.nav_logout -> {
                    FirebaseAuth.getInstance().signOut()
                    startActivity(Intent(this, LoginActivity::class.java))
                    finishAffinity()
                    true
                }

                else -> false
            }
        }

        findViewById<Button>(R.id.logSleepButton).setOnClickListener {
            startActivity(Intent(this, ClockActivity::class.java))
        }

        findViewById<Button>(R.id.recent_logs_button).setOnClickListener {
            startActivity(Intent(this, SleepListActivity::class.java))
        }
    }

    private fun loadSleepSummary() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return

        CoroutineScope(Dispatchers.Main).launch {
            try {
                val allSessions = withContext(Dispatchers.IO) {
                    FirestoreManagement().loadAllSleepSessions(userId)
                }

                val dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
                val calendar = Calendar.getInstance()
                calendar.add(Calendar.DAY_OF_YEAR, -6)
                val sevenDaysAgo = calendar.time

                val recentSessions = allSessions.filter {
                    val sessionDate = dateFormat.parse(it.date)
                    sessionDate != null && sessionDate >= sevenDaysAgo
                }

                if (recentSessions.isEmpty()) {
                    findViewById<TextView>(R.id.sleepSummaryText).text = "Add your sleeping sessions..."
                    return@launch
                }

                val durationsInMinutes = recentSessions.mapNotNull {
                    val sleep = SimpleDateFormat("HH:mm", Locale.getDefault()).parse(it.sleepTime)
                    val wake = SimpleDateFormat("HH:mm", Locale.getDefault()).parse(it.wakeUpTime)
                    if (sleep != null && wake != null) {
                        var diff = wake.time - sleep.time
                        if (diff < 0) diff += 24 * 60 * 60 * 1000 // jeśli przeszło przez północ
                        TimeUnit.MILLISECONDS.toMinutes(diff)
                    } else null
                }

                val avgMinutes = durationsInMinutes.average().toInt()
                val avgHours = avgMinutes / 60
                val avgRemainder = avgMinutes % 60

                val summaryText = StringBuilder().apply {
                    append("Average sleeping hours\n in last ${durationsInMinutes.size} days: ")
                    append("${avgHours}h ${avgRemainder}m\n")

                    when {
                        avgMinutes < 420 -> { // mniej niż 7h
                            val diff = 420 - avgMinutes
                            append("It's too low about ${diff / 60}h ${diff % 60}m")
                        }
                        avgMinutes > 540 -> { // więcej niż 9h
                            val diff = avgMinutes - 540
                            append("It's too high about ${diff / 60}h ${diff % 60}m")
                        }
                        else -> {
                            append("Good job! Perfect average sleeping time!")
                        }
                    }
                }.toString()

                findViewById<TextView>(R.id.sleepSummaryText).text = summaryText

            } catch (e: Exception) {
                findViewById<TextView>(R.id.sleepSummaryText).text = "Error in reading sleep ${e.message}"
            }
        }
    }
    override fun onResume() {
        super.onResume()
        loadSleepSummary()
    }

}

