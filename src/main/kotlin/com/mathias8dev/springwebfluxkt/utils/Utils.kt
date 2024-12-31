package com.mathias8dev.springwebfluxkt.utils

import com.mathias8dev.springwebfluxkt.beans.request.ReactiveRequestContextHolder
import com.mathias8dev.springwebfluxkt.dtos.PageableDto
import com.mathias8dev.springwebfluxkt.dtos.Sorting
import com.mathias8dev.springwebfluxkt.dtos.toPageable
import com.mathias8dev.springwebfluxkt.dtos.toSort
import com.mathias8dev.springwebfluxkt.exceptions.HttpException
import kotlinx.coroutines.reactive.awaitSingle
import org.slf4j.LoggerFactory
import org.springframework.core.env.Environment
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.http.HttpStatus
import org.springframework.http.server.reactive.ServerHttpRequest
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.ReactiveSecurityContextHolder
import org.springframework.security.core.context.SecurityContext
import org.springframework.security.oauth2.jwt.Jwt
import kotlin.math.min

object Utils {

    fun <T> toPageResponse(
        content: List<T>,
        pageable: Pageable,
        totalElements: Long = -1
    ): PageImpl<T> {
        return PageImpl(
            content,
            pageable,
            totalElements
        )
    }

    fun <T> paginate(
        data: Collection<T>,
        page: Int,
        pageSize: Int,
        sorting: Sorting? = null
    ): PageImpl<T> {
        val startIndex = page * pageSize
        if (startIndex > data.size)
            throw HttpException(HttpStatus.BAD_REQUEST, "Index out of bounds")

        val endIndex = min(startIndex + pageSize, data.size)

        val pagedResult = if (data.isNotEmpty()) {
            data.toList().subList(startIndex, endIndex)
        } else {
            emptyList()
        }
        if (sorting != null) {
            return PageImpl(
                pagedResult,
                PageRequest.of(page, pageSize, sorting.toSort()),
                data.size.toLong()
            )
        }
        return PageImpl(pagedResult, PageRequest.of(page, pageSize), data.size.toLong())
    }


    fun <T> paginate(
        data: Collection<T>,
        page: Int,
        pageSize: Int,
        sorting: List<Sorting>? = null
    ): PageImpl<T> {
        val startIndex = page * pageSize
        if (startIndex > data.size)
            throw HttpException(HttpStatus.BAD_REQUEST, "Index out of bounds")

        val endIndex = minOf(startIndex + pageSize, data.size)
        val pagedResult = if (data.isNotEmpty()) {
            data.toList().subList(startIndex, endIndex)
        } else {
            emptyList()
        }
        if (sorting?.isNotEmpty() == true) {

            return PageImpl(
                pagedResult,
                PageRequest.of(page, pageSize, sorting.toSort()),
                data.size.toLong()
            )
        }
        return PageImpl(pagedResult, PageRequest.of(page, pageSize), data.size.toLong())
    }

    fun <T> paginate(
        data: Collection<T>,
        pageable: Pageable
    ): PageImpl<T> {
        val startIndex = pageable.pageNumber * pageable.pageSize
        if (startIndex > data.size)
            throw HttpException(HttpStatus.BAD_REQUEST, "Index out of bounds")
        val endIndex = minOf(startIndex + pageable.pageSize, data.size)
        val results = data.toList().subList(startIndex, endIndex)
        return PageImpl(results, pageable, data.size.toLong())
    }

    fun <T> paginate(
        data: Collection<T>,
        pageableDto: PageableDto? = null
    ): PageImpl<T> {
        return paginate(
            data,
            pageableDto.otherwise(PageableDto()).toPageable()
        )
    }

    fun <T, V> isBetween(
        value: Comparable<V>,
        min: V?,
        max: V?,
        ifMin: () -> T,
        ifMax: () -> T,
        ifBetween: () -> T,
        otherwise: () -> T,
    ): T {
        return if (min != null && max == null && value >= min) ifMin()
        else if (max != null && min == null && value <= max) ifMax()
        else if (min != null && max != null && value >= min && value <= max) ifBetween()
        else otherwise()
    }

    private val logger = LoggerFactory.getLogger(Utils::class.java)

    fun baseServerUrl(request: ServerHttpRequest): String {
        return request.uri.scheme + "://" + request.uri.host + ":" + request.uri.port
    }

    suspend fun tryGetBaseServerUrl(): String? {
        return ReactiveRequestContextHolder.get()?.let { request ->
            baseServerUrl(request)
        }.otherwise {
            tryOrNull {
                SpringApplicationContext.getBean(Environment::class.java).getProperty("webfluxblog.gateway.baseurl")
            }
        }
    }


    suspend fun getJwtTokenString(): String? {
        return tryOrNull {
            ReactiveSecurityContextHolder.getContext()
                .map<Any>(SecurityContext::getAuthentication)
                .map {
                    val authentication = it as Authentication
                    getJwt(authentication).tokenValue
                }
                .awaitSingle()
        }
    }


    suspend fun getJwtSubject(): String? {
        return tryOrNull {
            ReactiveSecurityContextHolder.getContext()
                .map<Any>(SecurityContext::getAuthentication)
                .map {
                    val authentication = it as Authentication
                    getJwt(authentication).subject
                }
                .awaitSingle()
        }
    }

    @Throws(IllegalStateException::class)
    private fun getJwt(authentication: Authentication): Jwt {
        logger.debug("The authentication is {}", authentication)
        logger.debug("The principal is Jwt ? {}", authentication.principal is Jwt)
        logger.debug("The credentials is Jwt ? {}", authentication.credentials is Jwt)

        // If the authentication object is JwtAuthenticationToken, then the principal is Jwt and the credentials also
        // Reference org.springframework.security.oauth2.server.resource.authentication.AbstractOAuth2TokenAuthenticationToken
        return (authentication.principal as? Jwt ?: authentication.credentials as? Jwt).otherwise {
            throw IllegalStateException("JWT not found in principal or credentials")
        }
    }
}