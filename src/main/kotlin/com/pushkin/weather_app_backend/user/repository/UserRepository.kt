package com.pushkin.weather_app_backend.user.repository

import com.pushkin.weather_app_backend.user.entity.TokenType
import com.pushkin.weather_app_backend.user.entity.User
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository

@Repository
interface UserRepository : MongoRepository<User, String> {

    fun findByLogin(login: String): User?

    fun findByLoginAndTokenType(login: String, tokenType: TokenType?): User?

}
