package com.pushkin.weather_app_backend.configuration

import com.pushkin.weather_app_backend.cache.service.EntityCacheConfiguration
import com.pushkin.weather_app_backend.weather.entity.WeatherCached
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.bind.ConstructorBinding

@ConfigurationProperties(prefix = "weather.cache")
data class WeatherCacheConfiguration @ConstructorBinding constructor(
    override val ttlSeconds: Long
) : EntityCacheConfiguration<WeatherCached>(ttlSeconds)
