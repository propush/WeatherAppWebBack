package com.pushkin.weather_app_backend.lock.service.impl

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.lang.Thread.sleep
import kotlin.concurrent.thread

class LocalLockServiceImplTest {

    private lateinit var localLockServiceImpl: LocalLockServiceImpl

    @BeforeEach
    fun setUp() {
        localLockServiceImpl = LocalLockServiceImpl()
    }

    @Test
    fun lockWithConcurrent() {
        var counter = 0
        val threads = mutableListOf<Thread>()
        repeat(3000) {
            threads.add(
                thread {
                    sleep((Math.random() * 5).toLong())
                    localLockServiceImpl.lockWith("test2") {
                        sleep((Math.random() * 5).toLong())
                        counter = counter.inc()
                    }
                }
            )
        }
        println("Waiting for threads to finish, locks: ${localLockServiceImpl.locks}")
        threads.forEach(Thread::join)
        assertEquals(3000, counter)
        assertEquals(0, localLockServiceImpl.locks.size)
    }

    @Test
    fun lockWithException() {
        assertThrows<IllegalStateException> {
            localLockServiceImpl.lockWith("test3") {
                println("test3")
                throw IllegalStateException("test3")
            }
        }
        assertEquals(0, localLockServiceImpl.locks.size)
    }

}
