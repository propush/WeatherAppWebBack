package com.pushkin.weather_app_backend.cache.service

import com.pushkin.weather_app_backend.cache.entity.CacheableEntity

open class EntityCacheConfiguration<T : CacheableEntity>(
    open val ttlSeconds: Long
)
