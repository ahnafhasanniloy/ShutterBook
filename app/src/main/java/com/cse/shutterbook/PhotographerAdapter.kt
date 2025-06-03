package com.cse.shutterbook

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*

class PhotographerAdapter(
    private val context: Context,
    private val photographerList: List<PhotographerData>,
    private val selectedCategory: String
) : RecyclerView.Adapter<PhotographerAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nameText: TextView = itemView.findViewById(R.id.nameTextView)
        val addressText: TextView = itemView.findViewById(R.id.addressTextView)
        val experienceText: TextView = itemView.findViewById(R.id.experienceTextView)
        val ratingText: TextView = itemView.findViewById(R.id.ratingTextView)
        val starIcon: ImageView = itemView.findViewById(R.id.starIconView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.photographer_list_item, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = photographerList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val photographer = photographerList[position]

        holder.nameText.text = photographer.name
        holder.addressText.text = "Location: ${photographer.address}"
        holder.experienceText.text = "Experience: ${photographer.experience} years"

        fetchAndDisplayRating(photographer.id ?: "", holder.ratingText)

        holder.itemView.setOnClickListener {
            val intent = Intent(context, PhotographerProfileActivity::class.java)
            intent.putExtra("photographer", photographer)
            intent.putExtra("selectedCategory", selectedCategory)
            context.startActivity(intent)
        }
    }

    private fun fetchAndDisplayRating(photographerId: String, ratingTextView: TextView) {
        val ratingRef = FirebaseDatabase.getInstance().getReference("Ratings").child(photographerId)
        ratingRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                var totalRating = 0.0
                var count = 0
                for (ratingSnap in snapshot.children) {
                    val rating = ratingSnap.child("rating").getValue(Double::class.java)
                    if (rating != null) {
                        totalRating += rating
                        count++
                    }
                }
                if (count > 0) {
                    val avg = totalRating / count
                    ratingTextView.text = String.format(" %.1f", avg)
                } else {
                    ratingTextView.text = "N/A"
                }
            }

            override fun onCancelled(error: DatabaseError) {
                ratingTextView.text = " N/A"
            }
        })
    }
}