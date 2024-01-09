package com.pushkin.weather_app_backend

import com.pushkin.weather_app_backend.weather.vo.*

fun mockWeatherResponse(name: String) = WeatherResponse(
    coord = Coord(1.0, 2.0),
    weather = listOf(Weather(3, "m1", "d1", "i1")),
    base = "b1",
    main = Main(4.0, 5.0, 6.0, 7.0, 8, 9),
    visibility = 10,
    wind = Wind(11.0, 12, 13.0),
    clouds = Clouds(14),
    dt = 1234567890,
    sys = Sys(1, 2, "C1", 150, 16),
    timezone = 1,
    id = 100,
    name = name,
    cod = 200
)
