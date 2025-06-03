package com.cse.shutterbook

import android.graphics.Color
import android.view.*
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*

class BookingAdapter(
    private val bookingList: List<Booking>,
    private val onItemClick: (Booking) -> Unit
) : RecyclerView.Adapter<BookingAdapter.BookingViewHolder>() {

    inner class BookingViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val txtUserName: TextView = view.findViewById(R.id.txtUserName)
        val txtCategory: TextView = view.findViewById(R.id.txtCategory)
        val txtDateTime: TextView = view.findViewById(R.id.txtDateTime)
        val txtStatus: TextView = view.findViewById(R.id.txtStatus)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookingViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_booking, parent, false)
        return BookingViewHolder(view)
    }

    override fun onBindViewHolder(holder: BookingViewHolder, position: Int) {
        val booking = bookingList[position]

        val clientRef = FirebaseDatabase.getInstance().getReference("clients").child(booking.userId)
        clientRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val user = snapshot.getValue(User::class.java)
                holder.txtUserName.text = user?.name ?: "Unknown User"
            }

            override fun onCancelled(error: DatabaseError) {
                holder.txtUserName.text = "Unknown User"
            }
        })

        holder.txtCategory.text = "Category: ${booking.category.capitalize()}"
        holder.txtDateTime.text = "${booking.date} at ${booking.time}"
        holder.txtStatus.text = "Status: ${booking.status.capitalize()}"


        when (booking.status.lowercase()) {
            "pending" -> holder.txtStatus.setTextColor(Color.RED)
            "confirmed" -> holder.txtStatus.setTextColor(Color.parseColor("#4CAF50"))
            "denied" -> holder.txtStatus.setTextColor(Color.GRAY)
            else -> holder.txtStatus.setTextColor(Color.BLACK)
        }

        holder.itemView.setOnClickListener {
            onItemClick(booking)
        }
    }

    override fun getItemCount(): Int = bookingList.size
}