package com.mathias8dev.springwebfluxkt.communication.http.adapters.gson

import com.google.gson.*
import java.lang.reflect.Type
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter


class ZonedDateTimeTypeAdapter : JsonSerializer<ZonedDateTime?>, JsonDeserializer<ZonedDateTime?> {
    override fun serialize(
        zonedDateTime: ZonedDateTime?,
        srcType: Type?,
        context: JsonSerializationContext?
    ): JsonElement? {
        return zonedDateTime?.let { JsonPrimitive(formatter.format(it)) }
    }

    @Throws(JsonParseException::class)
    override fun deserialize(
        json: JsonElement?,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): ZonedDateTime? {
        return json?.let { ZonedDateTime.parse(it.asString, formatter) }
    }

    companion object {
        private val formatter: DateTimeFormatter = DateTimeFormatter.ISO_ZONED_DATE_TIME
    }
}