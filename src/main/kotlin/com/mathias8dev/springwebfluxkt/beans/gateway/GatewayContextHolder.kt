package com.mathias8dev.springwebfluxkt.beans.gateway

import com.mathias8dev.springwebfluxkt.utils.Utils
import kotlinx.coroutines.reactor.awaitSingle
import reactor.core.publisher.Mono


class GatewayContextHolder {

    companion object {
        val CONTEXT_KEY = GatewayContextHolder::class.java

        suspend fun get(): GatewayContext {
            val defaultUrl = Utils.tryGetBaseServerUrl()
            return Mono.deferContextual { context ->
                var gatewayContext = context.getOrDefault<GatewayContext>(CONTEXT_KEY, null)
                if (gatewayContext == null) gatewayContext = GatewayContext(defaultUrl)
                Mono.just(gatewayContext)
            }.awaitSingle()
        }
    }
}