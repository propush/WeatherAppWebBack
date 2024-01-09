package com.pushkin.weather_app_backend.weather.controller

import com.pushkin.weather_app_backend.mockWeatherResponse
import com.pushkin.weather_app_backend.weather.exception.WeatherNotFoundException
import com.pushkin.weather_app_backend.weather.service.WeatherService
import com.pushkin.weather_app_backend.weather.vo.UserWeatherResponse
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
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf
import org.springframework.test.context.junit.jupiter.EnabledIf
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*

@EnabledIf(value = "#{'\${spring.profiles.active}' == 'test'}", loadContext = true)
@WebMvcTest(WeatherController::class)
class WeatherControllerTest {

    @MockBean
    private lateinit var weatherService: WeatherService

    @Autowired
    private lateinit var mockMvc: MockMvc

    @BeforeEach
    fun setUp() {
        whenever(weatherService.getWeather("L1")).thenReturn(mockWeatherResponse("L1"))
        whenever(weatherService.getWeatherByUser(eq("user"))).thenReturn(
            UserWeatherResponse(
                "user",
                mapOf("L1" to mockWeatherResponse("L1"))
            )
        )
        whenever(weatherService.saveLocation(eq("user"), eq("L1"))).thenReturn(
            UserWeatherResponse(
                "user",
                mapOf("L1" to mockWeatherResponse("L1"))
            )
        )
        whenever(weatherService.deleteLocation(eq("user"), eq("L1"))).thenReturn(
            UserWeatherResponse(
                "user",
                mapOf()
            )
        )
    }

    @Test
    @WithMockUser
    fun getWeather() {
        mockMvc.perform(get("/api/v1/weather?location=L1"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.name").value("L1"))
        verify(weatherService).getWeather(eq("L1"))
    }

    @Test
    @WithMockUser
    fun getWeatherNonexistent() {
        whenever(weatherService.getWeather("nonexistent"))
            .thenThrow(WeatherNotFoundException("nonexistent"))
        mockMvc.perform(get("/api/v1/weather?location=nonexistent"))
            .andExpect(status().isNotFound)
        verify(weatherService).getWeather(eq("nonexistent"))
    }

    @Test
    fun getWeatherNoAuthorization() {
        mockMvc.perform(get("/api/v1/weather?location=L1"))
            .andExpect(status().isUnauthorized)
        verifyNoInteractions(weatherService)
    }

    @Test
    @WithMockUser
    fun getWeatherByUser() {
        mockMvc.perform(get("/api/v1/weather/user"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.weatherResponseMap['L1'].name").value("L1"))
        verify(weatherService).getWeatherByUser(eq("user"))
    }

    @Test
    @WithMockUser
    fun saveLocation() {
        mockMvc.perform(
            post("/api/v1/weather/user/location")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"location\": \"L1\"}")
                .with(csrf())
        )
            .andDo { println("Handler: ${it.handler}") }
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.weatherResponseMap['L1'].name").value("L1"))
        verify(weatherService).saveLocation(eq("user"), eq("L1"))
    }

    @Test
    @WithMockUser
    fun deleteLocation() {
        mockMvc.perform(
            delete("/api/v1/weather/user/location")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"location\": \"L1\"}")
                .with(csrf())
        )
            .andDo { println("Handler: ${it.handler}") }
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        verify(weatherService).deleteLocation(eq("user"), eq("L1"))
    }

}
