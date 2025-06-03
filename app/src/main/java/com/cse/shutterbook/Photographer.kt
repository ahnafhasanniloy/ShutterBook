package com.cse.shutterbook

import java.io.Serializable

data class Photographer(

    val id: String? = null,
    val name: String? = null,
    val phone: String? = null,
    val email: String? = null,
    val address: String? = null,
    val experience: String? = null,
    val aboutMe: String? = null,

    val weddingCost: String? = null,
    val weddingHours: String? = null,

    val birthdayCost: String? = null,
    val birthdayHours: String? = null,

    val outdoorCost: String? = null,
    val outdoorHours: String? = null,

    val eventCost: String? = null,
    val eventHours: String? = null,

    val averageRating: Double? = null

):Serializable