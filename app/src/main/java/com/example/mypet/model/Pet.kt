package com.example.mypet.model

data class Pet(
    val id: String,
    val name: String,
    val sex: Sex,
    val age: Int,
    val dateOfBirth: String,
    val species: Species,
    val breed: String,
    val colour: Colour,
    // ???
    val photo : String
)

enum class Species {
    Cat, Dog, Fish, Bird, Exotic, Rodent
}

enum class Colour {
    BLACK, BROWN, BLONDE, WHITE, MULTI, YELLOW, BLUE, RED, GREEN, GREY
}

enum class Sex {
    M, F
}

