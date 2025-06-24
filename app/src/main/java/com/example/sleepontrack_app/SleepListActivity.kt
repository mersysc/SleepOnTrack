package com.example.sleepontrack_app

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.sleepontrack_app.firestore.FirestoreManagement
import com.example.sleepontrack_app.firestore.SleepSession
import kotlinx.coroutines.*
import com.google.firebase.auth.FirebaseAuth
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.concurrent.TimeUnit

class SleepListActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: SleepSessionsAdapter
    private var userId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.sleep_history_activity)

        userId = FirebaseAuth.getInstance().currentUser?.uid

        recyclerView = findViewById(R.id.sleepRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        adapter = SleepSessionsAdapter(
            sessions = mutableListOf(),
            onDelete = { session -> deleteSession(session) },
            onEdit = { session -> editSession(session) },
            onDetails = { session -> showDetailsDialog(session) }
        )
        recyclerView.adapter = adapter

        loadSleepSessions()
    }

    private fun loadSleepSessions() {
        if (userId == null) return

        CoroutineScope(Dispatchers.Main).launch {
            try {
                val sessions = withContext(Dispatchers.IO) {
                    FirestoreManagement().loadAllSleepSessions(userId!!)
                }

                val displayItems = sessions.map { session ->
                    val duration = calculateDuration(session.sleepTime, session.wakeUpTime)
                    SleepItemDisplay(
                        date = session.date,
                        duration = duration,
                        rating = session.sleepQuality,
                        notes = session.notes,
                        sleepTime = session.sleepTime,
                        wakeTime = session.wakeUpTime
                    )
                }

                adapter.updateList(displayItems)
            } catch (e: Exception) {
                Toast.makeText(this@SleepListActivity, "Error loading sessions: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun calculateDuration(sleepTime: String?, wakeTime: String?): String {
        if (sleepTime.isNullOrBlank() || wakeTime.isNullOrBlank()) return "--"

        return try {
            val format = SimpleDateFormat("HH:mm", Locale.getDefault())

            val sleep = format.parse(sleepTime)
            val wake = format.parse(wakeTime)

            var diff = wake.time - sleep.time
            if (diff < 0) diff += 24 * 60 * 60 * 1000

            val hours = TimeUnit.MILLISECONDS.toHours(diff)
            val minutes = (TimeUnit.MILLISECONDS.toMinutes(diff) % 60)

            "${hours}h ${minutes}m"
        } catch (e: Exception) {
            "Error calculating"
        }
    }

    private fun deleteSession(session: SleepItemDisplay) {
        if (userId == null) return

        CoroutineScope(Dispatchers.Main).launch {
            try {
                withContext(Dispatchers.IO) {
                    FirestoreManagement().deleteSleepSession(userId!!, session.date)
                }
                adapter.removeItem(session)
                Toast.makeText(this@SleepListActivity, "Session deleted", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                Toast.makeText(this@SleepListActivity, "Delete failed: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }


    private fun editSession(session: SleepItemDisplay) {
        val intent = Intent(this, ClockActivity::class.java).apply {
            putExtra("sleepTime", session.sleepTime)
            putExtra("wakeTime", session.wakeTime)
            putExtra("rating", session.rating)
            putExtra("note", session.notes)
            putExtra("date", session.date)
            putExtra("editMode", true) // flagowanie trybu edycji
        }
        startActivity(intent)
    }


    private fun showDetailsDialog(session: SleepItemDisplay) {
        val message = """
            Date: ${session.date}
            Sleep Time: ${session.sleepTime}
            Wake Time: ${session.wakeTime}
            Duration: ${session.duration}
            Rating: ${"\u2B50".repeat(session.rating)}
            Notes: ${session.notes.ifBlank { "" }}
        """.trimIndent()

        androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle("Sleep Details")
            .setMessage(message)
            .setPositiveButton("OK", null)
            .show()
    }
}
