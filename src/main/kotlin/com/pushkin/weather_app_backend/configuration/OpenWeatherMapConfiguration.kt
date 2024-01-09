package com.pushkin.weather_app_backend.configuration

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.bind.ConstructorBinding

@ConfigurationProperties(prefix = "openweathermap")
data class OpenWeatherMapConfiguration @ConstructorBinding constructor(
    val apiKey: String,
    val url: String,
    val limit: Int
)
