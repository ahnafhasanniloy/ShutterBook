package com.cse.shutterbook

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class PhotographerLandingPageActivity : AppCompatActivity() {

    private val notificationFragment = PhotographerNotificationFragment()
    private lateinit var databaseReference: DatabaseReference
    private val currentUser = FirebaseAuth.getInstance().currentUser

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_photographer_landing_page)

        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNavigationView)

        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, notificationFragment)
            .commit()

        bottomNav.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.nav_notification -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.fragmentContainer, notificationFragment)
                        .commit()
                    true
                }
                R.id.nav_settings -> {
                    openPhotographerSettings()
                    true
                }
                else -> false
            }
        }
    }

    private fun openPhotographerSettings() {
        val uid = currentUser?.uid ?: return
        databaseReference = FirebaseDatabase.getInstance().getReference("Photographers").child(uid)

        databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val name = snapshot.child("name").value?.toString() ?: "Photographer"
                val email = snapshot.child("email").value?.toString() ?: "photographer@example.com"

                val intent = Intent(this@PhotographerLandingPageActivity, PhotographerSettingsActivity::class.java)
                intent.putExtra("name", name)
                intent.putExtra("email", email)
                startActivity(intent)
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
    }
}