package com.cse.shutterbook

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class BookingActivity : AppCompatActivity() {

    private lateinit var datePicker: DatePicker
    private lateinit var timePicker: TimePicker
    private lateinit var confirmBookingButton: Button

    private lateinit var photographerId: String
    private lateinit var photographerName: String
    private lateinit var selectedCategory: String
    private lateinit var userId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_booking)

        datePicker = findViewById(R.id.datePicker)
        timePicker = findViewById(R.id.timePicker)
        confirmBookingButton = findViewById(R.id.confirmBookingButton)

        timePicker.setIs24HourView(false)

        photographerId = intent.getStringExtra("photographerId") ?: ""
        photographerName = intent.getStringExtra("photographerName") ?: ""
        selectedCategory = intent.getStringExtra("selectedCategory") ?: ""

        userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""

        confirmBookingButton.setOnClickListener {
            val day = datePicker.dayOfMonth
            val month = datePicker.month + 1
            val year = datePicker.year
            val hour = if (android.os.Build.VERSION.SDK_INT >= 23) timePicker.hour else timePicker.currentHour
            val minute = if (android.os.Build.VERSION.SDK_INT >= 23) timePicker.minute else timePicker.currentMinute
            val amPm = if (hour >= 12) "PM" else "AM"
            val formattedHour = if (hour % 12 == 0) 12 else hour % 12
            val formattedTime = String.format("%02d:%02d %s", formattedHour, minute, amPm)
            val formattedDate = "$day/$month/$year"

            val bookingsRef = FirebaseDatabase.getInstance().getReference("Bookings")
            val bookingId = bookingsRef.push().key

            if (bookingId != null) {
                val bookingData = mapOf(
                    "bookingId" to bookingId,
                    "userId" to userId,
                    "photographerId" to photographerId,
                    "photographerName" to photographerName,
                    "category" to selectedCategory,
                    "date" to formattedDate,
                    "time" to formattedTime,
                    "status" to "pending",
                    "timestamp" to System.currentTimeMillis()
                )

                bookingsRef.child(bookingId).setValue(bookingData)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(this, "Booking Request sent!", Toast.LENGTH_SHORT).show()
                            finish()
                        } else {
                            Toast.makeText(this, "Booking failed. Try again.", Toast.LENGTH_SHORT).show()
                        }
                    }
            }
        }
    }
}