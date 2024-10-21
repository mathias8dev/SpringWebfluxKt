package com.mathias8dev.springwebfluxkt.models

import com.mathias8dev.springwebfluxkt.filtering.SelfFilteringHandler
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDateTime


@Table("comments")
data class Comment(
    @Id val id: Long = 0,
    val postId: Long,
    val comment: String,
    @CreatedDate
    val createdAt: LocalDateTime = LocalDateTime.now(),
    @LastModifiedDate
    val updatedAt: LocalDateTime = LocalDateTime.now()
) : SelfFilteringHandler() {
    override val filterableFields: Collection<String>
        get() = listOf("id", "postId", "comment", "createdAt", "updatedAt")
}
