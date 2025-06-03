package com.cse.shutterbook

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class PhotographerProfileActivity : AppCompatActivity() {

    private lateinit var nameTextView: TextView
    private lateinit var experienceTextView: TextView
    private lateinit var addressTextView: TextView
    private lateinit var emailTextView: TextView
    private lateinit var phoneTextView: TextView
    private lateinit var aboutMeTextView: TextView
    private lateinit var categoryTextView: TextView
    private lateinit var categoryCostTextView: TextView
    private lateinit var categoryHoursTextView: TextView
    private lateinit var bookNowButton: Button

    private lateinit var ratingBar: RatingBar
    private lateinit var reviewEditText: EditText
    private lateinit var submitRatingButton: Button
    private lateinit var averageRatingTextView: TextView

    private lateinit var database: DatabaseReference
    private val currentUserId = FirebaseAuth.getInstance().currentUser?.uid

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_photographer_profile)

        nameTextView = findViewById(R.id.nameTextView)
        experienceTextView = findViewById(R.id.experienceTextView)
        addressTextView = findViewById(R.id.addressTextView)
        emailTextView = findViewById(R.id.emailTextView)
        phoneTextView = findViewById(R.id.phoneTextView)
        aboutMeTextView = findViewById(R.id.aboutMeTextView)
        categoryTextView = findViewById(R.id.categoryTextView)
        categoryCostTextView = findViewById(R.id.categoryCostTextView)
        categoryHoursTextView = findViewById(R.id.categoryHoursTextView)
        bookNowButton = findViewById(R.id.bookNowButton)

        ratingBar = findViewById(R.id.ratingBar)
        reviewEditText = findViewById(R.id.reviewEditText)
        submitRatingButton = findViewById(R.id.submitRatingButton)
        averageRatingTextView = findViewById(R.id.averageRatingTextView)

        database = FirebaseDatabase.getInstance().reference

        val photographer = intent.getSerializableExtra("photographer") as? PhotographerData
        val selectedCategory = intent.getStringExtra("selectedCategory") ?: ""

        if (photographer != null) {
            nameTextView.text = photographer.name
            experienceTextView.text = "Experience: ${photographer.experience} years"
            addressTextView.text = "Location: ${photographer.address}"
            emailTextView.text = "Email: ${photographer.email}"
            phoneTextView.text = "Phone: ${photographer.phone}"
            aboutMeTextView.text = photographer.aboutMe
            categoryTextView.text = "Category: $selectedCategory"

            val cost = when (selectedCategory.lowercase()) {
                "wedding" -> photographer.weddingCost
                "birthday" -> photographer.birthdayCost
                "outdoor" -> photographer.outdoorCost
                "event" -> photographer.eventCost
                else -> null
            }

            val hours = when (selectedCategory.lowercase()) {
                "wedding" -> photographer.weddingHours
                "birthday" -> photographer.birthdayHours
                "outdoor" -> photographer.outdoorHours
                "event" -> photographer.eventHours
                else -> null
            }

            categoryCostTextView.text = "৳${cost ?: "N/A"}"
            categoryHoursTextView.text = "${hours ?: "N/A"} hours"

            fetchAverageRating(photographer.id)

            submitRatingButton.setOnClickListener {
                val rating = ratingBar.rating
                val review = reviewEditText.text.toString().trim()

                if (currentUserId != null && rating > 0) {
                    val ratingData = mapOf(
                        "rating" to rating,
                        "review" to review
                    )

                    database.child("Ratings").child(photographer.id).child(currentUserId)
                        .setValue(ratingData).addOnSuccessListener {
                            Toast.makeText(this, "Rating submitted!", Toast.LENGTH_SHORT).show()
                            calculateAndSaveAverage(photographer.id)
                        }.addOnFailureListener {
                            Toast.makeText(this, "Failed to submit rating", Toast.LENGTH_SHORT).show()
                        }
                } else {
                    Toast.makeText(this, "Please give a rating", Toast.LENGTH_SHORT).show()
                }
            }

            bookNowButton.setOnClickListener {
                if (photographer.id.isNullOrEmpty()) {
                    Toast.makeText(this, "Invalid photographer ID", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                val intent = Intent(this, BookingActivity::class.java)
                intent.putExtra("photographerId", photographer.id)
                intent.putExtra("photographerName", photographer.name)
                intent.putExtra("selectedCategory", selectedCategory)
                startActivity(intent)
            }

        } else {
            Toast.makeText(this, "Photographer data not found", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun fetchAverageRating(photographerId: String) {
        database.child("Photographers").child(photographerId).child("averageRating")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val avg = snapshot.getValue(Double::class.java)
                    averageRatingTextView.text = "Average Rating: ${String.format("%.1f", avg ?: 0.0)}"
                }

                override fun onCancelled(error: DatabaseError) {}
            })
    }

    private fun calculateAndSaveAverage(photographerId: String) {
        database.child("Ratings").child(photographerId)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    var total = 0.0
                    var count = 0

                    for (child in snapshot.children) {
                        val rating = child.child("rating").getValue(Double::class.java)
                        if (rating != null) {
                            total += rating
                            count++
                        }
                    }

                    if (count > 0) {
                        val average = total / count
                        database.child("Photographers").child(photographerId).child("averageRating")
                            .setValue(average)
                        averageRatingTextView.text = "Average Rating: ${String.format("%.1f", average)}"
                    }
                }

                override fun onCancelled(error: DatabaseError) {}
            })
    }
}