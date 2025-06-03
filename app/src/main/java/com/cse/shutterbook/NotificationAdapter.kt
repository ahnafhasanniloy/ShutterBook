package com.cse.shutterbook

import android.graphics.Color
import android.view.*
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class NotificationAdapter(
    private val list: List<Booking>
) : RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder>() {

    inner class NotificationViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val txtPhotographerName: TextView = view.findViewById(R.id.txtPhotographerName)
        val txtCategory: TextView = view.findViewById(R.id.txtCategory)
        val txtDateTime: TextView = view.findViewById(R.id.txtDateTime)
        val txtStatus: TextView = view.findViewById(R.id.txtStatus)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_notification, parent, false)
        return NotificationViewHolder(view)
    }

    override fun onBindViewHolder(holder: NotificationViewHolder, position: Int) {
        val booking = list[position]

        holder.txtPhotographerName.text = "Photographer: ${booking.photographerName}"
        holder.txtCategory.text = "Category: ${booking.category}"
        holder.txtDateTime.text = "Date: ${booking.date} at ${booking.time}"

        when (booking.status.lowercase()) {
            "confirmed" -> {
                holder.txtStatus.text = "Your booking was ACCEPTED by ${booking.photographerName}"
                holder.txtStatus.setTextColor(Color.parseColor("#2E7D32"))
            }
            "denied" -> {
                holder.txtStatus.text = "Your booking was DENIED by ${booking.photographerName}"
                holder.txtStatus.setTextColor(Color.parseColor("#B71C1C"))
            }
            else -> {
                holder.txtStatus.text = "Status: ${booking.status}"
                holder.txtStatus.setTextColor(Color.GRAY)
            }
        }
    }

    override fun getItemCount(): Int = list.size
}