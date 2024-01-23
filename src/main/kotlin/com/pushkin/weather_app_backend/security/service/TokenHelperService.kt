package com.pushkin.weather_app_backend.security.service

import com.auth0.jwt.JWT
import com.pushkin.weather_app_backend.user.entity.TokenType
import org.springframework.security.core.GrantedAuthority
import org.springframework.stereotype.Service
import java.util.*

@Service
class TokenHelperService(val jwtDecoder: JWTDecoder) {

    fun createToken(name: String, authorities: Collection<GrantedAuthority>, ttlSec: Long): String =
        jwtDecoder.sign(
            JWT.create()
                .withSubject(name)
                .withExpiresAt(getTokenExpirationDate(ttlSec))
                .withClaim("scopes", authorities.joinToString(",") { it.authority })
        )

    fun createDefaultUserToken(name: String) =
        createToken(name, listOf(GrantedAuthority { "ROLE_USER" }), jwtDecoder.ttlSeconds)

    private fun getTokenExpirationDate(ttlSec: Long) =
        Date(System.currentTimeMillis() + ttlSec * 1000)

    fun getLoginFromToken(token: String, tokenType: TokenType): String =
        when (tokenType) {
            TokenType.google -> jwtDecoder
                .decodeGoogle(token)
                ?.getClaim<String>("email")
                ?: throw IllegalArgumentException("Can't decode $tokenType token")
        }

}
