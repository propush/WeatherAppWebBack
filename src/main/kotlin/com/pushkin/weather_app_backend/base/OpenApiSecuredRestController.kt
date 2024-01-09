package com.pushkin.weather_app_backend.base

import com.pushkin.weather_app_backend.configuration.OpenApi30Configuration
import io.swagger.v3.oas.annotations.security.SecurityRequirement

@SecurityRequirement(name = OpenApi30Configuration.BEARER_AUTH_SECURITY_SCHEME)
interface OpenApiSecuredRestController
