package com.mathias8dev.springwebfluxkt.communication.http.adapters.gson

import java.time.*
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeFormatterBuilder
import java.time.temporal.ChronoField
import java.util.*


fun String.parseLocalTime(format: TimeFormat, locale: Locale = Locale.FRANCE): LocalTime =
    LocalTime.parse(this, DateTimeFormatter.ofPattern(format.format, locale))

fun String.parseLocalTimeOrNull(format: TimeFormat, locale: Locale = Locale.FRANCE): LocalTime? = kotlin.runCatching {
    this.parseLocalTime(format, locale)
}.getOrNull()

fun String.parseLocalDate(format: DateFormat, locale: Locale = Locale.FRANCE): LocalDate {
    val formatter = DateTimeFormatterBuilder()
        .append(DateTimeFormatter.ofPattern(format.format, locale))
        .parseDefaulting(ChronoField.DAY_OF_MONTH, 1)
        .toFormatter(locale)

    return LocalDate.parse(this, formatter)
}

fun String.parseLocalDateOrNull(format: DateFormat, locale: Locale = Locale.FRANCE): LocalDate? = kotlin.runCatching {
    this.parseLocalDate(format, locale)
}.getOrNull()

fun String.parseLocalDateTime(
    format: DateTimeFormat,
    locale: Locale = Locale.FRANCE
): LocalDateTime =
    LocalDateTime.parse(this, DateTimeFormatter.ofPattern(format.format, locale))

fun String.parseLocalDateTimeOrNull(
    format: DateTimeFormat,
    locale: Locale = Locale.FRANCE
): LocalDateTime? {
    return kotlin.runCatching {
        this.parseLocalDateTime(format, locale)
    }.getOrNull()
}

fun String?.asLocalDateTime(): LocalDateTime? = this.genericParseLocalDateTime() ?: this.genericParseLocalDate()?.atStartOfDay()
fun String?.asLocalDate(): LocalDate? = this.genericParseLocalDateTime()?.toLocalDate() ?: this.genericParseLocalDate()


fun String?.genericParseLocalDate(): LocalDate? {
    return this?.let { dateString ->
        kotlin.runCatching {
            LocalDate.parse(dateString, DateTimeFormatter.ISO_LOCAL_DATE)
        }.getOrElse {
            kotlin.runCatching {
                LocalDate.ofInstant(Instant.ofEpochMilli(Date.parse(dateString)), ZoneId.systemDefault())
            }.getOrElse {
                for (format in DateFormat.entries) {
                    dateString.parseLocalDateOrNull(format).let { parsedDate ->
                        if (parsedDate != null) return parsedDate
                    }

                }
                null
            }
        }
    }
}

fun String?.genericParseLocalDateTime(): LocalDateTime? {
    return this?.let { dateString ->
        kotlin.runCatching {
            LocalDateTime.parse(dateString, DateTimeFormatter.ISO_LOCAL_DATE_TIME)
        }.getOrElse {
            kotlin.runCatching {
                LocalDateTime.ofInstant(Instant.ofEpochMilli(Date.parse(dateString)), ZoneId.systemDefault())
            }.getOrElse {
                for (format in DateTimeFormat.entries) {
                    dateString.parseLocalDateTimeOrNull(format).let { parsedDateTime ->
                        if (parsedDateTime != null) return parsedDateTime
                    }

                }
                null
            }
        }
    }
}


interface DateTimePrintFormat {
    val format: String
}

enum class DateTimeFormat(override val format: String) : DateTimePrintFormat {
    FMT_DATE_TIME("yyyy-MM-dd'T'HH:mm:ss"),
    FMT_DATE_TIME_ZONED("yyyy-MM-dd'T'HH:mm:ss'Z'"),
    FMT_DATE_TIME_MILLIS_TIME_ZONED("yyyy-MM-dd'T'HH:mm:ss.SSSXXX"),
    FMT_DATE_TIME_MILLIS("yyyy-MM-dd'T'HH:mm:ss.SSS"),
    FMT_DATE_TIME_MILLIS_2("yyyy-MM-dd'T'HH:mm:ss.S"),
    FMT_DATE_TIME_X("yyyy-MM-dd'T'HH:mm:ssX"),
    FMT_DATE_TIME_ISO_CACHING("EEE, dd MMM yyyy HH:mm:ss z"),
    FMT_DATE_TIME_PRETTY("EEEE dd MMMM HH'H'mm"),
    FMT_DATE_TIME_PRETTY_NO_DAY("dd MMM YYYY, HH'h'mm"),
    FMT_DATE_TIME_MAINTENANCE("dd/MM/yyyy HH:mm"),
    FMT_DATE_TIME_ACCORDS("yyyy-MM-dd HH:mm:ss"),
    FMT_DATE_TIME_ERL("yyyy-MM-dd'T'HH:mm:ssXXX"),
    FMT_DATE_TIME_DMYHM("dd-MM-yyyy HH:mm:ss"),
    FMT_DATE_TIME_T("yyyy-MM-dd'T'HH:mm:ss"),
    FMT_DATE_TIME_PRETTY_SS("dd MMM YYYY, HH':'mm")
}

enum class DateFormat(override val format: String) : DateTimePrintFormat {
    FMT_DATE("yyyy-MM-dd"),
    FMT_DATE_PRETTY("EEEE dd MMMM"),
    FMT_DATE_PRETTY_DAY_MONTH("EEEE d MMMM"),
    FMT_DATE_DAY_MONTH("d MMM"),
    FMT_DATE_DAY_FULL_MONTH("d MMMM"),
    FMT_DATE_DAY_MONTH_YEAR("dd MMMM yyyy"),
    FMT_DATE_FULL_DAY_MONTH_YEAR("EEEE dd MMMM yyyy"),
    FMT_DATE_DAY_MONTH_YEAR_NO_ZERO("d MMMM yyyy"),
    FMT_DATE_DAY_MONTH_SHORT_YEAR("dd MMM yyyy"),
    FMT_DATE_DAY_MONTH_YEAR_SHORT("d MMM yyyy"),
    FMT_DATE_DAY_MONTH_YEAR_SHORT_LINE_BREAK("d MMM\nyyyy"),
    FMT_DATE_MONTH("MMMM yyyy"),
    FMT_DATE_MONTH_SHORT("MMM yyyy"),
    FMT_DATE_MONTH_WITHOUT_YEAR("MMMM"),
    FMT_DATE_YEAR("yyyy"),
    FMT_DATE_WITH_SLASH("dd/MM/yyyy"),
    FMT_DAY_MONTH("dd MMM"),
    FMT_DAY_FULL_MONTH("dd MMMM"),
    FMT_MONTH("MMM"),
    FMT_DAY_FIRST_LETTER("EEEEE"),
    FMT_DAY_NAME_NUMBER("EEEE dd"),
    FMT_DAY_NAME_NUMBER2("EEEE d"),
    FMT_DAY_NAME_MONTH_NUMBER("EEEE dd/MM"),
    FMT_DAY_NAME("EEEE"),
    FMT_DAY_NAME_THREE("EEE"),
    FMT_DATE_WITHOUT_DAY("yyyy-MM"),
    FMT_EXPIRY_DATE("MM/yy"),
    FMT_ATOS_WALLET("E MMM dd HH:mm:ss ZZZZ y"),
    FMT_ATOS_EXPIRY_DATE("YYYYMM"),
    FMT_DATE_COMPLETE("EEEE d MMMM yyyy"),
    FMT_DATE_SHORT_DAY_MONTH("EEE. d MMM yyyy"),
}

enum class TimeFormat(override val format: String) : DateTimePrintFormat {
    FMT_TIME("HH:mm:ss"),
    FMT_HOURS_MIN_PRETTY("H'h'mm"),
    FMT_HOURS_PRETTY("HH'h'mm"),
    FMT_HOURS_MIN("HH:mm"),
    FMT_HOURS("H'h'"),
    FMT_HOURS_WITH_ZERO("HH"),
}