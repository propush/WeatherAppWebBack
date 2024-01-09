package com.pushkin.weather_app_backend.cache.service

import com.pushkin.weather_app_backend.cache.entity.CacheableEntity
import com.pushkin.weather_app_backend.cache.exception.CachingException
import com.pushkin.weather_app_backend.cache.repository.CacheableRepository
import com.pushkin.weather_app_backend.lock.service.LockService
import io.github.oshai.kotlinlogging.KotlinLogging
import java.time.ZonedDateTime

private val logger = KotlinLogging.logger {}

abstract class BaseEntityCachingServiceImpl<T : CacheableEntity, U, R : CacheableRepository<T>>(
    protected val repository: R,
    protected val entityCacheConfiguration: EntityCacheConfiguration<T>,
    private val lockService: LockService
) : EntityCachingService<T, U, R> {

    override fun getEntity(key: U, fetcher: ((U) -> T)?): T {
        logger.debug { "getEntity: key=$key" }
        return lockService.lockWith("getEntity:$key") {
            getEntityUnsafe(key, fetcher)
        }
    }

    private fun getEntityUnsafe(key: U, fetcher: ((U) -> T)?): T {
        val cachedEntity = findInRepository(key)
        logger.debug { "getEntity: cachedEntity=$cachedEntity" }
        return if (cachedEntity != null && !evict(cachedEntity)) {
            logger.debug { "getEntity: cachedEntity is valid" }
            cachedEntity
        } else {
            logger.debug { "getEntity: cachedEntity is NOT valid" }
            if (fetcher != null) {
                logger.info { "getEntity: fetching entity by key $key" }
                val newEntity = fetcher(key)
                repository
                    .save(newEntity)
                    .also { logger.debug { "getEntity: saved new fetched entity: $it" } }
            } else {
                throw CachingException("Entity not found in cache and fetcher is null")
            }
        }
    }

    private fun evict(cachedEntity: T): Boolean {
        if (isExpired(cachedEntity)) {
            logger.info { "evict: cachedEntity is expired, deleting. id: ${cachedEntity.id}" }
            repository.delete(cachedEntity)
            return true
        }
        return false
    }

    private fun isExpired(cachedEntity: T): Boolean =
        cachedEntity
            .cacheDt
            .plusSeconds(entityCacheConfiguration.ttlSeconds)
            .isBefore(ZonedDateTime.now())

}
