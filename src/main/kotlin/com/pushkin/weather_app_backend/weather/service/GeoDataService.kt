package com.pushkin.weather_app_backend.weather.service

import com.pushkin.weather_app_backend.cache.service.BaseEntityCachingServiceImpl
import com.pushkin.weather_app_backend.cache.service.EntityCacheConfiguration
import com.pushkin.weather_app_backend.lock.service.LockService
import com.pushkin.weather_app_backend.weather.client.WeatherSourceClient
import com.pushkin.weather_app_backend.weather.entity.GeoDataCached
import com.pushkin.weather_app_backend.weather.repository.GeoDataCachedRepository
import com.pushkin.weather_app_backend.weather.vo.GeoRequest
import com.pushkin.weather_app_backend.weather.vo.GeoResponse
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Service

private val logger = KotlinLogging.logger {}

@Service
class GeoDataService(
    private val weatherSourceClient: WeatherSourceClient,
    geoDataCachedRepository: GeoDataCachedRepository,
    geoDataCacheConfiguration: EntityCacheConfiguration<GeoDataCached>,
    lockService: LockService
) : BaseEntityCachingServiceImpl<GeoDataCached, String, GeoDataCachedRepository>(
    geoDataCachedRepository,
    geoDataCacheConfiguration,
    lockService
) {

    fun getGeoData(location: String): Set<GeoResponse> {
        logger.info { "getGeoData: location=$location" }
        val geoDataCached = getEntity(location) {
            toGeoDataCached(location, weatherSourceClient.fetchGeoLocation(GeoRequest(it)))
        }
        return geoDataCached.geoResponses
    }

    fun toGeoDataCached(location: String, geoResponses: Set<GeoResponse>): GeoDataCached =
        GeoDataCached(location, geoResponses)

    override fun findInRepository(key: String): GeoDataCached? =
        repository.findByLocation(key)

}
