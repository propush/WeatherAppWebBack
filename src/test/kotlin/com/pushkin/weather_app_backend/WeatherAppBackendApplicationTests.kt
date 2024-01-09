package com.pushkin.weather_app_backend

import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit.jupiter.EnabledIf
import org.testcontainers.containers.MongoDBContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.utility.DockerImageName

@SpringBootTest
@EnabledIf(value = "#{'\${spring.profiles.active}' == 'test'}", loadContext = true)
class WeatherAppBackendApplicationTests {


    @Test
    fun contextLoads() {
    }

}
