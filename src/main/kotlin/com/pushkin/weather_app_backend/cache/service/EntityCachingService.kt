package com.pushkin.weather_app_backend.cache.service

import com.pushkin.weather_app_backend.cache.entity.CacheableEntity
import com.pushkin.weather_app_backend.cache.exception.CachingException
import com.pushkin.weather_app_backend.cache.repository.CacheableRepository

interface EntityCachingService<T : CacheableEntity, U, R : CacheableRepository<T>> {

    @Throws(CachingException::class)
    fun getEntity(key: U, fetcher: ((U) -> T)?): T

    fun findInRepository(key: U): T?

}
