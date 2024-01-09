package com.pushkin.weather_app_backend.weather.client.impl

import com.pushkin.weather_app_backend.configuration.OpenWeatherMapConfiguration
import com.pushkin.weather_app_backend.weather.client.WeatherSourceClient
import com.pushkin.weather_app_backend.weather.exception.WeatherException
import com.pushkin.weather_app_backend.weather.exception.WeatherNotFoundException
import com.pushkin.weather_app_backend.weather.vo.GeoRequest
import com.pushkin.weather_app_backend.weather.vo.GeoResponse
import com.pushkin.weather_app_backend.weather.vo.WeatherRequest
import com.pushkin.weather_app_backend.weather.vo.WeatherResponse
import io.github.oshai.kotlinlogging.KotlinLogging
import jakarta.annotation.PostConstruct
import org.springframework.context.annotation.Profile
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.client.HttpClientErrorException
import org.springframework.web.client.RestClientException
import org.springframework.web.client.RestTemplate

private val logger = KotlinLogging.logger {}

@Service
@Profile("!test")
class OpenWeatherMapSourceClientImpl(
    private val openWeatherMapConfiguration: OpenWeatherMapConfiguration,
    private val restTemplate: RestTemplate
) : WeatherSourceClient {

    @PostConstruct
    fun init() {
        logger.info {
            "OpenWeatherMapSourceClientImpl initialized, url=${openWeatherMapConfiguration.url}, key=${
                openWeatherMapConfiguration.apiKey.take(4)
            }..."
        }
    }

    @Throws(WeatherException::class, WeatherNotFoundException::class)
    override fun fetchWeather(weatherRequest: WeatherRequest): WeatherResponse {
        logger.debug { "Fetching weather data from OpenWeatherMap for location: ${weatherRequest.location}" }
        val url = with(openWeatherMapConfiguration) {
            "${url}/data/2.5/weather?q=${weatherRequest.location}&limit=${limit}&appid=${apiKey}&units=metric"
        }
        try {
            val response = restTemplate.getForObject(url, WeatherResponse::class.java)
            logger.debug { "Weather response: $response" }
            return response
                ?: throw WeatherException("Null response fetching weather data from OpenWeatherMap")
        } catch (e: RestClientException) {
            if (e is HttpClientErrorException && e.statusCode == HttpStatus.NOT_FOUND) {
                throw WeatherNotFoundException(weatherRequest.location)
            }
            throw WeatherException("Error while fetching weather data from OpenWeatherMap", e)
        }
    }

    @Throws(WeatherException::class)
    override fun fetchGeoLocation(geoRequest: GeoRequest): Set<GeoResponse> {
        logger.debug { "Fetching geo data from OpenWeatherMap for location: ${geoRequest.location}" }
        val url = with(openWeatherMapConfiguration) {
            "${url}/geo/1.0/direct?q=${geoRequest.location}&limit=${limit}&appid=${apiKey}"
        }
        try {
            val response = restTemplate.getForObject(url, Array<GeoResponse>::class.java)
            logger.debug { "Geo response: $response" }
            return response
                ?.toSet()
                ?: throw WeatherException("Null response fetching geo data from OpenWeatherMap")
        } catch (e: RestClientException) {
            throw WeatherException("Error while fetching geo data from OpenWeatherMap", e)
        }
    }

}
