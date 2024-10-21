package com.mathias8dev.springwebfluxkt.services

import com.mathias8dev.springwebfluxkt.dtos.comment.CommentRequestDto
import com.mathias8dev.springwebfluxkt.models.Comment
import com.mathias8dev.springwebfluxkt.repositories.comment.CommentRepository
import org.springframework.stereotype.Service


@Service
class CommentService(
    private val commentRepository: CommentRepository
) {

    suspend fun insert(dto: CommentRequestDto): Comment {
        val comment = Comment(postId = dto.postId, comment = dto.comment)
        return commentRepository.save(comment)
    }
}