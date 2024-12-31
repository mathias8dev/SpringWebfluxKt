package com.mathias8dev.springwebfluxkt.beans.gateway

import com.mathias8dev.springwebfluxkt.utils.Utils
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.web.server.ServerWebExchange
import org.springframework.web.server.WebFilter
import org.springframework.web.server.WebFilterChain
import reactor.core.publisher.Mono


@Component
class GatewayContextFilter : WebFilter {

    private val logger = LoggerFactory.getLogger(GatewayContextFilter::class.java)

    override fun filter(exchange: ServerWebExchange, chain: WebFilterChain): Mono<Void> {
        val request = exchange.request
        val gatewayUrl = request.queryParams.getFirst("gateway") ?: Utils.baseServerUrl(request)
        val gatewayContext = GatewayContext(gatewayUrl = gatewayUrl)

        logger.debug("The gateway url is $gatewayUrl")

        return chain.filter(exchange)
            .contextWrite { context ->
                context.put(GatewayContextHolder.CONTEXT_KEY, gatewayContext)
            }
    }


}