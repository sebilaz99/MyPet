package com.example.mypet.model

data class Medication(
    val brand: String = "",
    val date: String = "",
    val exp: String = "",
    val type: String = ""
)

enum class MedicationType {
    Internal, External, Other
}

