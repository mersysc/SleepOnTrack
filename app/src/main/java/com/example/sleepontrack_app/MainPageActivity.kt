package com.example.sleepontrack_app

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import androidx.appcompat.widget.Toolbar
import androidx.appcompat.app.ActionBarDrawerToggle


class MainPageActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_drawer)

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
                    Toast.makeText(this, "Notifications soon", Toast.LENGTH_SHORT).show()
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

        // Twoje przyciski
        findViewById<Button>(R.id.logSleepButton).setOnClickListener {
            startActivity(Intent(this, ClockActivity::class.java))
        }

        findViewById<Button>(R.id.recent_logs_button).setOnClickListener {
            startActivity(Intent(this, SleepListActivity::class.java))
        }
    }
}