package com.mathias8dev.springwebfluxkt.dtos.user

data class UpdatePostRequestDto(
    val id: Long,
    val authorId: Long,
    val title: String? = null,
    val content: String? = null
)
