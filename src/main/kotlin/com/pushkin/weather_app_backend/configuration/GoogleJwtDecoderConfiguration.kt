package com.pushkin.weather_app_backend.configuration

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder

@Configuration
class GoogleJwtDecoderConfiguration {

    @Bean
    fun googleJwtDecoder(): NimbusJwtDecoder =
        NimbusJwtDecoder
            .withJwkSetUri("https://www.googleapis.com/oauth2/v3/certs")
            .build()

}
