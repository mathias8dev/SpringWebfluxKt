package com.mathias8dev.springwebfluxkt.dtos.user

data class AddPostRequestDto(
    val authorId: Long,
    val title: String,
    val content: String
)
