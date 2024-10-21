package com.mathias8dev.springwebfluxkt.models

import com.mathias8dev.springwebfluxkt.filtering.SelfFilteringHandler
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table


@Table("categories")
data class Category(
    @Id
    var id: Long = 0,
    var name: String
) : SelfFilteringHandler() {
    override val filterableFields: Collection<String>
        get() = listOf("id", "name")
}