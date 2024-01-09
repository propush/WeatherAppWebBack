package com.pushkin.weather_app_backend.weather.controller

import com.pushkin.weather_app_backend.weather.service.GeoDataService
import com.pushkin.weather_app_backend.weather.vo.GeoResponse
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.kotlin.eq
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyNoInteractions
import org.mockito.kotlin.whenever
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.context.junit.jupiter.EnabledIf
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*

@EnabledIf(value = "#{'\${spring.profiles.active}' == 'test'}", loadContext = true)
@WebMvcTest(GeoDataController::class)
class GeoDataControllerTest {

    @MockBean
    private lateinit var geoDataService: GeoDataService

    @Autowired
    private lateinit var mockMvc: MockMvc

    @BeforeEach
    fun setUp() {
        whenever(geoDataService.getGeoData("L1"))
            .thenReturn(setOf(GeoResponse("L1", "c1", "s1")))
    }

    @Test
    @WithMockUser
    fun getGeoDataByLocation() {
        mockMvc.perform(get("/api/v1/geo?location=L1"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$[0].name").value("L1"))
        verify(geoDataService).getGeoData(eq("L1"))
    }

    @Test
    fun getGeoDataByLocationNoAuthorization() {
        mockMvc.perform(get("/api/v1/geo?location=L1"))
            .andExpect(status().isUnauthorized)
        verifyNoInteractions(geoDataService)
    }

}
