package com.mathias8dev.springwebfluxkt.services

import com.mathias8dev.springwebfluxkt.dtos.Sorting
import com.mathias8dev.springwebfluxkt.dtos.post.PostRequestDto
import com.mathias8dev.springwebfluxkt.dtos.post.PostResponseDto
import com.mathias8dev.springwebfluxkt.dtos.user.AddPostRequestDto
import com.mathias8dev.springwebfluxkt.filtering.FilterCriteria
import com.mathias8dev.springwebfluxkt.filtering.FilterMode
import com.mathias8dev.springwebfluxkt.models.Post
import com.mathias8dev.springwebfluxkt.repositories.category.CategoryRepository
import com.mathias8dev.springwebfluxkt.repositories.post.PostRepository
import com.mathias8dev.springwebfluxkt.utils.otherwise
import com.mathias8dev.springwebfluxkt.utils.toFilterCriteria
import com.mathias8dev.springwebfluxkt.utils.toFilterMode
import com.mathias8dev.springwebfluxkt.utils.toSort
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import org.slf4j.LoggerFactory
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional


@Service
@Transactional
class PostService(
    private val postRepository: PostRepository,
    private val categoryRepository: CategoryRepository,
) {

    private val logger = LoggerFactory.getLogger(javaClass)

    suspend fun findById(id: Long) = postRepository.findById(id).otherwise { throw IllegalArgumentException("Post not found") }

    fun findAll() = postRepository.findAll()

    suspend fun findAllPaginated(
        page: Int,
        pageSize: Int,
        sort: String? = null,
        filter: String? = null,
        filterMode: String? = null
    ): Page<PostResponseDto> {

        logger.debug("Finding all posts paginated")
        val pageable = PageRequest.of(
            page,
            pageSize,
            sort?.toSort().otherwise(Sorting.default().toSort()),
        )

        return postRepository.findAll(
            pageable = pageable,
            filterCriteria = filter?.toFilterCriteria() ?: emptyList(),
            filterMode = filterMode?.toFilterMode() ?: FilterMode.AND
        )

    }


    suspend fun addCategory(postId: Long, categoryId: Long) {
        findById(postId)
        categoryRepository.findById(categoryId) ?: throw IllegalArgumentException("Category not found")
        postRepository.addCategory(postId, categoryId)
    }

    suspend fun update(dto: PostRequestDto): Post = coroutineScope {
        val post = findById(dto.id!!)
        dto.title?.let { post.title = it }
        dto.content?.let { post.content = it }
        val categoryUpdateAsync = dto.categoryId?.let {
            async {
                addCategory(dto.id, it)
            }
        }
        dto.tags?.let { newTags ->
            val tags = post.tags?.split(",")?.plus(newTags)?.toSet() ?: newTags.toSet()
            post.tags = tags.joinToString(",")
        }
        val saved = postRepository.save(post)
        categoryUpdateAsync?.await()
        saved
    }

    suspend fun add(dto: AddPostRequestDto): Post {
        val post = Post(
            authorId = dto.authorId,
            title = dto.title,
            content = dto.content
        )
        return postRepository.save(post)

    }

    suspend fun delete(id: Long): Post {
        val post = findById(id)
        postRepository.delete(post)
        return post
    }

    suspend fun findAllByAuthorId(
        authorId: Long,
        pageable: Pageable,
        filterCriteria: List<FilterCriteria> = emptyList(),
        filterMode: FilterMode = FilterMode.AND
    ): Page<PostResponseDto> {
        return postRepository.findAllByAuthorId(
            authorId,
            pageable,
            filterCriteria,
            filterMode
        )
    }
}