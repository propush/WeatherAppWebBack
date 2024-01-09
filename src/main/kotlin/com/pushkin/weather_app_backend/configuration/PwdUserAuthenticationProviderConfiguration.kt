package com.pushkin.weather_app_backend.configuration

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.bind.ConstructorBinding

@ConfigurationProperties("users")
data class PwdUserAuthenticationProviderConfiguration @ConstructorBinding constructor(
    val swaggerAdmin: User
) {
    data class User(
        val login: String,
        val password: String
    )
}
