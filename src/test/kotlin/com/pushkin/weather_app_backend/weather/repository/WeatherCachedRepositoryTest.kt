package com.pushkin.weather_app_backend.weather.repository

import com.pushkin.weather_app_backend.mockWeatherResponse
import com.pushkin.weather_app_backend.weather.entity.WeatherCached
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit.jupiter.EnabledIf
import org.testcontainers.junit.jupiter.Testcontainers


@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@EnabledIf(value = "#{'\${spring.profiles.active}' == 'test'}", loadContext = true)
class WeatherCachedRepositoryTest {

    @Autowired
    private lateinit var weatherCachedRepository: WeatherCachedRepository

    @Test
    fun saveAndRestore() {
        val w1 = WeatherCached(
            "L1",
            mockWeatherResponse("L1n")
        )
        val saved1 = weatherCachedRepository.save(w1)
        val all = weatherCachedRepository.findAll()
        all.forEach { println(it) }
        assertEquals(1, all.size)
        assertEquals("L1", all[0].location)
        assertEquals("L1n", all[0].weatherResponse.name)
    }

}
