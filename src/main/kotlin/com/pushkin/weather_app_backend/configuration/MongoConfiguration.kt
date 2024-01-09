package com.pushkin.weather_app_backend.configuration

import com.pushkin.weather_app_backend.db.ZonedDateTimeReadConverter
import com.pushkin.weather_app_backend.db.ZonedDateTimeWriteConverter
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.mongodb.core.convert.MongoCustomConversions

@Configuration
class MongoConfiguration {

    @Bean
    fun customConversions(): MongoCustomConversions =
        MongoCustomConversions(
            listOf(
                ZonedDateTimeWriteConverter(),
                ZonedDateTimeReadConverter()
            )
        )


}
