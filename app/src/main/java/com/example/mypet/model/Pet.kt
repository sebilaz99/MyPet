package com.example.mypet.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

data class Pet(
    val id: String,
    val name: String,
    val sex: String,
    val dateOfBirth: String,
    val species: String,
    val breed: String,
    val colour: String,
    // ???
    val photo : String,
    val score: Int,
    val owner: Owner
)

@Parcelize
enum class Species : Parcelable {
    Cat, Dog, Fish, Bird, Exotic, Rodent
}

@Parcelize
enum class Colour : Parcelable {
    Black, Brown, Blonde, White, Multi, Yellow, Blue, Red, Green, Grey
}

@Parcelize
enum class Sex : Parcelable {
    M, F
}

