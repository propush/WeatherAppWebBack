package com.pushkin.weather_app_backend.security.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.pushkin.weather_app_backend.security.exception.UserNotAuthorizedException
import com.pushkin.weather_app_backend.security.token.PwdUserAuthenticationToken
import com.pushkin.weather_app_backend.security.vo.JWTResponseVO
import jakarta.servlet.FilterChain
import jakarta.servlet.ServletException
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import java.io.IOException

@Suppress("TooGenericExceptionThrown")
open class JWTAuthenticationFilter(
    private val jwtDecoder: JWTDecoder,
    private val authenticationManagerBean: AuthenticationManager,
    private val tokenHelperService: TokenHelperService,
    private val objectMapper: ObjectMapper
) : UsernamePasswordAuthenticationFilter() {

    companion object {
        private const val HEADER_STRING = "Authorization"
        private const val TOKEN_PREFIX = "Bearer "
    }

    @Throws(AuthenticationException::class)
    override fun attemptAuthentication(
        req: HttpServletRequest,
        res: HttpServletResponse?
    ): Authentication {
        val authValue = try {
            objectMapper.readValue(req.inputStream, Map::class.java)
        } catch (e: Exception) {
            throw RuntimeException(e)
        }
        val authentication = try {
            objectMapper.convertValue(authValue, PwdUserAuthenticationToken::class.java)
        } catch (e: IllegalArgumentException) {
            throw UserNotAuthorizedException("Bad auth value: $authValue")
        }
        return authenticationManagerBean.authenticate(authentication)
    }

    @Throws(IOException::class, ServletException::class)
    override fun successfulAuthentication(
        req: HttpServletRequest,
        res: HttpServletResponse,
        chain: FilterChain,
        auth: Authentication
    ) {

        val token = tokenHelperService.createToken(
            (auth as UsernamePasswordAuthenticationToken).name,
            auth.authorities,
            jwtDecoder.ttlSeconds
        )

        res.addHeader(HEADER_STRING, TOKEN_PREFIX + token)
        res.apply {
            contentType = "application/json"
            characterEncoding = "UTF-8"
            writer.write(objectMapper.writeValueAsString(JWTResponseVO(token)))
        }
    }


}
