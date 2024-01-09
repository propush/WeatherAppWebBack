package com.pushkin.weather_app_backend.lock.service

interface LockService {

    fun <T> lockWith(lockName: String, block: () -> T): T

}
