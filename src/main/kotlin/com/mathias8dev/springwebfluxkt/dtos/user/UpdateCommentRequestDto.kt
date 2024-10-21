package com.mathias8dev.springwebfluxkt.dtos.user

data class UpdateCommentRequestDto(
    val id: Long,
    val authorId: Long,
    val content: String
)
