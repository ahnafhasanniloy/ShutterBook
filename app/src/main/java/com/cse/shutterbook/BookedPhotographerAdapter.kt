package com.cse.shutterbook

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class BookedPhotographerAdapter(
    private val list: List<Pair<Booking, Photographer>>
) : RecyclerView.Adapter<BookedPhotographerAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvPhotographerName = itemView.findViewById<TextView>(R.id.tvPhotographerName)
        val tvBookingDate = itemView.findViewById<TextView>(R.id.tvBookingDate)
        val tvBookingStatus = itemView.findViewById<TextView>(R.id.tvBookingStatus)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_booked_photographer, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount() = list.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val (booking, photographer) = list[position]
        holder.tvPhotographerName.text = photographer.name ?: "Unknown"
        holder.tvBookingDate.text = "Date: ${booking.date ?: "N/A"}"
        holder.tvBookingStatus.text = "Status: ${booking.status ?: "N/A"}"
    }
}