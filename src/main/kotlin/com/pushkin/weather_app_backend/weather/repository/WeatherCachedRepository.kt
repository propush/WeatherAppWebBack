package com.pushkin.weather_app_backend.weather.repository

import com.pushkin.weather_app_backend.cache.repository.CacheableRepository
import com.pushkin.weather_app_backend.weather.entity.WeatherCached
import org.springframework.stereotype.Repository

@Repository
interface WeatherCachedRepository : CacheableRepository<WeatherCached> {

    fun findByLocation(location: String): WeatherCached?

}
