package com.pushkin.weather_app_backend.weather.repository

import com.pushkin.weather_app_backend.cache.repository.CacheableRepository
import com.pushkin.weather_app_backend.weather.entity.GeoDataCached
import org.springframework.stereotype.Repository

@Repository
interface GeoDataCachedRepository : CacheableRepository<GeoDataCached> {

    fun findByLocation(location: String): GeoDataCached?

}
