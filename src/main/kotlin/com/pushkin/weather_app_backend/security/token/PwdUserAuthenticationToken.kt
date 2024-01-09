package com.pushkin.weather_app_backend.security.token

import org.springframework.security.authentication.AbstractAuthenticationToken
import org.springframework.security.core.GrantedAuthority

class PwdUserAuthenticationToken(
    private val login: String,
    private val password: String,
    authorities: List<GrantedAuthority> = emptyList()
) : AbstractAuthenticationToken(authorities) {

    override fun getCredentials(): Any = password

    override fun getPrincipal(): Any = login

}
