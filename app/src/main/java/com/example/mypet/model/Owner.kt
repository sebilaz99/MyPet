package com.example.mypet.model

data class Owner(
    val email: String,
    val password: String,
    val status: String
)

enum class UserStatus {
    ONLINE, OFFLINE
}
