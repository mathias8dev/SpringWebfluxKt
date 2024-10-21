package com.mathias8dev.springwebfluxkt.dtos.post

import com.fasterxml.jackson.annotation.JsonInclude
import com.mathias8dev.springwebfluxkt.dtos.category.CategoryResponseDto
import com.mathias8dev.springwebfluxkt.dtos.comment.CommentResponseDto
import reactor.core.publisher.Mono
import java.time.LocalDateTime

@JsonInclude(JsonInclude.Include.NON_NULL)
data class PostResponseDto(
    val id: Long,
    val title: String,
    val content: String,
    val published: Boolean = false,
    val tags: List<String>? = null,
    val categories: List<CategoryResponseDto>? = null,
    val comments: List<CommentResponseDto>? = null,
    val createdAt: LocalDateTime? = null,
    val updatedAt: LocalDateTime? = null
) {
    companion object {
        fun fromRow(row: Map<String, Any?>): PostResponseDto {
            return PostResponseDto(
                id = row["id"] as Long,
                title = row["title"] as String,
                content = row["content"] as String,
                tags = (row["tags"] as? String)?.split(",")?.toMutableList() ?: mutableListOf(),
                published = (row["published"] as? Boolean) ?: false,
                createdAt = row["created_at"] as LocalDateTime,
                updatedAt = row["updated_at"] as LocalDateTime
            )
        }

        fun fromRows(rows: List<Map<String, Any?>>): Mono<PostResponseDto> {
            return Mono.just(
                PostResponseDto(
                    id = rows[0]["post_id"] as Long,
                    title = rows[0]["title"] as String,
                    content = rows[0]["content"] as String,
                    tags = (rows[0]["tags"] as? String)?.split(",")?.toMutableList() ?: mutableListOf(),
                    published = (rows[0]["published"] as? Boolean) ?: false,
                    categories = rows.stream()
                        .filter { it["category_id"] != null }
                        .map(CategoryResponseDto::fromRowPrefixed)
                        .toList().toMutableList(),
                    comments = rows.stream()
                        .filter { it["comment_id"] != null }
                        .map(CommentResponseDto::fromRowPrefixed)
                        .toList().toMutableList(),
                    createdAt = rows[0]["created_at"] as LocalDateTime,
                    updatedAt = rows[0]["updated_at"] as LocalDateTime
                )
            )
        }
    }
}
