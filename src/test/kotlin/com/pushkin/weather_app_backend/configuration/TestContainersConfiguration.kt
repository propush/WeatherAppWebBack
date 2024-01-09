package com.pushkin.weather_app_backend.configuration

import org.springframework.context.annotation.Configuration
import org.testcontainers.containers.MongoDBContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.utility.DockerImageName

@Configuration
class TestContainersConfiguration {

    companion object {

        @JvmStatic
        @Container
        private val mongoDBContainer: MongoDBContainer =
            MongoDBContainer(DockerImageName.parse("mongo:latest"))
                .withExposedPorts(1234)
                .apply {
                    portBindings = listOf("1234:27017")
                    start()
                }

    }

}
