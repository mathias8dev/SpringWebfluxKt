package com.mathias8dev.springwebfluxkt.dtos.user

data class AddCommentRequestDto(
    val authorId: Long,
    val postId: Long,
    val content: String
)
