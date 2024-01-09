package com.pushkin.weather_app_backend.user.vo

data class SignUpRq(
    val login: String,
    val password: String,
    val email: String
)
