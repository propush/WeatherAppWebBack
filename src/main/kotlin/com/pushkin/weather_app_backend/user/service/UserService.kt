package com.pushkin.weather_app_backend.user.service

import com.pushkin.weather_app_backend.user.entity.TokenType
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

    @Throws(UserExistsException::class, UserException::class)
    fun register(signUpRq: SignUpRq): User {
        val login = signUpRq.login.trim().lowercase()
        val encryptedPassword = getEncryptedPassword(signUpRq)
        if (encryptedPassword == null && signUpRq.tokenType == null) {
            throw UserException("Password is null and tokenType is null")
        }
        val user = User(
            login = login,
            encryptedPassword = encryptedPassword,
            tokenType = signUpRq.tokenType
        )
        if (userRepository.findByLogin(login) != null) {
            throw UserExistsException("User with login ${signUpRq.login} already exists")
        }
        return userRepository.save(user)
    }

    private fun getEncryptedPassword(signUpRq: SignUpRq): String? =
        signUpRq
            .password
            ?.let(passwordEncoder::encode)

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
        if (!isUserValid(user)) {
            throw UserException("User with login $login is not active")
        }
        return user
    }

    private fun isUserValid(user: User) =
        user.active

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

    @Throws(UserException::class)
    fun getOrCreateByToken(login: String, tokenType: TokenType): User {
        val user = findByLoginAndTokenType(login, tokenType)
        if (user != null) {
            if (!isUserValid(user)) {
                throw UserException("User with login $login is not active")
            }
            return user
        }
        return register(SignUpRq(login, null, TokenType.google))
    }

    private fun findByLoginAndTokenType(login: String, tokenType: TokenType): User? =
        userRepository.findByLoginAndTokenType(login, tokenType)

}
