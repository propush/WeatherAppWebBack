package com.pushkin.weather_app_backend.weather.entity

import com.pushkin.weather_app_backend.cache.entity.CacheableEntity
import com.pushkin.weather_app_backend.weather.vo.GeoResponse
import org.springframework.data.mongodb.core.mapping.Document

@Document(collection = "geodata")
class GeoDataCached(
    val location: String,
    val geoResponses: Set<GeoResponse>,
) : CacheableEntity() {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is GeoDataCached) return false
        if (!super.equals(other)) return false

        if (location != other.location) return false
        if (geoResponses != other.geoResponses) return false

        return true
    }

    override fun hashCode(): Int {
        var result = super.hashCode()
        result = 31 * result + location.hashCode()
        result = 31 * result + geoResponses.hashCode()
        return result
    }

    override fun toString(): String {
        return "GeoDataCached(location='$location', geoResponses=$geoResponses) ${super.toString()}"
    }

}
