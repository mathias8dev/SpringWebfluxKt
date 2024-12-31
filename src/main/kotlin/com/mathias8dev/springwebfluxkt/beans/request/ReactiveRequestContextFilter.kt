package com.mathias8dev.springwebfluxkt.beans.request

import org.springframework.stereotype.Component
import org.springframework.web.server.ServerWebExchange
import org.springframework.web.server.WebFilter
import org.springframework.web.server.WebFilterChain
import reactor.core.publisher.Mono


@Component
class ReactiveRequestContextFilter : WebFilter {

    override fun filter(exchange: ServerWebExchange, chain: WebFilterChain): Mono<Void> {
        val request = exchange.request
        return chain.filter(exchange)
            .contextWrite { context ->
                context.put(ReactiveRequestContextHolder.CONTEXT_KEY, request)
            }
    }
}