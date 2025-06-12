package com.example.sleepontrack_app
import android.app.TimePickerDialog
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

import java.util.Calendar

class ClockActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.sleep_activity)

        val selectTimeButton = findViewById<Button>(R.id.selectTimeButton)
        val selectedTimeText = findViewById<TextView>(R.id.selectedTimeText)

        val calendar = Calendar.getInstance()
        var hour = calendar.get(Calendar.HOUR_OF_DAY)
        var minute = calendar.get(Calendar.MINUTE)

        selectTimeButton.setOnClickListener {
            val timePickerDialog = TimePickerDialog(
                this,
                { _, selectedHour, selectedMinute ->
                    hour = selectedHour
                    minute = selectedMinute

                    val isPM = hour >= 12
                    val displayHour = if (hour == 0 || hour == 12) 12 else hour % 12
                    val amPm = if (isPM) "PM" else "AM"

                    val formattedTime = String.format("%02d:%02d %s", displayHour, minute, amPm)
                    selectedTimeText.text = "TIME: $formattedTime"
                },
                hour, minute, false
            )
            timePickerDialog.show()
        }
    }
}
