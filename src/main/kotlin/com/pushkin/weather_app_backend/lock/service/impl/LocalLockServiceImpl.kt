package com.pushkin.weather_app_backend.lock.service.impl

import com.pushkin.weather_app_backend.lock.service.LockService
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Service
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicLong
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

private val logger = KotlinLogging.logger {}

data class LockCacheEntry(
    val lock: ReentrantLock,
    val counter: AtomicLong
)

@Service
class LocalLockServiceImpl : LockService {

    internal val locks = ConcurrentHashMap<String, LockCacheEntry>()

    override fun <T> lockWith(lockName: String, block: () -> T): T {
        logger.trace { "Locking $lockName" }
        val lockCacheEntry = locks.getOrPut(lockName) {
            logger.trace { "Creating new lock: $lockName" }
            LockCacheEntry(ReentrantLock(), AtomicLong(0))
        }
        val counter = lockCacheEntry.counter.incrementAndGet()
        logger.trace { "Checking lock: $lockName, counter: $counter" }
        return lockCacheEntry.lock.withLock {
            try {
                logger.trace { "Lock acquired: $lockName" }
                block()
            } catch (e: Exception) {
                logger.error(e) { "Error while locking $lockName: $e" }
                throw e
            } finally {
                logger.trace { "Lock released: $lockName" }
                if (lockCacheEntry.counter.decrementAndGet() == 0L) {
                    logger.trace { "Removing lock: $lockName" }
                    locks.remove(lockName)
                }
            }
        }
    }

}
