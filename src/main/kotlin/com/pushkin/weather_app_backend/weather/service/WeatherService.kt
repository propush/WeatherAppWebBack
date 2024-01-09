package com.pushkin.weather_app_backend.weather.service

import com.pushkin.weather_app_backend.cache.service.BaseEntityCachingServiceImpl
import com.pushkin.weather_app_backend.cache.service.EntityCacheConfiguration
import com.pushkin.weather_app_backend.lock.service.LockService
import com.pushkin.weather_app_backend.user.service.UserService
import com.pushkin.weather_app_backend.weather.client.WeatherSourceClient
import com.pushkin.weather_app_backend.weather.entity.WeatherCached
import com.pushkin.weather_app_backend.weather.exception.WeatherException
import com.pushkin.weather_app_backend.weather.exception.WeatherNotFoundException
import com.pushkin.weather_app_backend.weather.repository.WeatherCachedRepository
import com.pushkin.weather_app_backend.weather.vo.UserWeatherResponse
import com.pushkin.weather_app_backend.weather.vo.WeatherRequest
import com.pushkin.weather_app_backend.weather.vo.WeatherResponse
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Service

private val logger = KotlinLogging.logger {}

@Service
class WeatherService(
    private val weatherSourceClient: WeatherSourceClient,
    private val userService: UserService,
    weatherCachedRepository: WeatherCachedRepository,
    weatherCacheConfiguration: EntityCacheConfiguration<WeatherCached>,
    lockService: LockService
) : BaseEntityCachingServiceImpl<WeatherCached, String, WeatherCachedRepository>(
    weatherCachedRepository,
    weatherCacheConfiguration,
    lockService
) {

    @Throws(WeatherException::class, WeatherNotFoundException::class)
    fun getWeather(location: String): WeatherResponse {
        logger.info { "getWeather: location=$location" }
        val weatherCached = getEntity(location) {
            toWeatherCached(location, weatherSourceClient.fetchWeather(WeatherRequest(it)))
        }
        return weatherCached.weatherResponse
    }

    override fun findInRepository(key: String): WeatherCached? =
        repository.findByLocation(key)

    private fun toWeatherCached(location: String, weatherResponse: WeatherResponse): WeatherCached =
        WeatherCached(location, weatherResponse)

    fun getWeatherByUser(login: String): UserWeatherResponse {
        val locations = userService.getUserLocations(login)
        return UserWeatherResponse(
            login,
            locations
                .map { it to getWeatherSafe(it) }
                .filter { it.second != null }
                .associate { it.first to it.second!! }
        )
    }

    private fun getWeatherSafe(location: String): WeatherResponse? =
        try {
            getWeather(location)
        } catch (e: Exception) {
            logger.error(e) { "getWeatherSafe: location=$location" }
            null
        }

    @Throws(WeatherException::class, WeatherNotFoundException::class)
    fun saveLocation(login: String, location: String): UserWeatherResponse {
        getWeather(location) // check if location exists, throw exception early
        userService.addLocationToUser(login, location)
        return getWeatherByUser(login)
    }

    fun deleteLocation(login: String, location: String): UserWeatherResponse {
        userService.deleteLocationFromUser(login, location)
        return getWeatherByUser(login)
    }

}
