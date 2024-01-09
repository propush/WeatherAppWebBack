package com.pushkin.weather_app_backend.weather.exception

import com.pushkin.weather_app_backend.base.EntityNotFoundException

class WeatherNotFoundException(entity: String) : EntityNotFoundException(entity)
