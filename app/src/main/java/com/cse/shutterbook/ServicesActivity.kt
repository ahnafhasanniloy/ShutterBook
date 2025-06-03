package com.cse.shutterbook

import android.content.Intent
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.badge.BadgeDrawable
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class ServicesActivity : AppCompatActivity() {

    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var weddingCategory: LinearLayout
    private lateinit var birthdayCategory: LinearLayout
    private lateinit var outdoorCategory: LinearLayout
    private lateinit var eventCategory: LinearLayout
    private lateinit var usernameTextView: TextView
    private lateinit var bottomNavigationView: BottomNavigationView

    private lateinit var popularRecyclerView: RecyclerView
    private lateinit var popularAdapter: PopularPhotographerAdapter
    private val popularPhotographersList = ArrayList<Photographer>()

    private lateinit var photographersDbRef: DatabaseReference
    private lateinit var clientsDbRef: DatabaseReference
    private lateinit var bookingsDbRef: DatabaseReference
    private lateinit var seenRef: DatabaseReference
    private var currentUserId: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_services)


        firebaseAuth = FirebaseAuth.getInstance()
        photographersDbRef = FirebaseDatabase.getInstance().getReference("Photographers")
        clientsDbRef = FirebaseDatabase.getInstance().getReference("clients")
        bookingsDbRef = FirebaseDatabase.getInstance().getReference("Bookings")
        currentUserId = firebaseAuth.currentUser?.uid ?: ""
        seenRef = FirebaseDatabase.getInstance().getReference("SeenNotifications").child(currentUserId)


        usernameTextView = findViewById(R.id.usernameTextView)
        bottomNavigationView = findViewById(R.id.bottomNavigationView)
        weddingCategory = findViewById(R.id.weddingCategory)
        birthdayCategory = findViewById(R.id.birthdayCategory)
        outdoorCategory = findViewById(R.id.outdoorCategory)
        eventCategory = findViewById(R.id.eventCategory)

        popularRecyclerView = findViewById(R.id.popularPhotographersRecyclerView)
        popularRecyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        popularAdapter = PopularPhotographerAdapter(this, popularPhotographersList)
        popularRecyclerView.adapter = popularAdapter


        currentUserId.let {
            clientsDbRef.child(it).get().addOnSuccessListener { snapshot ->
                val name = snapshot.child("name").value?.toString()
                usernameTextView.text = name ?: "User"
            }
        }

        loadPopularPhotographers()
        setupBottomNavigation()
        setupBadgeListener()


        weddingCategory.setOnClickListener { openCategory("wedding") }
        birthdayCategory.setOnClickListener { openCategory("birthday") }
        outdoorCategory.setOnClickListener { openCategory("outdoor") }
        eventCategory.setOnClickListener { openCategory("event") }
    }

    private fun loadPopularPhotographers() {
        photographersDbRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                popularPhotographersList.clear()
                for (photographerSnap in snapshot.children) {
                    val photographer = photographerSnap.getValue(Photographer::class.java)
                    if (photographer != null) {
                        popularPhotographersList.add(photographer)
                    }
                }
                popularAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun setupBottomNavigation() {
        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> true

                R.id.nav_notification -> {
                    bottomNavigationView.getBadge(R.id.nav_notification)?.isVisible = false
                    seenRef.removeValue()
                    startActivity(Intent(this, NotificationActivity::class.java))
                    true
                }

                R.id.nav_setting -> {
                    val email = firebaseAuth.currentUser?.email ?: "client@example.com"
                    clientsDbRef.child(currentUserId).get().addOnSuccessListener { snapshot ->
                        val name = snapshot.child("name").value?.toString() ?: "Client"
                        val intent = Intent(this, ClientSettingsActivity::class.java)
                        intent.putExtra("name", name)
                        intent.putExtra("email", email)
                        startActivity(intent)
                    }
                    true
                }

                else -> false
            }
        }
    }

    private fun setupBadgeListener() {
        bookingsDbRef.orderByChild("userId").equalTo(currentUserId)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(bookingSnapshot: DataSnapshot) {
                    seenRef.get().addOnSuccessListener { seenSnapshot ->
                        var unseenCount = 0

                        for (data in bookingSnapshot.children) {
                            val booking = data.getValue(Booking::class.java)
                            if (booking != null && (booking.status == "confirmed" || booking.status == "denied")) {
                                val isSeen = seenSnapshot.hasChild(booking.bookingId)
                                if (!isSeen) {
                                    unseenCount++
                                }
                            }
                        }

                        val badge: BadgeDrawable = bottomNavigationView.getOrCreateBadge(R.id.nav_notification)
                        if (unseenCount > 0) {
                            badge.isVisible = true
                            badge.number = unseenCount
                        } else {
                            badge.isVisible = false
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {}
            })
    }

    private fun openCategory(category: String) {
        val intent = Intent(this, PhotographerListActivity::class.java)
        intent.putExtra("category", category)
        startActivity(intent)
    }
}