package com.mathias8dev.springwebfluxkt.communication.http.configuration

import okhttp3.Credentials
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import org.slf4j.LoggerFactory
import java.io.IOException


class BasicAuthInterceptor : Interceptor {

    private val logger = LoggerFactory.getLogger(this.javaClass)

    private val clientId = "webflux-oauth2-client"
    private val clientSecret = "webflux-oauth2-client-secret"

    private val credentials: String = Credentials.basic(clientId, clientSecret)

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val request: Request = chain.request()
        val authenticatedRequestBuilder = request.newBuilder()
        logger.debug("The request is $request")
        if (request.url.encodedPath == "/api/authorization-server/oauth2/token") {
            authenticatedRequestBuilder
                .header("Authorization", credentials)
        }
        return chain.proceed(authenticatedRequestBuilder.build())
    }
}