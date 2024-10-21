package com.mathias8dev.springwebfluxkt.dtos.post

data class PostRequestDto(
    val title: String?,
    val content: String?,
    val categoryId: Long? = null,
    val tags: List<String>? = null
)
