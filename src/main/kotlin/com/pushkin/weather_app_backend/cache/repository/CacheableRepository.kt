package com.pushkin.weather_app_backend.cache.repository

import com.pushkin.weather_app_backend.cache.entity.CacheableEntity
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.data.repository.NoRepositoryBean

@NoRepositoryBean
interface CacheableRepository<T : CacheableEntity> : MongoRepository<T, String>
