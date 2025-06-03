package com.cse.shutterbook

import android.content.SharedPreferences
import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class PhotographerNotificationFragment : Fragment() {

    private lateinit var bookingRecyclerView: RecyclerView
    private lateinit var bookingAdapter: BookingAdapter
    private lateinit var searchView: SearchView

    private val bookingList = mutableListOf<Booking>()
    private val filteredList = mutableListOf<Booking>()

    private val bookingsRef = FirebaseDatabase.getInstance().getReference("Bookings")
    private val clientsRef = FirebaseDatabase.getInstance().getReference("clients")

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var photographerId: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_photographer_notification, container, false)

        bookingRecyclerView = view.findViewById(R.id.bookingRecyclerView)
        bookingRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        searchView = view.findViewById(R.id.searchView)

        bookingAdapter = BookingAdapter(filteredList) { booking ->
            showBookingDetails(booking)
        }

        bookingRecyclerView.adapter = bookingAdapter

        sharedPreferences = requireContext().getSharedPreferences("ShutterPrefs", Context.MODE_PRIVATE)
        photographerId = FirebaseAuth.getInstance().currentUser?.uid ?: return view

        loadBookings()
        showNewReviewAlertOnce()

        setupSearchView()

        return view
    }

    private fun loadBookings() {
        bookingsRef.orderByChild("photographerId").equalTo(photographerId)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    bookingList.clear()
                    for (bookingSnap in snapshot.children) {
                        val booking = bookingSnap.getValue(Booking::class.java)
                        if (booking != null) {
                            bookingList.add(0, booking)
                        }
                    }
                    filterBookings(searchView.query.toString())
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(context, "Failed to load bookings", Toast.LENGTH_SHORT).show()
                }
            })
    }

    private fun setupSearchView() {
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                filterBookings(query ?: "")
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                filterBookings(newText ?: "")
                return true
            }
        })
    }

    private fun filterBookings(query: String) {
        val lowerQuery = query.lowercase().trim()
        filteredList.clear()

        if (lowerQuery.isEmpty()) {
            filteredList.addAll(bookingList)
        } else {
            for (booking in bookingList) {
                if (booking.date.lowercase().contains(lowerQuery)) {
                    filteredList.add(booking)
                }
            }
        }
        bookingAdapter.notifyDataSetChanged()
    }

    private fun showBookingDetails(booking: Booking) {
        clientsRef.child(booking.userId)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val user = snapshot.getValue(User::class.java)
                    if (user != null) {
                        val message = """
                            Name: ${user.name}
                            Email: ${user.email}
                            Phone: ${user.phone}
                            Address: ${user.address}
                            Date: ${booking.date}
                            Time: ${booking.time}
                            Category: ${booking.category}
                            Status: ${booking.status}
                        """.trimIndent()

                        AlertDialog.Builder(requireContext())
                            .setTitle("Booking Details")
                            .setMessage(message)
                            .setPositiveButton("Confirm") { _, _ ->
                                updateBookingStatus(booking.bookingId, "confirmed")
                            }
                            .setNegativeButton("Deny") { _, _ ->
                                updateBookingStatus(booking.bookingId, "denied")
                            }
                            .setNeutralButton("Close", null)
                            .show()
                    } else {
                        Toast.makeText(context, "Client not found", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(context, "Failed to fetch client info", Toast.LENGTH_SHORT).show()
                }
            })
    }

    private fun updateBookingStatus(bookingId: String, status: String) {
        bookingsRef.child(bookingId).child("status").setValue(status)
            .addOnSuccessListener {
                Toast.makeText(context, "Booking $status", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(context, "Failed to update status", Toast.LENGTH_SHORT).show()
            }
    }

    private fun showNewReviewAlertOnce() {
        val key = "review_shown_$photographerId"
        val hasSeenReview = sharedPreferences.getBoolean(key, false)

        if (!hasSeenReview) {
            val ratingsRef = FirebaseDatabase.getInstance().getReference("Ratings").child(photographerId)

            ratingsRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (reviewSnap in snapshot.children) {
                        val userId = reviewSnap.key ?: continue
                        val rating = reviewSnap.child("rating").getValue(Double::class.java) ?: continue
                        val reviewText = reviewSnap.child("review").getValue(String::class.java) ?: ""

                        if (reviewText.isNotEmpty()) {
                            clientsRef.child(userId).addListenerForSingleValueEvent(object : ValueEventListener {
                                override fun onDataChange(clientSnapshot: DataSnapshot) {
                                    val user = clientSnapshot.getValue(User::class.java)
                                    if (user != null) {
                                        val message = """
                                            ✨ New Review Received ✨
                                            
                                            Name: ${user.name}
                                            Rating: $rating
                                            Review: $reviewText
                                        """.trimIndent()

                                        AlertDialog.Builder(requireContext())
                                            .setTitle("Client Review")
                                            .setMessage(message)
                                            .setPositiveButton("OK") { _, _ ->
                                                sharedPreferences.edit().putBoolean(key, true).apply()
                                            }
                                            .setCancelable(false)
                                            .show()
                                    }
                                }

                                override fun onCancelled(error: DatabaseError) {}
                            })

                            break
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(context, "Failed to load reviews", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }
}