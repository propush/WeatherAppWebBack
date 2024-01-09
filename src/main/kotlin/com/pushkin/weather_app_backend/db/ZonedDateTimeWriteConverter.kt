package com.pushkin.weather_app_backend.db

import org.springframework.core.convert.converter.Converter
import org.springframework.data.convert.WritingConverter
import java.time.ZonedDateTime
import java.util.*

@WritingConverter
open class ZonedDateTimeWriteConverter : Converter<ZonedDateTime, Date> {

    override fun convert(source: ZonedDateTime): Date? =
        Date.from(source.toInstant())

}
