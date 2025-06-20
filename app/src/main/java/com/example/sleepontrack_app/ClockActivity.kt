package com.example.sleepontrack_app
import android.app.TimePickerDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.sleepontrack_app.firestore.FirestoreManagement
import com.example.sleepontrack_app.firestore.SleepSession
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class ClockActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var viewFlipper: ViewFlipper

    private var sleepTime: String? = null
    private var wakeTime: String? = null
    private var rating: Int = 0
    private var note: String = ""

    private var editMode: Boolean = false
    private var passedDate: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.sleep_activity)

        auth = FirebaseAuth.getInstance()
        viewFlipper = findViewById(R.id.viewFlipper)

        // Odczytaj dane z intencji (jeśli edycja)
        editMode = intent.getBooleanExtra("edit_mode", false)
        passedDate = intent.getStringExtra("date")
        val passedSleepTime = intent.getStringExtra("sleepTime")
        val passedWakeTime = intent.getStringExtra("wakeTime")
        val passedRating = intent.getIntExtra("rating", 0)
        val passedNotes = intent.getStringExtra("notes")

        val sleepTimeText = findViewById<TextView>(R.id.sleepTimeText)
        val wakeTimeText = findViewById<TextView>(R.id.wakeTimeText)
        val ratingBar = findViewById<RatingBar>(R.id.ratingBar)
        val noteInput = findViewById<EditText>(R.id.noteInput)

        // Jeśli edycja - wstaw dane do pól
        if (editMode) {
            sleepTime = passedSleepTime
            wakeTime = passedWakeTime
            rating = passedRating
            note = passedNotes ?: ""

            sleepTimeText.text = "Sleep Time: $sleepTime"
            wakeTimeText.text = "Wake Time: $wakeTime"
            ratingBar.rating = rating.toFloat()
            noteInput.setText(note)
        }

        findViewById<Button>(R.id.btnSelectSleepTime).setOnClickListener {
            showTimePicker { time ->
                sleepTime = time
                sleepTimeText.text = "Sleep Time: $time"
            }
        }

        findViewById<Button>(R.id.btnNext1).setOnClickListener {
            if (sleepTime != null) viewFlipper.showNext() else toast("Select sleep time")
        }
        findViewById<Button>(R.id.btnSkip1).setOnClickListener {
            finish()
        }

        findViewById<Button>(R.id.btnSelectWakeTime).setOnClickListener {
            showTimePicker { time ->
                wakeTime = time
                wakeTimeText.text = "Wake Time: $time"
            }
        }

        findViewById<Button>(R.id.btnNext2).setOnClickListener {
            if (wakeTime != null) viewFlipper.showNext() else toast("Select wake time")
        }
        findViewById<Button>(R.id.btnSkip2).setOnClickListener {
            finish()
        }

        findViewById<Button>(R.id.btnNext3).setOnClickListener {
            rating = ratingBar.rating.toInt()
            viewFlipper.showNext()
        }
        findViewById<Button>(R.id.btnSkip3).setOnClickListener {
            rating = 0
            viewFlipper.showNext()
        }

        findViewById<Button>(R.id.btnFinish).setOnClickListener {
            note = noteInput.text.toString()
            saveSession()
        }
        findViewById<Button>(R.id.btnSkip4).setOnClickListener {
            note = ""
            saveSession()
        }
    }

    private fun showTimePicker(onTimeSelected: (String) -> Unit) {
        val cal = Calendar.getInstance()
        val hour = cal.get(Calendar.HOUR_OF_DAY)
        val minute = cal.get(Calendar.MINUTE)
        TimePickerDialog(this, { _, h, m ->
            val time = String.format("%02d:%02d", h, m)
            onTimeSelected(time)
        }, hour, minute, true).show()
    }

    private fun saveSession() {
        val userId = auth.currentUser?.uid
        if (userId == null || sleepTime == null || wakeTime == null) {
            toast("Missing required data")
            return
        }

        val date = if (editMode && passedDate != null) passedDate!! else
            SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

        val session = SleepSession(
            sleepTime = sleepTime!!,
            wakeUpTime = wakeTime!!,
            notificationTime = "",
            sleepQuality = rating,
            notes = note,
            date = date
        )

        CoroutineScope(Dispatchers.IO).launch {
            try {
                FirestoreManagement().saveSleepSession(userId, session)
                runOnUiThread {
                    toast("Sleep session saved!")
                    val intent = Intent(this@ClockActivity, MainPageActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                    startActivity(intent)
                    finish()
                }
            } catch (e: Exception) {
                Log.e("SaveSession", "Error", e)
                runOnUiThread { toast("Failed to save session: ${e.message}") }
            }
        }
    }

    private fun toast(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }
}
