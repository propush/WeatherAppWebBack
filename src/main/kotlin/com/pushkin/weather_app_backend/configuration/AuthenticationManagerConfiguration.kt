package com.pushkin.weather_app_backend.configuration

import com.pushkin.weather_app_backend.security.service.PwdUserAuthenticationProvider
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.web.builders.HttpSecurity

@Configuration
class AuthenticationManagerConfiguration(
    private val pwdUserAuthenticationProvider: PwdUserAuthenticationProvider,
) {

    @Bean
    fun authenticationManager(http: HttpSecurity): AuthenticationManager {
        val authenticationManagerBuilder =
            http.getSharedObject(AuthenticationManagerBuilder::class.java)
        authenticationManagerBuilder.authenticationProvider(pwdUserAuthenticationProvider)
        return authenticationManagerBuilder.build()
    }

}
