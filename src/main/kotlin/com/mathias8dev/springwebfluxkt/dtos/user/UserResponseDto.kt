package com.mathias8dev.springwebfluxkt.dtos.user

import java.time.LocalDateTime

data class UserResponseDto(
    val id: Long,
    val username: String,
    val firstname: String?,
    val lastname: String?,
    val email: String,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
)
