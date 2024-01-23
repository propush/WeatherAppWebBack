package com.pushkin.weather_app_backend.user.entity

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document
import java.time.ZonedDateTime
import java.util.*

@Document(collection = "user")
class User(
    @Id
    var id: String? = null,
    @Indexed(unique = true)
    val login: String,
    val encryptedPassword: String?,
    var active: Boolean = true,
    val locations: SortedSet<String> = sortedSetOf(),
    val registeredDt: ZonedDateTime = ZonedDateTime.now(),
    val tokenType: TokenType? = null,
) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is User) return false

        if (id != other.id) return false
        if (login != other.login) return false
        if (encryptedPassword != other.encryptedPassword) return false
        if (locations != other.locations) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id?.hashCode() ?: 0
        result = 31 * result + login.hashCode()
        result = 31 * result + encryptedPassword.hashCode()
        result = 31 * result + locations.hashCode()
        return result
    }

    override fun toString(): String {
        return "User(id=$id, login='$login', encryptedPassword='$encryptedPassword', locations=$locations)"
    }

}
