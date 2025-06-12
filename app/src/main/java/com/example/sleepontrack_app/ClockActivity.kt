package com.example.sleepontrack_app
import android.app.TimePickerDialog
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import java.util.Calendar
import java.util.Locale

class ClockActivity : AppCompatActivity() {
    private lateinit var selectedTimeText: TextView
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.sleep_activity)
        auth = FirebaseAuth.getInstance()
        val database = FirebaseDatabase.getInstance().reference

        val selectTimeButton = findViewById<Button>(R.id.selectTimeButton)
        selectedTimeText = findViewById(R.id.selectedTimeText)
        val confirmButton = findViewById<Button>(R.id.confirm_button_full)

        // Domyślna godzina
        val calendar = Calendar.getInstance()
        var hour = calendar.get(Calendar.HOUR_OF_DAY)
        var minute = calendar.get(Calendar.MINUTE)

        // Obsługa przycisku wyboru czasu
        selectTimeButton.setOnClickListener {
            val timePickerDialog = TimePickerDialog(
                this,
                { _, selectedHour, selectedMinute ->
                    hour = selectedHour
                    minute = selectedMinute

                    val isPM = hour >= 12
                    val displayHour = if (hour == 0 || hour == 12) 12 else hour % 12
                    val amPm = if (isPM) "PM" else "AM"

                    val formattedTime = String.format(Locale.getDefault(), "%02d:%02d %s", displayHour, minute, amPm)
                    selectedTimeText.text = "TIME: $formattedTime"
                },
                hour, minute, false
            )
            timePickerDialog.show()
        }

        // Obsługa przycisku Confirm
        confirmButton.setOnClickListener {
            val userId = auth.currentUser?.uid
            val timeText = selectedTimeText.text.toString()

            if (userId != null && timeText != "TIME: --:--") {
                val sleepData = mapOf(
                    "sleepTime" to timeText,
                    "timestamp" to System.currentTimeMillis()
                )

                database.child("users").child(userId).child("sleepEntries").push()
                    .setValue(sleepData)
                    .addOnSuccessListener {
                        Toast.makeText(this, "Sleep time saved!", Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener {
                        Toast.makeText(this, "Failed to save: ${it.message}", Toast.LENGTH_SHORT).show()
                    }
            } else {
                Toast.makeText(this, "Please select a time first", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
