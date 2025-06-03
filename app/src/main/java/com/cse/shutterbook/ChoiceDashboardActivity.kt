package com.cse.shutterbook

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.Button

class ChoiceDashboardActivity : AppCompatActivity() {

    private lateinit var btnSignupPhotographer: Button
    private lateinit var btnSignupClient: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_choice_dashboard)

        btnSignupPhotographer = findViewById(R.id.btnSignupPhotographer)
        btnSignupClient = findViewById(R.id.btnSignupClient)
        btnSignupPhotographer.setOnClickListener {
            val intent = Intent(this, PhotographerSignupActivity::class.java)
            startActivity(intent)
        }

        btnSignupClient.setOnClickListener {
            val intent = Intent(this, ClientSignupActivity::class.java)
            startActivity(intent)
        }

    }
}