package com.pushkin.weather_app_backend.weather.vo

data class UserWeatherResponse(
    val login: String,
    val weatherResponseMap: Map<String, WeatherResponse>
)
