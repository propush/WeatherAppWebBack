package com.pushkin.weather_app_backend.configuration

import io.swagger.v3.oas.annotations.OpenAPIDefinition
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType
import io.swagger.v3.oas.annotations.info.Info
import io.swagger.v3.oas.annotations.security.SecurityScheme
import org.springframework.context.annotation.Configuration

@Configuration
@OpenAPIDefinition(info = Info(title = "Weather API", version = "v1"))
@SecurityScheme(
    name = OpenApi30Configuration.BEARER_AUTH_SECURITY_SCHEME,
    type = SecuritySchemeType.HTTP,
    bearerFormat = "JWT",
    scheme = "bearer"
)
class OpenApi30Configuration {
    companion object {
        const val BEARER_AUTH_SECURITY_SCHEME = "bearerAuth"
    }
}
