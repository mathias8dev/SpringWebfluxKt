package com.mathias8dev.springwebfluxkt.communication.http.configuration

import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import org.slf4j.LoggerFactory
import java.io.IOException


class AuthInterceptor : Interceptor {

    private val logger = LoggerFactory.getLogger(AuthInterceptor::class.java)


    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val request: Request = chain.request()
        val authenticatedRequestBuilder = request.newBuilder()
        logger.debug("The request is $request")
        logger.debug("The request's encodedPath is ${request.url.encodedPath} ${request.url.encodedPath == "/api/authentication-server/find/by/username"}")
        if (request.url.encodedPath != "/api/authorization-server/oauth2/token") {
            runBlocking {
                val client = SpringApplicationContext.getBean(AuthorizationServerRestClient::class.java)
                val accessToken =
                    client.grantClientCredentialsToken(scope = "openid data.read user.full_read user.create notify")
                        .get("access_token").asString
                logger.debug("The grantClientCredentialsToken is $accessToken")
                authenticatedRequestBuilder.addHeader(
                    "Authorization",
                    "Bearer $accessToken"
                )
            }
        }

        return chain.proceed(authenticatedRequestBuilder.build())
    }
}