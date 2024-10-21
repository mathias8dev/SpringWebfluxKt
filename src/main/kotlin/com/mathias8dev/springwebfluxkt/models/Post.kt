package com.mathias8dev.springwebfluxkt.models

import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDateTime

@Table("posts")
data class Post(
    @Id
    var id: Long = 0,
    var title: String,
    var content: String,
    var tags: String? = null, // The tags are separated by commas
    var published: Boolean = false,
    var authorId: Long = 0,
    @CreatedDate
    var createdAt: LocalDateTime = LocalDateTime.now(),
    @LastModifiedDate
    var updatedAt: LocalDateTime = LocalDateTime.now(),
) {
    /*override val filterableFields: Collection<String>
        get() = listOf("id", "title", "content", "tags", "createdAt", "updatedAt")
*/
    companion object {
        fun fromRow(row: Map<String, Any>): Post {
            return Post(
                id = row["id"] as Long,
                title = row["title"] as String,
                content = row["content"] as String,
                tags = row["tags"] as? String,
                published = (row["published"] as? Boolean) ?: false,
                createdAt = row["created_at"] as LocalDateTime,
                updatedAt = row["updated_at"] as LocalDateTime,
            )
        }
    }
}
