package com.pushkin.weather_app_backend.user.exception

import com.pushkin.weather_app_backend.security.exception.UserNotAuthorizedException


class SignUpException : UserNotAuthorizedException {
    constructor(msg: String?, cause: Throwable?) : super(msg, cause)
    constructor(msg: String?) : super(msg)
}
