package com.cse.shutterbook

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class PopularPhotographerAdapter(
    private val context: Context,
    private val photographerList: List<Photographer>
) : RecyclerView.Adapter<PopularPhotographerAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val photographerImage: ImageView = itemView.findViewById(R.id.photographerImageView)
        val photographerName: TextView = itemView.findViewById(R.id.photographerNameTextView)
        val photographerCategory: TextView = itemView.findViewById(R.id.photographerCategoryTextView)
        val starIcon: ImageView = itemView.findViewById(R.id.starIcon)
        val averageRating: TextView = itemView.findViewById(R.id.averageRatingTextView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context)
            .inflate(R.layout.item_popular_photographer, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = photographerList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val photographer = photographerList[position]

        holder.photographerName.text = photographer.name ?: "Unknown"

        val category = when {
            !photographer.weddingCost.isNullOrEmpty() -> "Wedding"
            !photographer.birthdayCost.isNullOrEmpty() -> "Birthday"
            !photographer.outdoorCost.isNullOrEmpty() -> "Outdoor"
            !photographer.eventCost.isNullOrEmpty() -> "Event"
            else -> "General"
        }
        holder.photographerCategory.text = category


        holder.photographerImage.setImageResource(R.drawable.default_user)


        val avgRating = photographer.averageRating ?: 0.0
        holder.averageRating.text = String.format("%.1f", avgRating)

        if (avgRating <= 0.0) {
            holder.starIcon.visibility = View.GONE
            holder.averageRating.visibility = View.GONE
        } else {
            holder.starIcon.visibility = View.VISIBLE
            holder.averageRating.visibility = View.VISIBLE
        }

        holder.itemView.setOnClickListener {
            val intent = Intent(context, PhotographerProfileActivity::class.java)
            intent.putExtra("photographerId", photographer.id)
            intent.putExtra("photographerName", photographer.name)
            intent.putExtra("selectedCategory", category)
            context.startActivity(intent)
        }
    }
}