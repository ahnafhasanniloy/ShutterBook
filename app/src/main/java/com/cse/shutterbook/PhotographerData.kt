package com.cse.shutterbook

import java.io.Serializable

data class PhotographerData(
    val id: String = " ",
    val name: String = "",
    val phone: String = "",
    val email: String = "",
    val address: String = "",
    val experience: String = "",
    val aboutMe: String = "",
    val weddingCost: String? = null,
    val weddingHours: String? = null,
    val birthdayCost: String? = null,
    val birthdayHours: String? = null,
    val outdoorCost: String? = null,
    val outdoorHours: String? = null,
    val eventCost: String? = null,
    val eventHours: String? = null
) :Serializable