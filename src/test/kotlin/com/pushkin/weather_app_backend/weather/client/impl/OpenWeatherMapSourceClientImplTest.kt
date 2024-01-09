package com.pushkin.weather_app_backend.weather.client.impl

import com.pushkin.weather_app_backend.weather.client.WeatherSourceClient
import com.pushkin.weather_app_backend.weather.exception.WeatherNotFoundException
import com.pushkin.weather_app_backend.weather.vo.GeoRequest
import com.pushkin.weather_app_backend.weather.vo.WeatherRequest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit.jupiter.EnabledIf

@SpringBootTest
@EnabledIf(value = "#{'\${spring.profiles.active}' == 'inttest'}", loadContext = true)
class OpenWeatherMapSourceClientImplTest {

    @Autowired
    private lateinit var weatherSourceClient: WeatherSourceClient

    @Test
    fun fetchWeather() {
        val weatherResponse = weatherSourceClient.fetchWeather(
            WeatherRequest("Moscow")
        )
        println(weatherResponse)
        assertEquals("Moscow", weatherResponse.name)
        assertEquals(200, weatherResponse.cod)
    }

    @Test
    fun fetchWeatherNonexistentLocation() {
        assertThrows<WeatherNotFoundException> {
            weatherSourceClient.fetchWeather(
                WeatherRequest("nonexistent")
            )
        }
    }

    @Test
    fun fetchGeoLocation() {
        val geoResponse = weatherSourceClient.fetchGeoLocation(
            GeoRequest("Moscow")
        )
        println(geoResponse)
        assertTrue(geoResponse.isNotEmpty())
        assertTrue(geoResponse.any { it.name == "Moscow" })
        assertTrue(geoResponse.any { it.country == "RU" })
    }

    @Test
    fun fetchGeoLocationNonexistent() {
        val geoResponse = weatherSourceClient.fetchGeoLocation(
            GeoRequest("Nonexistent")
        )
        println(geoResponse)
        assertTrue(geoResponse.isEmpty())
    }

}
