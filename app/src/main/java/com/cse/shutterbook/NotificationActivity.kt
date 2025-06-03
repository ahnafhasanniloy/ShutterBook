package com.cse.shutterbook

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class NotificationActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: NotificationAdapter
    private lateinit var bookingList: MutableList<Booking>
    private lateinit var bookingsRef: DatabaseReference
    private lateinit var seenRef: DatabaseReference
    private lateinit var currentUserId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notification)

        recyclerView = findViewById(R.id.notificationRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        bookingList = mutableListOf()
        adapter = NotificationAdapter(bookingList)
        recyclerView.adapter = adapter

        currentUserId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
        bookingsRef = FirebaseDatabase.getInstance().getReference("Bookings")
        seenRef = FirebaseDatabase.getInstance().getReference("SeenNotifications").child(currentUserId)

        loadNotifications()
    }

    private fun loadNotifications() {
        bookingsRef.orderByChild("userId").equalTo(currentUserId)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    bookingList.clear()
                    val updates = mutableMapOf<String, Any?>()

                    for (data in snapshot.children) {
                        val booking = data.getValue(Booking::class.java)
                        if (booking != null && (booking.status == "confirmed" || booking.status == "denied")) {
                            bookingList.add(booking)
                            updates[booking.bookingId] = true
                        }
                    }


                    bookingList.sortWith(compareByDescending { it.timestamp ?: 0L })

                    seenRef.updateChildren(updates)
                    adapter.notifyDataSetChanged()
                }

                override fun onCancelled(error: DatabaseError) {}
            })
    }
}