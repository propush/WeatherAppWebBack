package com.pushkin.weather_app_backend.cache.entity

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.index.Indexed
import java.time.ZonedDateTime

open class CacheableEntity {

    @Id
    @Indexed(unique = true)
    var id: String? = null
    var cacheDt: ZonedDateTime = ZonedDateTime.now()

    override fun toString(): String {
        return "CacheableEntity(id=$id, cacheDt=$cacheDt)"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is CacheableEntity) return false

        if (id != other.id) return false
        if (cacheDt != other.cacheDt) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id?.hashCode() ?: 0
        result = 31 * result + cacheDt.hashCode()
        return result
    }

}
