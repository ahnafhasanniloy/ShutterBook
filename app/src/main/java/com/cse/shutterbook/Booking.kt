package com.cse.shutterbook

data class Booking(
    val bookingId: String = "",
    val userId: String = "",
    val userName: String = "",
    val userPhone: String = "",
    val userAddress: String = "",
    val photographerId: String = "",
    val photographerName: String = "",
    val category: String = "",
    val date: String = "",
    val time: String = "",
    val status: String = "",
    val timestamp: Long = 0L
)

data class User(
    val name: String = "",
    val email: String = "",
    val phone: String = "",
    val address: String = ""
)