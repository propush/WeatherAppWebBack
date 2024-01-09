package com.pushkin.weather_app_backend.security.exception

import org.springframework.security.core.AuthenticationException

open class UserNotAuthorizedException : AuthenticationException {
    constructor(msg: String?, cause: Throwable?) : super(msg, cause)
    constructor(msg: String?) : super(msg)
}
