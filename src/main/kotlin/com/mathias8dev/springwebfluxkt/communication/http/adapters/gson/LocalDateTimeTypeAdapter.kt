package com.mathias8dev.springwebfluxkt.communication.http.adapters.gson

import com.google.gson.*
import java.lang.reflect.Type
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*


class LocalDateTimeTypeAdapter : JsonSerializer<LocalDateTime?>, JsonDeserializer<LocalDateTime?> {

    override fun serialize(
        localDateTime: LocalDateTime?,
        srcType: Type?,
        context: JsonSerializationContext?
    ): JsonElement? {
        return localDateTime?.let { JsonPrimitive(formatter.format(it)) }
    }

    @Throws(JsonParseException::class)
    override fun deserialize(
        json: JsonElement?,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): LocalDateTime? {
        return json?.asString?.asLocalDateTime()
    }

    companion object {
        private val formatter: DateTimeFormatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME
    }
}