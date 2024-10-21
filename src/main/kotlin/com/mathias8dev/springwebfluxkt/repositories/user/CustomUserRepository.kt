package com.mathias8dev.springwebfluxkt.repositories.user

import com.mathias8dev.springwebfluxkt.dtos.user.AddCommentRequestDto
import com.mathias8dev.springwebfluxkt.models.Comment

interface CustomUserRepository {
    suspend fun addComment(dto: AddCommentRequestDto): Comment
}