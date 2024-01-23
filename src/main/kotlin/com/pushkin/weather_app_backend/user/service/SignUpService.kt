package com.pushkin.weather_app_backend.user.service

import com.pushkin.weather_app_backend.security.service.TokenHelperService
import com.pushkin.weather_app_backend.security.vo.JWTResponseVO
import com.pushkin.weather_app_backend.user.exception.SignUpException
import com.pushkin.weather_app_backend.user.vo.ExchangeTokenRq
import com.pushkin.weather_app_backend.user.vo.SignInRq
import com.pushkin.weather_app_backend.user.vo.SignUpRq
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Service

private val logger = KotlinLogging.logger {}

@Service
class SignUpService(
    private val tokenHelperService: TokenHelperService,
    private val userService: UserService
) {

    @Throws(SignUpException::class)
    fun register(signUpRq: SignUpRq): JWTResponseVO {
        logger.info { "Registering user: ${signUpRq.login}" }
        try {
            val user = userService.register(signUpRq)
            return JWTResponseVO(tokenHelperService.createDefaultUserToken(signUpRq.login))
        } catch (e: Exception) {
            logger.warn { "Error registering user: ${signUpRq.login}" }
            throw SignUpException("Error registering: ${signUpRq.login}", e)
        }
    }

    @Throws(SignUpException::class)
    fun signIn(signInRq: SignInRq): JWTResponseVO {
        logger.info { "Signing in user: ${signInRq.login}" }
        try {
            val user = userService.findByActiveLoginOrThrow(signInRq.login)
            if (user.encryptedPassword == null) {
                logger.warn { "User ${signInRq.login} has no password, tokenType = ${user.tokenType}" }
                throw SignUpException("Bad user or password for user: ${signInRq.login}")
            }
            if (!userService.checkPassword(signInRq.password, user.encryptedPassword)) {
                logger.warn { "Wrong password for user: ${signInRq.login}" }
                throw SignUpException("Bad user or password for user: ${signInRq.login}")
            }
            return JWTResponseVO(tokenHelperService.createDefaultUserToken(signInRq.login))
        } catch (e: Exception) {
            throw SignUpException("Error signing in: ${signInRq.login}", e)
        }
    }

    @Throws(SignUpException::class)
    fun exchangeToken(externalTokenRq: ExchangeTokenRq): JWTResponseVO {
        logger.info { "Exchanging ${externalTokenRq.externalTokenType} token: ${externalTokenRq.externalToken.length} bytes" }
        try {
            val login = tokenHelperService
                .getLoginFromToken(externalTokenRq.externalToken, externalTokenRq.externalTokenType)
            val user = userService
                .getOrCreateByToken(login, externalTokenRq.externalTokenType)
            return JWTResponseVO(tokenHelperService.createDefaultUserToken(user.login))
        } catch (e: Exception) {
            throw SignUpException("Error exchanging token", e)
        }
    }

}
