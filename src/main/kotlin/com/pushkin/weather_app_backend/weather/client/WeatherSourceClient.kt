package com.pushkin.weather_app_backend.weather.client

import com.pushkin.weather_app_backend.weather.exception.WeatherException
import com.pushkin.weather_app_backend.weather.exception.WeatherNotFoundException
import com.pushkin.weather_app_backend.weather.vo.GeoRequest
import com.pushkin.weather_app_backend.weather.vo.GeoResponse
import com.pushkin.weather_app_backend.weather.vo.WeatherRequest
import com.pushkin.weather_app_backend.weather.vo.WeatherResponse

interface WeatherSourceClient {

    @Throws(WeatherException::class, WeatherNotFoundException::class)
    fun fetchWeather(weatherRequest: WeatherRequest): WeatherResponse

    @Throws(WeatherException::class)
    fun fetchGeoLocation(geoRequest: GeoRequest): Set<GeoResponse>

}
