package com.pushkin.weather_app_backend.user.controller

import com.pushkin.weather_app_backend.security.vo.JWTResponseVO
import com.pushkin.weather_app_backend.toJson
import com.pushkin.weather_app_backend.user.entity.TokenType
import com.pushkin.weather_app_backend.user.exception.SignUpException
import com.pushkin.weather_app_backend.user.service.SignUpService
import com.pushkin.weather_app_backend.user.vo.ExchangeTokenRq
import com.pushkin.weather_app_backend.user.vo.SignInRq
import com.pushkin.weather_app_backend.user.vo.SignUpRq
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.kotlin.argThat
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf
import org.springframework.test.context.junit.jupiter.EnabledIf
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*

@EnabledIf(value = "#{'\${spring.profiles.active}' == 'test'}", loadContext = true)
@WebMvcTest(UserSignUpController::class)
@AutoConfigureMockMvc(addFilters = false)
class UserSignUpControllerTest {

    @MockBean
    private lateinit var signUpService: SignUpService

    @Autowired
    private lateinit var mockMvc: MockMvc

    @BeforeEach
    fun setUp() {
        whenever(signUpService.signIn(argThat { login == "l1" && password == "p1" }))
            .thenReturn(JWTResponseVO("t1"))
        whenever(signUpService.signIn(argThat { login == "bad" }))
            .thenThrow(SignUpException("Bad user"))
        whenever(signUpService.register(argThat { login == "l1" && password == "p1" }))
            .thenReturn(JWTResponseVO("t1"))
        whenever(signUpService.exchangeToken(argThat { externalToken == "et1" && externalTokenType == TokenType.google }))
            .thenReturn(JWTResponseVO("t1"))
    }

    @Test
    fun signUp() {
        mockMvc.perform(
            post("/signup/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(SignUpRq("l1", "p1")))
                .with(csrf())
        )
            .andDo { println("Handler: ${it.handler}") }
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.token").value("t1"))
        verify(signUpService).register(argThat { login == "l1" && password == "p1" })
    }

    @Test
    fun signIn() {
        mockMvc.perform(
            post("/signup/signin")
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(SignInRq("l1", "p1")))
                .with(csrf())
        )
            .andDo { println("Handler: ${it.handler}") }
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.token").value("t1"))
        verify(signUpService).signIn(argThat { login == "l1" && password == "p1" })
    }

    @Test
    fun signInBadUser() {
        mockMvc.perform(
            post("/signup/signin")
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(SignInRq("bad", "p1")))
                .with(csrf())
        )
            .andDo { println("Handler: ${it.handler}") }
            .andExpect(status().isForbidden)
        verify(signUpService).signIn(argThat { login == "bad" && password == "p1" })
    }

    @Test
    fun exchangeToken() {
        mockMvc.perform(
            post("/signup/exchange-token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(ExchangeTokenRq("et1", TokenType.google)))
                .with(csrf())
        )
            .andDo { println("Handler: ${it.handler}") }
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.token").value("t1"))
        verify(signUpService)
            .exchangeToken(argThat { externalToken == "et1" && externalTokenType == TokenType.google })
    }

}
