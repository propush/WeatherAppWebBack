package com.pushkin.weather_app_backend.user.service

import com.pushkin.weather_app_backend.user.exception.UserException
import com.pushkin.weather_app_backend.user.exception.UserExistsException
import com.pushkin.weather_app_backend.user.repository.UserRepository
import com.pushkin.weather_app_backend.user.vo.SignUpRq
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit.jupiter.EnabledIf
import org.testcontainers.junit.jupiter.Testcontainers

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@EnabledIf(value = "#{'\${spring.profiles.active}' == 'test'}", loadContext = true)
class UserServiceTest {

    @Autowired
    private lateinit var userService: UserService

    @Autowired
    private lateinit var userRepository: UserRepository

    @AfterEach
    fun tearDown() {
        userRepository.deleteAll()
    }

    @Test
    fun register() {
        val user = userService.register(
            SignUpRq(
                "l1",
                "p1"
            )
        )
        assertEquals("l1", user.login)
        assertNotEquals("p1", user.encryptedPassword) //check encryption
        assertTrue(user.encryptedPassword.isNotBlank()) //check encryption
        assertEquals(user, userService.findByLogin("l1"))
    }

    @Test
    fun registerExisting() {
        userService.register(
            SignUpRq(
                "l2",
                "p1"
            )
        )
        assertThrows<UserExistsException> {
            userService.register(
                SignUpRq(
                    "l2",
                    "p2"
                )
            )
        }
        assertEquals(1, userRepository.findAll().count { it.login == "l2" })
    }

    @Test
    fun findByActiveLoginOrThrow() {
        userService.register(
            SignUpRq(
                "l3",
                "p1"
            )
        )
        val user = userService.findByActiveLoginOrThrow("l3")
        assertEquals("l3", user.login)
        assertTrue(user.active)
    }

    @Test
    fun findByActiveLoginOrThrowInactiveUser() {
        userService.register(
            SignUpRq(
                "l4",
                "p1"
            )
        )
        val user = userService.findByLogin("l4")!!
        user.active = false
        userRepository.save(user)
        assertThrows<UserException> {
            userService.findByActiveLoginOrThrow("l4")
        }
    }

    @Test
    fun findByActiveLoginOrThrowNonexistent() {
        assertThrows<UserException> {
            userService.findByActiveLoginOrThrow("nonexistent")
        }
    }

    @Test
    fun addLocationToUser() {
        userService.register(
            SignUpRq(
                "l5",
                "p1"
            )
        )
        val user = userService.addLocationToUser("l5", "loc1")
        assertEquals("l5", user.login)
        assertEquals(1, user.locations.size)
        assertTrue(user.locations.contains("loc1"))
    }

    @Test
    fun deleteLocationFromUser() {
        userService.register(
            SignUpRq(
                "l6",
                "p1"
            )
        )
        val user = userService.addLocationToUser("l6", "loc1")
        assertEquals("l6", user.login)
        assertEquals(1, user.locations.size)
        assertTrue(user.locations.contains("loc1"))
        val user2 = userService.deleteLocationFromUser("l6", "loc1")
        assertEquals("l6", user2.login)
        assertEquals(0, user2.locations.size)
        assertFalse(user2.locations.contains("loc1"))
    }

}
