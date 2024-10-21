package com.mathias8dev.springwebfluxkt.services

import com.mathias8dev.springwebfluxkt.dtos.comment.CommentRequestDto
import com.mathias8dev.springwebfluxkt.exceptions.HttpException
import com.mathias8dev.springwebfluxkt.models.Comment
import com.mathias8dev.springwebfluxkt.repositories.comment.CommentRepository
import com.mathias8dev.springwebfluxkt.utils.otherwise
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional


@Service
@Transactional
class CommentService(
    private val commentRepository: CommentRepository
) {

    suspend fun findById(id: Long) = commentRepository.findById(id).otherwise {
        throw HttpException(HttpStatus.BAD_REQUEST, "The comment with id $id was not found")
    }

    suspend fun insert(dto: CommentRequestDto): Comment {
        val comment = Comment(postId = dto.postId!!, comment = dto.comment)
        return commentRepository.save(comment)
    }

    suspend fun update(dto: CommentRequestDto): Comment {
        val comment = findById(dto.id!!)
        dto.comment.let {
            comment.comment = it
        }
        return commentRepository.save(comment)
    }

    suspend fun delete(id: Long): Comment {
        val comment = findById(id)
        commentRepository.delete(comment)
        return comment
    }
}