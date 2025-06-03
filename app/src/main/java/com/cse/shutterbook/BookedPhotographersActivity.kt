package com.cse.shutterbook

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class BookedPhotographersActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: BookedPhotographerAdapter
    private val bookingPhotographerList = mutableListOf<Pair<Booking, Photographer>>()

    private val bookingsRef = FirebaseDatabase.getInstance().getReference("Bookings")
    private val photographersRef = FirebaseDatabase.getInstance().getReference("Photographers")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_booked_photographers)

        recyclerView = findViewById(R.id.recyclerBookedPhotographers)
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = BookedPhotographerAdapter(bookingPhotographerList)
        recyclerView.adapter = adapter

        loadBookings()
    }

    private fun loadBookings() {
        val clientId = FirebaseAuth.getInstance().currentUser?.uid
        if (clientId == null) {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show()
            return
        }

        bookingsRef.orderByChild("userId").equalTo(clientId)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (!snapshot.exists()) {
                        Toast.makeText(this@BookedPhotographersActivity, "No bookings found", Toast.LENGTH_SHORT).show()
                        return
                    }
                    bookingPhotographerList.clear()
                    val bookings = mutableListOf<Booking>()
                    val photographerIds = mutableSetOf<String>()

                    for (bookingSnap in snapshot.children) {
                        val booking = bookingSnap.getValue(Booking::class.java)
                        if (booking != null) {
                            bookings.add(booking)
                            booking.photographerId?.let { photographerIds.add(it) }
                        }
                    }

                    if (photographerIds.isEmpty()) {
                        Toast.makeText(this@BookedPhotographersActivity, "No photographers found", Toast.LENGTH_SHORT).show()
                        return
                    }

                    photographersRef.addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(photoSnap: DataSnapshot) {
                            val photographerMap = mutableMapOf<String, Photographer>()
                            for (pSnap in photoSnap.children) {
                                val id = pSnap.key ?: ""
                                val photographer = pSnap.getValue(Photographer::class.java)
                                if (photographer != null && photographerIds.contains(id)) {
                                    photographerMap[id] = photographer
                                }
                            }

                            bookingPhotographerList.clear()
                            for (booking in bookings) {
                                val photographer = booking.photographerId?.let { photographerMap[it] }
                                if (photographer != null) {
                                    bookingPhotographerList.add(Pair(booking, photographer))
                                }
                            }
                            adapter.notifyDataSetChanged()
                        }

                        override fun onCancelled(error: DatabaseError) {
                            Toast.makeText(this@BookedPhotographersActivity, "Failed to load photographers", Toast.LENGTH_SHORT).show()
                        }
                    })
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(this@BookedPhotographersActivity, "Failed to load bookings", Toast.LENGTH_SHORT).show()
                }
            })
    }
}