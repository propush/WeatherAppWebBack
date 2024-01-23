package com.pushkin.weather_app_backend.user.vo

import com.pushkin.weather_app_backend.user.entity.TokenType

data class ExchangeTokenRq(
    val externalToken: String,
    val externalTokenType: TokenType
)
