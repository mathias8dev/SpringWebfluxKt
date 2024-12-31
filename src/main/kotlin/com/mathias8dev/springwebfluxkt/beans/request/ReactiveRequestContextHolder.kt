package com.mathias8dev.springwebfluxkt.beans.request

import kotlinx.coroutines.reactor.awaitSingle
import org.springframework.http.server.reactive.ServerHttpRequest
import reactor.core.publisher.Mono
import reactor.util.context.ContextView


class ReactiveRequestContextHolder {

    companion object {
        val CONTEXT_KEY = ServerHttpRequest::class.java

        suspend fun get(): ServerHttpRequest? {
            return Mono.deferContextual { context: ContextView ->
                val request = context.getOrDefault<ServerHttpRequest>(CONTEXT_KEY, null)
                Mono.justOrEmpty(request)
            }.awaitSingle()
        }
    }
}