package com.pushkin.weather_app_backend.configuration

import com.fasterxml.jackson.databind.ObjectMapper
import com.pushkin.weather_app_backend.security.service.JWTAuthenticationFilter
import com.pushkin.weather_app_backend.security.service.JWTAuthorizationFilter
import com.pushkin.weather_app_backend.security.service.JWTDecoder
import com.pushkin.weather_app_backend.security.service.TokenHelperService
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


@Configuration
@EnableWebSecurity
@EnableMethodSecurity(securedEnabled = true, jsr250Enabled = true)
class WebSecurityConfiguration(
    private val jwtDecoder: JWTDecoder,
    private val tokenHelperService: TokenHelperService,
    private val objectMapper: ObjectMapper,
    @Lazy private val authenticationManager: AuthenticationManager
) {

    @Bean
    fun filterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .cors().and().csrf().disable()
            .authorizeHttpRequests { auth ->
                auth.requestMatchers(
                    AntPathRequestMatcher("/swagger-ui.html/**"),
                    AntPathRequestMatcher("/webjars/**"),
                    AntPathRequestMatcher("/v3/**"),
                    AntPathRequestMatcher("/swagger-resources/**"),
                    AntPathRequestMatcher("/actuator/**")
                ).hasRole("SWAGGER").and().httpBasic()
            }.authorizeHttpRequests { auth ->
                auth.requestMatchers(AntPathRequestMatcher("/api/v1/**")).hasRole("USER")
            }.authorizeHttpRequests { auth ->
                auth.requestMatchers(
                    AntPathRequestMatcher("/ws/**"),
                    AntPathRequestMatcher("/login/**"),
                    AntPathRequestMatcher("/signup/**")
                ).permitAll()
                    .anyRequest().authenticated()
            }.addFilterBefore(
                JWTAuthenticationFilter(jwtDecoder, authenticationManager, tokenHelperService, objectMapper),
                UsernamePasswordAuthenticationFilter::class.java
            ).addFilter(JWTAuthorizationFilter(jwtDecoder, authenticationManager))
            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        return http.build()
    }

    @Bean
    fun passwordEncoder(): PasswordEncoder = BCryptPasswordEncoder()

}
