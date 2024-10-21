package com.mathias8dev.springwebfluxkt.utils

import com.mathias8dev.springwebfluxkt.filtering.FilterCriteria
import com.mathias8dev.springwebfluxkt.filtering.FilterMode
import org.springframework.r2dbc.core.DatabaseClient


fun bindFilterValues(
    querySpec: DatabaseClient.GenericExecuteSpec,
    filterCriteria: List<FilterCriteria>
): DatabaseClient.GenericExecuteSpec {
    var spec = querySpec
    filterCriteria.forEach { criteria ->
        spec = when {
            criteria.filterFnIsMember -> spec.bind(criteria.id, criteria.value as Collection<*>)
            else -> spec.bind(criteria.id, criteria.value)
        }
    }
    return spec
}

fun buildWhereClause(filterCriteria: List<FilterCriteria>, filterMode: FilterMode = FilterMode.AND): String {
    if (filterCriteria.isEmpty()) return ""

    val conditions = filterCriteria.map { criteria ->
        val column = criteria.id.camelToSnakeCase() // Assuming column names are in snake_case
        val value = criteria.value
        when {
            criteria.filterFnIsLessThan -> "$column < $value"
            criteria.filterFnIsLessThanOrEqualTo -> "$column <= $value"
            criteria.filterFnIsGreaterThan -> "$column > $value"
            criteria.filterFnIsGreaterThanOrEqualTo -> "$column >= $value"
            criteria.filterFnIsEquals -> "LOWER($column) = LOWER('${escapeValue(value).lowercased()}')" // Ensure proper escaping
            criteria.filterFnIsLike -> "LOWER($column) LIKE '%${escapeValue(value).lowercased()}%'" // Properly escape the value
            criteria.filterFnIsFuzzy -> "LOWER($column) LIKE '%${escapeValue(value).lowercased()}%'" // Fuzzy search with LIKE
            criteria.filterFnIsMember -> "$column IN (${(value as? List<*>)?.joinToString(",") { "'${escapeValue(it)}'" } ?: value})" // Handle IN clause for list values
            else -> "$column = $value"
        }
    }

    return "WHERE " + conditions.joinToString(" ${filterMode.name} ")
}

// Utility function to escape values properly
fun escapeValue(value: Any?): String {
    return when (value) {
        is String -> value.replace("'", "''") // Escape single quotes
        else -> value.toString() // For other types, just convert to string
    }
}
