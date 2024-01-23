package com.pushkin.weather_app_backend.security.service

import com.pushkin.weather_app_backend.configuration.PwdUserAuthenticationProviderConfiguration
import com.pushkin.weather_app_backend.security.exception.UserNotAuthorizedException
import com.pushkin.weather_app_backend.security.token.PwdUserAuthenticationToken
import com.pushkin.weather_app_backend.user.service.UserService
import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.GrantedAuthority
import org.springframework.stereotype.Component

@Component
class PwdUserAuthenticationProvider(
    private val pwdUserAuthenticationProviderConfiguration: PwdUserAuthenticationProviderConfiguration,
    private val userService: UserService
) : AuthenticationProvider {

    @Throws(UserNotAuthorizedException::class)
    override fun authenticate(authentication: Authentication?): Authentication {
        val userAuthToken = authTokenFromAuthentication(authentication)
        val login = userAuthToken.principal.toString()
        if (isSwaggerAdmin(userAuthToken)) {
            return UsernamePasswordAuthenticationToken(
                login,
                null,
                listOf(GrantedAuthority { "ROLE_SWAGGER" })
            )
        }
        if (isUserValid(login, userAuthToken.credentials.toString())) {
            return UsernamePasswordAuthenticationToken(
                login,
                null,
                listOf(GrantedAuthority { "ROLE_USER" })
            )
        }
        throw UserNotAuthorizedException("User $login is not allowed")
    }

    private fun isUserValid(login: String, password: String): Boolean {
        val user = userService.findByLogin(login) ?: return false
        val encryptedPassword = user.encryptedPassword ?: return false
        return userService.checkPassword(password, encryptedPassword)
    }

    private fun isSwaggerAdmin(userAuthToken: Authentication): Boolean =
        userAuthToken.principal == pwdUserAuthenticationProviderConfiguration.swaggerAdmin.login
                && userAuthToken.credentials == pwdUserAuthenticationProviderConfiguration.swaggerAdmin.password

    private fun authTokenFromAuthentication(authentication: Authentication?): Authentication {
        if (authentication == null) {
            throw UserNotAuthorizedException("Authentication is null")
        }
        if (authentication is PwdUserAuthenticationToken) {
            return authentication
        }
        if (authentication is UsernamePasswordAuthenticationToken) {
            return PwdUserAuthenticationToken(
                authentication.principal?.toString() ?: throw UserNotAuthorizedException("Principal is null"),
                authentication.credentials?.toString() ?: throw UserNotAuthorizedException("Credentials not provided")
            )
        }
        throw UserNotAuthorizedException("Bad authentication type: ${authentication.javaClass}")
    }

    override fun supports(authentication: Class<*>?): Boolean =
        authentication == PwdUserAuthenticationToken::class.java
                || authentication == UsernamePasswordAuthenticationToken::class.java
}
