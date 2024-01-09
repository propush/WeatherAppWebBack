package com.pushkin.weather_app_backend.user.service

import com.pushkin.weather_app_backend.user.entity.User
import com.pushkin.weather_app_backend.user.exception.UserException
import com.pushkin.weather_app_backend.user.exception.UserExistsException
import com.pushkin.weather_app_backend.user.repository.UserRepository
import com.pushkin.weather_app_backend.user.vo.SignUpRq
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
class UserService(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder
) {

    fun findByLogin(login: String): User? =
        userRepository.findByLogin(login.trim().lowercase())

    @Throws(UserExistsException::class)
    fun register(signUpRq: SignUpRq): User {
        val login = signUpRq.login.trim().lowercase()
        val user = User(
            login = login,
            encryptedPassword = passwordEncoder.encode(signUpRq.password)
        )
        if (userRepository.findByLogin(login) != null) {
            throw UserExistsException("User with login ${signUpRq.login} already exists")
        }
        return userRepository.save(user)
    }

    fun checkPassword(password: String, encryptedPassword: String): Boolean {
        return passwordEncoder.matches(password, encryptedPassword)
    }

    @Throws(UserException::class)
    fun getUserLocations(login: String): Set<String> {
        val user = findByActiveLoginOrThrow(login)
        return user.locations
    }

    @Throws(UserException::class)
    fun findByActiveLoginOrThrow(login: String): User {
        val user = findByLogin(login)
            ?: throw UserException("User with login $login not found")
        if (!user.active) {
            throw UserException("User with login $login is not active")
        }
        return user
    }

    fun addLocationToUser(login: String, location: String): User {
        val user = findByActiveLoginOrThrow(login)
        user.locations.add(location)
        return userRepository.save(user)
    }

    fun deleteLocationFromUser(login: String, location: String): User {
        val user = findByActiveLoginOrThrow(login)
        user.locations.remove(location)
        return userRepository.save(user)
    }

}
