package com.pushkin.weather_app_backend.configuration

import com.fasterxml.jackson.databind.ObjectMapper
import com.pushkin.weather_app_backend.security.service.JWTAuthenticationFilter
import com.pushkin.weather_app_backend.security.service.JWTAuthorizationFilter
import com.pushkin.weather_app_backend.security.service.JWTDecoder
import com.pushkin.weather_app_backend.security.service.TokenHelperService
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Lazy
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.security.web.util.matcher.AntPathRequestMatcher
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.CorsConfigurationSource
import org.springframework.web.cors.UrlBasedCorsConfigurationSource


@Configuration
@EnableWebSecurity
@EnableMethodSecurity(securedEnabled = true, jsr250Enabled = true)
class WebSecurityConfiguration(
    private val jwtDecoder: JWTDecoder,
    private val tokenHelperService: TokenHelperService,
    private val objectMapper: ObjectMapper,
    @Lazy private val authenticationManager: AuthenticationManager
) {

    @Value("\${cors.allowed-origins}")
    private lateinit var allowedOrigins: List<String>

    @Bean
    fun filterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .csrf { csrf ->
                csrf.disable()
            }.cors { cors ->
                cors.configurationSource(corsConfigurationSource())
            }
            .authorizeHttpRequests { auth ->
                auth.requestMatchers(
                    AntPathRequestMatcher("/swagger-ui.html/**"),
                    AntPathRequestMatcher("/webjars/**"),
                    AntPathRequestMatcher("/v3/**"),
                    AntPathRequestMatcher("/swagger-resources/**"),
                    AntPathRequestMatcher("/actuator/**")
                ).hasRole("SWAGGER")
            }
            .httpBasic { httpBasic ->
                httpBasic.realmName("Swagger admin")
            }
            .sessionManagement { it.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }
            .authorizeHttpRequests { auth ->
                auth.requestMatchers(
                    AntPathRequestMatcher("/api/v1/**")
                ).hasRole("USER")
            }.authorizeHttpRequests { auth ->
                auth.requestMatchers(
                    AntPathRequestMatcher("/ws/**"),
                    AntPathRequestMatcher("/login/**"),
                    AntPathRequestMatcher("/signup/**"),
                    AntPathRequestMatcher("/error"),
                ).permitAll()
                    .anyRequest().authenticated()
            }.addFilterBefore(
                JWTAuthenticationFilter(jwtDecoder, authenticationManager, tokenHelperService, objectMapper),
                UsernamePasswordAuthenticationFilter::class.java
            ).addFilter(JWTAuthorizationFilter(jwtDecoder, authenticationManager))
        return http.build()
    }

    @Bean
    fun corsConfigurationSource(): CorsConfigurationSource {
        val corsConfiguration = CorsConfiguration()
        corsConfiguration.allowedOrigins = allowedOrigins
        corsConfiguration.allowedMethods = listOf("GET", "POST", "PUT", "DELETE", "OPTIONS", "HEAD")
        corsConfiguration.allowCredentials = false
        corsConfiguration.allowedHeaders = listOf("*")
        corsConfiguration.maxAge = 3600L
        val source: UrlBasedCorsConfigurationSource = UrlBasedCorsConfigurationSource()
        source.registerCorsConfiguration("/**", corsConfiguration)
        return source
    }

    @Bean
    fun passwordEncoder(): PasswordEncoder = BCryptPasswordEncoder()

}
