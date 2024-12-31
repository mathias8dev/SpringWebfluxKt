package com.mathias8dev.springwebfluxkt.communication.http

class ApiRequestException(
    val httpStatusCode: Int,
    override val message: String?,
    override val cause: Throwable? = null
) : RuntimeException(message, cause)