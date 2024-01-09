package com.pushkin.weather_app_backend.weather.service

import com.pushkin.weather_app_backend.configuration.GeoDataCacheConfiguration
import com.pushkin.weather_app_backend.lock.service.impl.LocalLockServiceImpl
import com.pushkin.weather_app_backend.weather.client.WeatherSourceClient
import com.pushkin.weather_app_backend.weather.entity.GeoDataCached
import com.pushkin.weather_app_backend.weather.repository.GeoDataCachedRepository
import com.pushkin.weather_app_backend.weather.vo.GeoResponse
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.kotlin.*

class GeoDataServiceTest {

    private lateinit var geoDataService: GeoDataService
    private lateinit var weatherSourceClient: WeatherSourceClient
    private lateinit var geoDataCachedRepository: GeoDataCachedRepository
    private lateinit var geoDataCacheConfiguration: GeoDataCacheConfiguration

    @BeforeEach
    fun setUp() {
        weatherSourceClient = mock {
            on { fetchGeoLocation(argThat { this.location == "L1" }) } doReturn setOf(
                GeoResponse("L1", "c1", "s1"),
                GeoResponse("L1", "c2", "s2")
            )
        }

        geoDataCacheConfiguration = GeoDataCacheConfiguration(3600L)

        geoDataCachedRepository = mock {
            on { save(any<GeoDataCached>()) } doAnswer { it.getArgument(0) }
        }

        geoDataService = GeoDataService(
            weatherSourceClient,
            geoDataCachedRepository,
            geoDataCacheConfiguration,
            LocalLockServiceImpl()
        )
    }

    @Test
    fun getGeoData() {
        val geoDataCached = geoDataService.getGeoData("L1")
        assertEquals(2, geoDataCached.size)
        assertEquals("L1", geoDataCached.first().name)
        assertEquals("c1", geoDataCached.first().country)
        assertEquals("s1", geoDataCached.first().state)
        assertEquals("L1", geoDataCached.last().name)
        assertEquals("c2", geoDataCached.last().country)
        assertEquals("s2", geoDataCached.last().state)
        verify(geoDataCachedRepository).findByLocation(eq("L1"))
        verify(geoDataCachedRepository).save(argThat { this.location == "L1" })
        verifyNoMoreInteractions(geoDataCachedRepository)
    }

}
