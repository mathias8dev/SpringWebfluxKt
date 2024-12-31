package com.mathias8dev.springwebfluxkt.utils

import com.fasterxml.jackson.core.type.TypeReference
import com.mathias8dev.springwebfluxkt.dtos.Sorting
import com.mathias8dev.springwebfluxkt.dtos.toSort
import com.mathias8dev.springwebfluxkt.filtering.FilterCriteria
import com.mathias8dev.springwebfluxkt.filtering.FilterMode
import org.springframework.data.domain.Sort
import java.util.*


fun <T> T?.otherwise(value: T): T = this ?: value

inline fun <T> T?.otherwise(block: () -> T): T = this ?: block()

fun <T> T?.isNotNull(): Boolean = this != null
fun <T> T?.isNull(): Boolean = this == null

inline fun <T> tryOrNull(block: () -> T): T? = runCatching {
    block()
}.getOrNull()


fun String.camelToSnakeCase(): String {
    return this.replace(Regex("([a-z])([A-Z]+)"), "$1_$2").lowercased()
}

fun String.lowercased(): String = this.lowercase(Locale.getDefault())

fun <T> T.toJson(): String = JacksonUtils.objectMapper().writeValueAsString(this)
inline fun <reified T> String.fromJson(): T =
    JacksonUtils.objectMapper().readValue(this, object : TypeReference<T>() {})

inline fun <reified T, reified V> T.toTypedData(): V {
    return this.toJson().fromJson()
}

inline fun <reified T, reified V> T.convert(): V {
    return this.toJson().fromJson()
}

fun String.toSort(): Sort {
    this.split(",").map {
        val (key, direction) = it.split(":")
        Sorting(key, Sorting.Direction.valueOf(direction.uppercase(Locale.getDefault())))
    }.let {
        return it.toSort()
    }
}

fun String.toFilterCriteria(): List<FilterCriteria> {
    return this.split(",").map {
        val split = it.split(":")
        FilterCriteria(
            id = split[0],
            filterFn = split[1],
            value = split[2].toTyped(),
        )
    }
}

fun String.toTyped(): Any {
    return when {
        this == "true" || this == "false" -> this.toBoolean()
        this.toIntOrNull() != null -> this.toInt()
        this.toDoubleOrNull() != null -> this.toDouble()
        this.toLongOrNull() != null -> this.toLong()
        else -> this
    }
}

fun String.toFilterMode(): FilterMode {
    return FilterMode.valueOf(this.uppercase(Locale.getDefault()))
}

infix fun Any?.unless(condition: Boolean): Any? {
    return if (condition) this else null
}