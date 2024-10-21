package com.mathias8dev.springwebfluxkt.dtos.comment

import com.fasterxml.jackson.annotation.JsonInclude
import com.mathias8dev.springwebfluxkt.dtos.post.PostResponseDto
import java.time.LocalDateTime

@JsonInclude(JsonInclude.Include.NON_NULL)
data class CommentResponseDto(
    var id: Long,
    var comment: String,
    var posts: List<PostResponseDto>? = null,
    var createdAt: LocalDateTime? = null,
    var updatedAt: LocalDateTime? = null
) {

    companion object {
        fun fromRow(row: Map<String, Any?>): CommentResponseDto {
            return CommentResponseDto(
                id = row["id"] as Long,
                comment = row["comment"] as String,
                createdAt = row["created_at"] as LocalDateTime,
                updatedAt = row["updated_at"] as LocalDateTime
            )
        }

        fun fromRowPrefixed(row: Map<String, Any?>): CommentResponseDto {
            return CommentResponseDto(
                id = row["comment_id"] as Long,
                comment = row["comment_comment"] as String,
                createdAt = row["comment_created_at"] as LocalDateTime,
                updatedAt = row["comment_updated_at"] as LocalDateTime
            )
        }
    }
}
