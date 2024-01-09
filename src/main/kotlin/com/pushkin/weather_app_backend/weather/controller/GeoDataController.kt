package com.pushkin.weather_app_backend.weather.controller

import com.pushkin.weather_app_backend.base.BaseController
import com.pushkin.weather_app_backend.base.OpenApiSecuredRestController
import com.pushkin.weather_app_backend.weather.service.GeoDataService
import com.pushkin.weather_app_backend.weather.vo.GeoResponse
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/geo")
class GeoDataController(private val geoDataService: GeoDataService) :
    BaseController(), OpenApiSecuredRestController {

    @GetMapping
    fun getGeoDataByLocation(@RequestParam location: String): ResponseEntity<Set<GeoResponse>> =
        processServiceExceptions { geoDataService.getGeoData(location) }

}
