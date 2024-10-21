package com.mathias8dev.springwebfluxkt.models

import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDateTime


@Table("users")
data class User(
    @Id val id: Long = 0,
    var username: String,
    var email: String? = null,
    var firstname: String? = null,
    var lastname: String? = null,
    @CreatedDate
    val createdAt: LocalDateTime = LocalDateTime.now(),
    @LastModifiedDate
    val updatedAt: LocalDateTime = LocalDateTime.now()
)
