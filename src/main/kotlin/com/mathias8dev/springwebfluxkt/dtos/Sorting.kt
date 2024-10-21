package com.mathias8dev.springwebfluxkt.dtos

import org.springframework.data.domain.Sort


data class Sorting(val key: String, val direction: Direction) {

    enum class Direction {
        ASC,
        DESC, ;

        val isAsc: Boolean
            get() = this == ASC

        val isDesc: Boolean
            get() = this == DESC


        fun toSortDirection(): Sort.Direction = when (this) {
            ASC -> Sort.Direction.ASC
            else -> Sort.Direction.DESC
        }
    }

    fun toSort(): Sort {
        return Sort.by(
            this.direction.toSortDirection(),
            this.key
        )
    }

    companion object {
        fun default(): Sorting = Sorting("createdAt", Direction.DESC)
    }
}


fun Collection<Sorting>.toSort(): Sort {
    val orders = this.map {
        if (it.direction.isAsc) Sort.Order.asc(it.key)
        else Sort.Order.desc(it.key)
    }.toTypedArray()

    return Sort.by(*orders)
}