package com.cse.shutterbook

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class ClientSettingsActivity : AppCompatActivity() {

    private lateinit var clientName: TextView
    private lateinit var clientEmail: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_client_settings)

        clientName = findViewById(R.id.client_name)
        clientEmail = findViewById(R.id.client_email)

        val name = intent.getStringExtra("name") ?: "Client Name"
        val email = intent.getStringExtra("email") ?: "client@example.com"

        clientName.text = name
        clientEmail.text = email

        setupOption(findViewById(R.id.btn_update_profile), "Update Profile Information", R.drawable.ic_update_profile) {
            startActivity(Intent(this, UpdateProfileActivity::class.java))
        }

        setupOption(findViewById(R.id.btn_booked_photographers), "My Bookings", R.drawable.ic_camera) {
            startActivity(Intent(this, BookedPhotographersActivity::class.java))
        }

        setupOption(findViewById(R.id.btn_customer_care), "Customer Care Service", R.drawable.ic_customer_service) {
            startActivity(Intent(this, CustomerCareActivity::class.java))
        }

        findViewById<Button>(R.id.btn_sign_out).setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }

    private fun setupOption(view: View, text: String, iconResId: Int, onClick: () -> Unit) {
        view.findViewById<TextView>(R.id.option_text).text = text
        view.findViewById<ImageView>(R.id.option_icon).setImageResource(iconResId)
        view.setOnClickListener { onClick() }
    }
}