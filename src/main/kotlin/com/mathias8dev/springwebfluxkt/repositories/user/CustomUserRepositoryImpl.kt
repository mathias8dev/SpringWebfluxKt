package com.mathias8dev.springwebfluxkt.repositories.user

import com.mathias8dev.springwebfluxkt.dtos.user.AddCommentRequestDto
import com.mathias8dev.springwebfluxkt.models.Comment
import kotlinx.coroutines.reactive.awaitFirst
import org.springframework.r2dbc.core.DatabaseClient

class CustomUserRepositoryImpl(
    private val databaseClient: DatabaseClient
) : CustomUserRepository {
    override suspend fun addComment(dto: AddCommentRequestDto): Comment {
        val query = """
            INSERT INTO comments (author_id, post_id, content)
            VALUES (:userId, :content)
            RETURNING id, author_id, post_id, content, created_at, updated_at
        """.trimIndent()

        return databaseClient.sql(query)
            .bind("authorId", dto.authorId)
            .bind("postId", dto.postId)
            .bind("content", dto.content)
            .fetch()
            .one()
            .map(Comment::fromRow)
            .awaitFirst()
    }
}