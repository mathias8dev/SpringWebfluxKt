package com.mathias8dev.springwebfluxkt.dtos.user

data class UserRequestDto(
    val id: Long? = null,
    val username: String? = null,
    val email: String? = null,
    val firstname: String? = null,
    val lastname: String? = null
)
