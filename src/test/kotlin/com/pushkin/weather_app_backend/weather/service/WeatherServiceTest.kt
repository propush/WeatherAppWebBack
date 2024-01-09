package com.pushkin.weather_app_backend.weather.service

import com.pushkin.weather_app_backend.configuration.WeatherCacheConfiguration
import com.pushkin.weather_app_backend.lock.service.impl.LocalLockServiceImpl
import com.pushkin.weather_app_backend.mockWeatherResponse
import com.pushkin.weather_app_backend.user.entity.User
import com.pushkin.weather_app_backend.user.service.UserService
import com.pushkin.weather_app_backend.weather.client.WeatherSourceClient
import com.pushkin.weather_app_backend.weather.entity.WeatherCached
import com.pushkin.weather_app_backend.weather.exception.WeatherNotFoundException
import com.pushkin.weather_app_backend.weather.repository.WeatherCachedRepository
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.kotlin.*

class WeatherServiceTest {

    private lateinit var weatherService: WeatherService
    private lateinit var weatherCachedRepository: WeatherCachedRepository
    private lateinit var weatherCacheConfiguration: WeatherCacheConfiguration
    private lateinit var weatherSourceClient: WeatherSourceClient
    private lateinit var userService: UserService

    private val weatherResponse1 = mockWeatherResponse("L1")

    @BeforeEach
    fun setUp() {
        weatherSourceClient = mock {
            on { fetchWeather(argThat { this.location == "L1" }) } doReturn weatherResponse1
            on { fetchWeather(argThat { this.location == "l3" }) } doReturn mockWeatherResponse("l3")
            on { fetchWeather(argThat { this.location == "nonexistent" }) } doThrow WeatherNotFoundException("nonexistent")
        }
        weatherCacheConfiguration = WeatherCacheConfiguration(60L)
        weatherCachedRepository = mock {
            on { save(any<WeatherCached>()) } doAnswer { it.getArgument(0) }
        }
        userService = mock {
            on { getUserLocations(eq("L1")) } doReturn setOf("l1", "l2")
            on { addLocationToUser(eq("L1"), eq("l3")) } doReturn User(
                login = "l1",
                encryptedPassword = "p1",
                locations = sortedSetOf("l3")
            )
            on { deleteLocationFromUser(eq("L1"), eq("l3")) } doReturn User(
                login = "l1",
                encryptedPassword = "p1",
                locations = sortedSetOf()
            )
        }

        weatherService = WeatherService(
            weatherSourceClient,
            userService,
            weatherCachedRepository,
            weatherCacheConfiguration,
            LocalLockServiceImpl()
        )
    }

    @Test
    fun getWeatherNotCached() {
        whenever(weatherCachedRepository.findByLocation(eq("L1"))).thenReturn(null)
        val weatherResponse = weatherService.getWeather("L1")
        assertEquals("L1", weatherResponse.name)
        verify(weatherCachedRepository).findByLocation(eq("L1"))
        verify(weatherCachedRepository).save(argThat { this.location == "L1" })
        verifyNoMoreInteractions(weatherCachedRepository)
    }

    @Test
    fun getWeatherNotCachedNonexistent() {
        whenever(weatherCachedRepository.findByLocation(eq("nonexistent"))).thenReturn(null)
        assertThrows<WeatherNotFoundException> { weatherService.getWeather("nonexistent") }
        verify(weatherCachedRepository).findByLocation(eq("nonexistent"))
        verifyNoMoreInteractions(weatherCachedRepository)
    }

    @Test
    fun getWeatherCachedAndEvicted() {
        whenever(weatherCachedRepository.findByLocation(eq("L1"))).thenReturn(
            WeatherCached("L1", weatherResponse1).apply {
                cacheDt = cacheDt.minusSeconds(10000)
            }
        )
        val weatherResponse = weatherService.getWeather("L1")
        assertEquals("L1", weatherResponse.name)
        verify(weatherCachedRepository).findByLocation(eq("L1"))
        verify(weatherCachedRepository).delete(argThat { this.location == "L1" })
        verify(weatherCachedRepository).save(argThat { this.location == "L1" })
        verifyNoMoreInteractions(weatherCachedRepository)
    }

    @Test
    fun getWeatherCached() {
        whenever(weatherCachedRepository.findByLocation(eq("L1"))).thenReturn(
            WeatherCached("L1", weatherResponse1)
        )
        val weatherResponse = weatherService.getWeather("L1")
        assertEquals("L1", weatherResponse.name)
        verify(weatherCachedRepository).findByLocation(eq("L1"))
        verify(weatherCachedRepository, never()).save(any<WeatherCached>())
        verifyNoMoreInteractions(weatherCachedRepository)
    }

    @Test
    fun getWeatherByUser() {
        whenever(weatherCachedRepository.findByLocation(eq("l1"))).thenReturn(
            WeatherCached("l1", weatherResponse1)
        )
        whenever(weatherCachedRepository.findByLocation(eq("l2"))).thenReturn(
            WeatherCached("l2", weatherResponse1)
        )
        val userWeatherResponse = weatherService.getWeatherByUser("L1")
        assertEquals("L1", userWeatherResponse.login)
        assertEquals(2, userWeatherResponse.weatherResponseMap.size)
        verify(weatherCachedRepository).findByLocation(eq("l1"))
        verify(weatherCachedRepository).findByLocation(eq("l2"))
        verify(weatherCachedRepository, never()).save(any<WeatherCached>())
        verifyNoMoreInteractions(weatherCachedRepository)
    }

    @Test
    fun getWeatherByUserBadLocation() {
        whenever(userService.getUserLocations(eq("L1"))).thenReturn(setOf("nonexistent", "l2"))
        whenever(weatherCachedRepository.findByLocation(eq("nonexistent"))).thenReturn(null)
        whenever(weatherCachedRepository.findByLocation(eq("l2"))).thenReturn(
            WeatherCached("l2", weatherResponse1)
        )
        val userWeatherResponse = weatherService.getWeatherByUser("L1")
        assertEquals("L1", userWeatherResponse.login)
        assertEquals(1, userWeatherResponse.weatherResponseMap.size)
        verify(weatherCachedRepository).findByLocation(eq("nonexistent"))
        verify(weatherCachedRepository).findByLocation(eq("l2"))
        verify(weatherCachedRepository, never()).save(any<WeatherCached>())
        verifyNoMoreInteractions(weatherCachedRepository)
    }

    @Test
    fun saveLocation() {
        whenever(weatherCachedRepository.findByLocation(eq("l1"))).thenReturn(
            WeatherCached("l1", weatherResponse1)
        )
        whenever(weatherCachedRepository.findByLocation(eq("l2"))).thenReturn(
            WeatherCached("l2", weatherResponse1)
        )
        val userWeatherResponse = weatherService.saveLocation("L1", "l3")
        assertEquals("L1", userWeatherResponse.login)
        verify(userService).addLocationToUser(eq("L1"), eq("l3"))
    }

    @Test
    fun saveLocationNonexistent() {
        whenever(weatherCachedRepository.findByLocation(eq("l1"))).thenReturn(
            WeatherCached("l1", weatherResponse1)
        )
        whenever(weatherCachedRepository.findByLocation(eq("l2"))).thenReturn(
            WeatherCached("l2", weatherResponse1)
        )
        whenever(weatherCachedRepository.findByLocation(eq("nonexistent"))).thenReturn(null)
        assertThrows<WeatherNotFoundException> {
            weatherService.saveLocation("L1", "nonexistent")
        }
        verifyNoInteractions(userService)
    }

    @Test
    fun deleteLocation() {
        whenever(weatherCachedRepository.findByLocation(eq("l1"))).thenReturn(
            WeatherCached("l1", weatherResponse1)
        )
        whenever(weatherCachedRepository.findByLocation(eq("l2"))).thenReturn(
            WeatherCached("l2", weatherResponse1)
        )
        val userWeatherResponse = weatherService.deleteLocation("L1", "l3")
        assertEquals("L1", userWeatherResponse.login)
        verify(userService).deleteLocationFromUser(eq("L1"), eq("l3"))
    }

}
