package com.pushkin.weather_app_backend.user.controller

import com.pushkin.weather_app_backend.base.BaseController
import com.pushkin.weather_app_backend.security.vo.JWTResponseVO
import com.pushkin.weather_app_backend.user.service.SignUpService
import com.pushkin.weather_app_backend.user.vo.ConfirmCodeRq
import com.pushkin.weather_app_backend.user.vo.SignUpRq
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RequestMapping("/signup")
@RestController
class UserSignUpController(@Autowired val signUpService: SignUpService) : BaseController() {

    @PostMapping("/register")
    fun signUp(@RequestBody signUpRq: SignUpRq): ResponseEntity<Any> =
        processServiceExceptions { signUpService.register(signUpRq) }

    @PostMapping("/confirm")
    fun confirm(@RequestBody confirmCodeRq: ConfirmCodeRq): ResponseEntity<JWTResponseVO> =
        processServiceExceptions { signUpService.signUp(confirmCodeRq) }

}
