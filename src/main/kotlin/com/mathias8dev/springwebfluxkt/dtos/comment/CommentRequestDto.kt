package com.mathias8dev.springwebfluxkt.dtos.comment

data class CommentRequestDto(
    val id: Long? = null,
    val postId: Long? = null,
    val comment: String
)
