package com.example.mypet.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

data class Pet(
    val id: String,
    val name: String,
    val sex: Sex,
    val dateOfBirth: String,
    val species: Species,
    val breed: String,
    val colour: Colour,
    // ???
    val photo : String,
    val score: Int
)

@Parcelize
enum class Species : Parcelable {
    Cat, Dog, Fish, Bird, Exotic, Rodent
}

@Parcelize
enum class Colour : Parcelable {
    BLACK, BROWN, BLONDE, WHITE, MULTI, YELLOW, BLUE, RED, GREEN, GREY
}

@Parcelize
enum class Sex : Parcelable {
    M, F
}

