package com.mathias8dev.springwebfluxkt.models

import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDateTime


@Table("comments")
data class Comment(
    @Id val id: Long = 0,
    val postId: Long,
    var comment: String,
    val authorId: Long? = null,
    @CreatedDate
    val createdAt: LocalDateTime = LocalDateTime.now(),
    @LastModifiedDate
    val updatedAt: LocalDateTime = LocalDateTime.now()
) /*: SelfFilteringHandler() {
    override val filterableFields: Collection<String>
        get() = listOf("id", "postId", "comment", "createdAt", "updatedAt")
}*/ {
    companion object {
        fun fromRow(row: Map<String, Any>): Comment {
            return Comment(
                id = row["id"] as Long,
                postId = row["post_id"] as Long,
                comment = row["comment"] as String,
                authorId = row["author_id"] as? Long,
                createdAt = row["created_at"] as LocalDateTime,
                updatedAt = row["updated_at"] as LocalDateTime
            )
        }
    }
}
