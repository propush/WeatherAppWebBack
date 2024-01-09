package com.pushkin.weather_app_backend.user.service

import com.pushkin.weather_app_backend.security.service.TokenHelperService
import com.pushkin.weather_app_backend.security.vo.JWTResponseVO
import com.pushkin.weather_app_backend.user.exception.SignUpException
import com.pushkin.weather_app_backend.user.vo.ConfirmCodeRq
import com.pushkin.weather_app_backend.user.vo.SignUpRq
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class SignUpService(
    @Autowired val tokenHelperService: TokenHelperService
) {

    @Throws(SignUpException::class)
    fun signUp(confirmCodeRq: ConfirmCodeRq): JWTResponseVO =
        JWTResponseVO(tokenHelperService.createDefaultUserToken(confirmCodeRq.login))

    @Throws(SignUpException::class)
    fun register(signUpRq: SignUpRq) {
        if (signUpRq.login == "bad") {
            throw SignUpException("Wrong name: ${signUpRq.login}")
        }
    }

}
