package com.pushkin.weather_app_backend.user.vo

data class ConfirmCodeRq(
    val login: String,
    val code: String
)
