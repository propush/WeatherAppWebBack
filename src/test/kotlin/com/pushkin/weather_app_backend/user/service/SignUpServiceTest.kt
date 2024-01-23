package com.pushkin.weather_app_backend.user.service

import com.pushkin.weather_app_backend.security.service.TokenHelperService
import com.pushkin.weather_app_backend.user.entity.TokenType
import com.pushkin.weather_app_backend.user.entity.User
import com.pushkin.weather_app_backend.user.exception.SignUpException
import com.pushkin.weather_app_backend.user.exception.UserException
import com.pushkin.weather_app_backend.user.exception.UserExistsException
import com.pushkin.weather_app_backend.user.vo.ExchangeTokenRq
import com.pushkin.weather_app_backend.user.vo.SignInRq
import com.pushkin.weather_app_backend.user.vo.SignUpRq
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.kotlin.*

class SignUpServiceTest {

    private lateinit var signUpService: SignUpService
    private lateinit var tokenHelperService: TokenHelperService
    private lateinit var userService: UserService

    @BeforeEach
    fun setUp() {
        tokenHelperService = mock {
            on { createDefaultUserToken(eq("l1")) } doReturn "t1"
            on { createDefaultUserToken(eq("l2")) } doReturn "t2"
            on { getLoginFromToken(eq("t2"), eq(TokenType.google)) } doReturn "l2"
        }
        userService = mock {
            on { register(eq(SignUpRq("existing", "p1"))) } doThrow UserExistsException("User already exists")

            on { register(eq(SignUpRq("l1", "p1"))) } doReturn User(
                login = "l1",
                encryptedPassword = "p1"
            )

            on { findByActiveLoginOrThrow(eq("l1")) } doReturn User(
                login = "l1",
                encryptedPassword = "p1"
            )

            on { findByActiveLoginOrThrow(eq("inactive")) } doThrow UserException("Inactive user")

            on { findByActiveLoginOrThrow(eq("bad")) } doThrow UserException("Bad user")

            on { checkPassword(eq("p1"), eq("p1")) } doReturn true
            on { checkPassword(eq("wrong"), eq("p1")) } doReturn false

            on { getOrCreateByToken(eq("l2"), eq(TokenType.google)) } doReturn User(
                login = "l2",
                encryptedPassword = null
            )
        }
        signUpService = SignUpService(
            tokenHelperService,
            userService
        )
    }

    @Test
    fun register() {
        val jwtResponseVO = signUpService.register(
            SignUpRq(
                "l1",
                "p1"
            )
        )
        assertEquals("t1", jwtResponseVO.token)
        verify(tokenHelperService).createDefaultUserToken(eq("l1"))
        verify(userService).register(eq(SignUpRq("l1", "p1")))
    }

    @Test
    fun registerAlreadyExists() {
        assertThrows<SignUpException> {
            signUpService.register(
                SignUpRq(
                    "existing",
                    "p1"
                )
            )
        }
        verifyNoInteractions(tokenHelperService)
        verify(userService).register(eq(SignUpRq("existing", "p1")))
    }

    @Test
    fun signIn() {
        val jwtResponseVO = signUpService.signIn(
            SignInRq(
                "l1",
                "p1"
            )
        )
        assertEquals("t1", jwtResponseVO.token)
        verify(tokenHelperService).createDefaultUserToken(eq("l1"))
        verify(userService).findByActiveLoginOrThrow(eq("l1"))
        verify(userService).checkPassword(eq("p1"), eq("p1"))
    }

    @Test
    fun signInBadPassword() {
        assertThrows<SignUpException> {
            signUpService.signIn(
                SignInRq(
                    "l1",
                    "wrong"
                )
            )
        }
        verifyNoInteractions(tokenHelperService)
        verify(userService).findByActiveLoginOrThrow(eq("l1"))
        verify(userService).checkPassword(eq("wrong"), eq("p1"))
    }

    @Test
    fun signInBadUser() {
        assertThrows<SignUpException> {
            signUpService.signIn(
                SignInRq(
                    "bad",
                    "p1"
                )
            )
        }
        verifyNoInteractions(tokenHelperService)
        verify(userService).findByActiveLoginOrThrow(eq("bad"))
        verify(userService, never()).checkPassword(any(), any())
    }

    @Test
    fun signInInactiveUser() {
        assertThrows<SignUpException> {
            signUpService.signIn(
                SignInRq(
                    "inactive",
                    "p1"
                )
            )
        }
        verifyNoInteractions(tokenHelperService)
        verify(userService).findByActiveLoginOrThrow(eq("inactive"))
        verify(userService, never()).checkPassword(any(), any())
    }

    @Test
    fun exchangeToken() {
        val jwtResponseVO = signUpService.exchangeToken(
            ExchangeTokenRq(
                "t2",
                TokenType.google
            )
        )
        assertEquals("t2", jwtResponseVO.token)
        verify(tokenHelperService).createDefaultUserToken(eq("l2"))
        verify(userService).getOrCreateByToken(eq("l2"), eq(TokenType.google))
    }

}
