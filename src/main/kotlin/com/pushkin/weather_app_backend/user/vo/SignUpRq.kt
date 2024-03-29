package com.pushkin.weather_app_backend.user.vo

import com.pushkin.weather_app_backend.user.entity.TokenType

data class SignUpRq(
    val login: String,
    val password: String?,
    val tokenType: TokenType? = null
)
