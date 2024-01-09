package com.pushkin.weather_app_backend.db

import org.springframework.core.convert.converter.Converter
import org.springframework.data.convert.ReadingConverter
import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.util.*

@ReadingConverter
open class ZonedDateTimeReadConverter : Converter<Date, ZonedDateTime> {

    override fun convert(source: Date): ZonedDateTime? =
        source.toInstant().atZone(ZoneOffset.UTC)

}
