package com.mathias8dev.springwebfluxkt.filtering


interface SelfFilter {
    fun applyFilter(filterCriteria: FilterCriteria): Boolean

    companion object {
        val dateFields = listOf(
            "createdAt",
            "updatedAt",
            "sentAt",
            "arrivedAt",
            "receivedAt",
            "orderedAt",
            "confirmedAt"
        )
    }
}

