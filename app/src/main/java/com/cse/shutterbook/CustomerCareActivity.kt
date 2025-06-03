package com.cse.shutterbook

import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Typeface
import android.net.Uri
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.style.StyleSpan
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class CustomerCareActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_customer_care)

        val faqTextView = findViewById<TextView>(R.id.faqTextView)

        val faqText = """
            🔹 How do I contact support?
            You can email us or call directly using the buttons below.

            🔹 How to manage my bookings?
            Go to 'My Bookings' tab from your dashboard.

            🔹 What if I forgot my password?
            Click on 'Forgot Password?' on login screen to reset it.
        """.trimIndent()

        val spannable = SpannableString(faqText)
        val questions = listOf(
            "🔹 How do I contact support?",
            "🔹 How to manage my bookings?",
            "🔹 What if I forgot my password?"
        )

        for (q in questions) {
            val start = faqText.indexOf(q)
            if (start != -1) {
                spannable.setSpan(
                    StyleSpan(Typeface.BOLD),
                    start,
                    start + q.length,
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                )
            }
        }

        faqTextView.text = spannable


        val emailButton = findViewById<Button>(R.id.emailButton)
        val callButton = findViewById<Button>(R.id.callButton)

        emailButton.setOnClickListener {
            val emailIntent = Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse("mailto:shutterbookofficial@gmail.com")
                putExtra(Intent.EXTRA_SUBJECT, "Customer Support")
                putExtra(Intent.EXTRA_TEXT, "Hi Shutterbook Support,\n\n")
            }
            try {
                startActivity(Intent.createChooser(emailIntent, "Send Email"))
            } catch (e: ActivityNotFoundException) {
                Toast.makeText(this, "No email app found", Toast.LENGTH_SHORT).show()
            }
        }

        callButton.setOnClickListener {
            val callIntent = Intent(Intent.ACTION_DIAL).apply {
                data = Uri.parse("tel:+8801982830464")
            }
            try {
                startActivity(callIntent)
            } catch (e: ActivityNotFoundException) {
                Toast.makeText(this, "No dialer app found", Toast.LENGTH_SHORT).show()
            }
        }
    }
}