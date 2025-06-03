package com.cse.shutterbook

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class PhotographerSettingsActivity : AppCompatActivity() {

    private lateinit var photographerName: TextView
    private lateinit var photographerEmail: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_photographer_settings)

        photographerName = findViewById(R.id.photographer_name)
        photographerEmail = findViewById(R.id.photographer_email)

        val name = intent.getStringExtra("name") ?: "Photographer Name"
        val email = intent.getStringExtra("email") ?: "photographer@example.com"

        photographerName.text = name
        photographerEmail.text = email

        setupOption(findViewById(R.id.btn_update_profile), "Update Profile Information", R.drawable.ic_update_profile) {
            startActivity(Intent(this, UpdatePhotographerProfileActivity::class.java))
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