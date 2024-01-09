package com.pushkin.weather_app_backend.configuration

import com.pushkin.weather_app_backend.cache.service.EntityCacheConfiguration
import com.pushkin.weather_app_backend.weather.entity.GeoDataCached
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.bind.ConstructorBinding

@ConfigurationProperties(prefix = "geodata.cache")
data class GeoDataCacheConfiguration @ConstructorBinding constructor(
    override val ttlSeconds: Long
) : EntityCacheConfiguration<GeoDataCached>(ttlSeconds)
