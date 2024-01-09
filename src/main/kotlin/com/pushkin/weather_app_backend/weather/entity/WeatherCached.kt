package com.pushkin.weather_app_backend.weather.entity

import com.pushkin.weather_app_backend.cache.entity.CacheableEntity
import com.pushkin.weather_app_backend.weather.vo.WeatherResponse
import org.springframework.data.mongodb.core.mapping.Document

@Document(collection = "weather")
class WeatherCached(
    val location: String,
    val weatherResponse: WeatherResponse,
) : CacheableEntity() {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is WeatherCached) return false
        if (!super.equals(other)) return false

        if (location != other.location) return false
        if (weatherResponse != other.weatherResponse) return false

        return true
    }

    override fun hashCode(): Int {
        var result = super.hashCode()
        result = 31 * result + location.hashCode()
        result = 31 * result + weatherResponse.hashCode()
        return result
    }

    override fun toString(): String {
        return "WeatherCached(location='$location', weatherResponse=$weatherResponse) ${super.toString()}"
    }

}
