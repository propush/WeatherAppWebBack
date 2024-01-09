package com.pushkin.weather_app_backend

import com.pushkin.weather_app_backend.configuration.GeoDataCacheConfiguration
import com.pushkin.weather_app_backend.configuration.OpenWeatherMapConfiguration
import com.pushkin.weather_app_backend.configuration.PwdUserAuthenticationProviderConfiguration
import com.pushkin.weather_app_backend.configuration.WeatherCacheConfiguration
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication

@SpringBootApplication
@EnableConfigurationProperties(
    PwdUserAuthenticationProviderConfiguration::class,
    OpenWeatherMapConfiguration::class,
    WeatherCacheConfiguration::class,
    GeoDataCacheConfiguration::class
)
class WeatherAppBackendApplication

fun main(args: Array<String>) {
    runApplication<WeatherAppBackendApplication>(*args)
}
