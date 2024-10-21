package com.mathias8dev.springwebfluxkt.dtos.comment

data class CommentRequestDto(
    val postId: Long,
    val comment: String
)
