package com.pushkin.weather_app_backend.weather.controller

import com.pushkin.weather_app_backend.base.BaseController
import com.pushkin.weather_app_backend.base.OpenApiSecuredRestController
import com.pushkin.weather_app_backend.weather.service.WeatherService
import com.pushkin.weather_app_backend.weather.vo.DeleteLocationRq
import com.pushkin.weather_app_backend.weather.vo.SaveLocationRq
import com.pushkin.weather_app_backend.weather.vo.UserWeatherResponse
import com.pushkin.weather_app_backend.weather.vo.WeatherResponse
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.security.Principal

@RestController
@RequestMapping("/api/v1/weather")
class WeatherController(
    private val weatherService: WeatherService
) : BaseController(), OpenApiSecuredRestController {

    @GetMapping
    fun getWeatherByLocation(@RequestParam location: String): ResponseEntity<WeatherResponse> =
        processServiceExceptions { weatherService.getWeather(location) }

    @GetMapping("/user")
    fun getWeatherByUser(principal: Principal): ResponseEntity<UserWeatherResponse> =
        processServiceExceptions { weatherService.getWeatherByUser(principal.name) }

    @PostMapping("/user/location")
    fun saveLocation(
        @RequestBody saveLocationRq: SaveLocationRq,
        principal: Principal
    ): ResponseEntity<UserWeatherResponse> =
        processServiceExceptions { weatherService.saveLocation(principal.name, saveLocationRq.location) }

    @DeleteMapping("/user/location")
    fun deleteLocation(
        @RequestBody deleteLocationRq: DeleteLocationRq,
        principal: Principal
    ): ResponseEntity<UserWeatherResponse> =
        processServiceExceptions { weatherService.deleteLocation(principal.name, deleteLocationRq.location) }

}
