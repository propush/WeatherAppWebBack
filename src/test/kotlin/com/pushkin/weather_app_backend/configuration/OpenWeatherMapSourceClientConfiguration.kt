package com.pushkin.weather_app_backend.configuration

import com.pushkin.weather_app_backend.weather.client.WeatherSourceClient
import io.github.oshai.kotlinlogging.KotlinLogging
import org.mockito.kotlin.mock
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile

private val logger = KotlinLogging.logger {}

@Configuration
@Profile("test")
class OpenWeatherMapSourceClientConfiguration {

    @Bean
    fun openWeatherMapSourceClient(): WeatherSourceClient {
        logger.info { "mock<WeatherSourceClient> created" }
        return mock<WeatherSourceClient> {}
    }

}
