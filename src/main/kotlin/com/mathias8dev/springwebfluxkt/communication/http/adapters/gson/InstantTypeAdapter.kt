package com.mathias8dev.springwebfluxkt.communication.http.adapters.gson

import com.google.gson.*
import com.mathias8dev.springwebfluxkt.utils.tryOrNull
import java.lang.reflect.Type
import java.time.Instant
import java.time.ZoneOffset
import java.util.*


class InstantTypeAdapter : JsonSerializer<Instant?>, JsonDeserializer<Instant?> {

    override fun serialize(
        instant: Instant?,
        srcType: Type?,
        context: JsonSerializationContext?
    ): JsonElement? {
        return instant?.let { JsonPrimitive(it.toEpochMilli()) }
    }

    @Throws(JsonParseException::class)
    override fun deserialize(
        json: JsonElement?,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): Instant? {
        return json?.asString?.let { instantString ->
            instantString.asLocalDateTime()?.toInstant(ZoneOffset.UTC)?.let {
                tryOrNull {
                    instantString.toLongOrNull()?.let { instantMills ->
                        Instant.ofEpochMilli(instantMills)
                    }
                }
            }

        }
    }

}