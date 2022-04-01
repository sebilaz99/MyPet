package com.example.mypet.model

data class Vaccine(
    val brand: String = "",
    val date: String = "",
    val exp: String = "",
    val type: String = ""
)

enum class VaccineType {
    Birth, Rabies, Other
}

