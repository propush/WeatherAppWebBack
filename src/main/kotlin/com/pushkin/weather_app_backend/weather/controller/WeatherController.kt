package com.pushkin.weather_app_backend.weather.controller

import com.pushkin.weather_app_backend.base.BaseController
import com.pushkin.weather_app_backend.base.OpenApiSecuredRestController
import com.pushkin.weather_app_backend.weather.service.WeatherService
import com.pushkin.weather_app_backend.weather.vo.WeatherResponseVO
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/weather")
class WeatherController(
    private val weatherService: WeatherService
) : BaseController(), OpenApiSecuredRestController {

    @GetMapping
    fun getWeather(@RequestParam location: String): ResponseEntity<WeatherResponseVO> =
        processServiceExceptions { weatherService.getWeather(location) }

}
