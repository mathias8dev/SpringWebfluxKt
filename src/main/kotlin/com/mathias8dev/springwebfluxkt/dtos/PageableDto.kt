package com.mathias8dev.springwebfluxkt.dtos

import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable

data class PageableDto(
    val page: Int = 0,
    val pageSize: Int = 20,
    val sorting: List<Sorting> = listOf(Sorting.default())
)


fun PageableDto.toPageable(): Pageable = PageRequest.of(
    this.page,
    this.pageSize,
    this.sorting.toSort()
)

