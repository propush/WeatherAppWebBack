package com.pushkin.weather_app_backend.weather.service

import com.pushkin.weather_app_backend.weather.vo.*
import org.springframework.stereotype.Service

@Service
class WeatherService {

    fun getWeather(location: String): WeatherResponseVO {
        return WeatherResponseVO(
            coord = Coord(0.0, 0.0),
            weather = listOf(Weather(0, "", "", "")),
            base = "",
            main = Main(0.0, 0.0, 0.0, 0.0, 0, 0),
            visibility = 0,
            wind = Wind(0.0, 0, 0.0),
            clouds = Clouds(0),
            dt = 0,
            sys = Sys(0, 0, "", 0, 0),
            timezone = 0,
            id = 0,
            name = "",
            cod = 0
        )
    }

}
