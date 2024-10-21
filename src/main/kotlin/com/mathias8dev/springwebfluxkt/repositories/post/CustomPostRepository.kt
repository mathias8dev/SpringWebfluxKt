package com.mathias8dev.springwebfluxkt.repositories.post

import com.mathias8dev.springwebfluxkt.dtos.PageableDto
import com.mathias8dev.springwebfluxkt.dtos.post.PostResponseDto
import com.mathias8dev.springwebfluxkt.filtering.FilterCriteria
import com.mathias8dev.springwebfluxkt.filtering.FilterMode
import com.mathias8dev.springwebfluxkt.models.Post
import kotlinx.coroutines.flow.Flow
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface CustomPostRepository {
    suspend fun findAllPostWithCategoriesAndComments(): Flow<PostResponseDto>
    suspend fun addCategory(postId: Long, categoryId: Long)
    suspend fun findAll(
        pageableDto: PageableDto,
        filterCriteria: List<FilterCriteria> = emptyList(),
        filterMode: FilterMode = FilterMode.AND
    ): Page<PostResponseDto>

    suspend fun findAll(
        pageable: Pageable,
        filterCriteria: List<FilterCriteria> = emptyList(),
        filterMode: FilterMode = FilterMode.AND
    ): Page<PostResponseDto>

    suspend fun insert(post: Post): Post
}