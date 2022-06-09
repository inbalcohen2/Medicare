package com.example.pillreminder.models

data class PillModel(
    val id : Int,
    val name: String,
    val image: String,
    val description: String,
    val date: String,
    val time :String,
    val latitude: Double,
    val longitude: Double
)
