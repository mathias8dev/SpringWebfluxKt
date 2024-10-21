package com.mathias8dev.springwebfluxkt.services

import com.mathias8dev.springwebfluxkt.dtos.Sorting
import com.mathias8dev.springwebfluxkt.dtos.post.PostRequestDto
import com.mathias8dev.springwebfluxkt.dtos.post.PostResponseDto
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

    suspend fun insert(dto: PostRequestDto): Post {
        val post = Post(
            title = dto.title!!,
            content = dto.content!!
        )

        dto.tags?.let { post.tags = it.joinToString(",") }
        logger.debug("The post to be saved is $post")
        val savedPost = postRepository.save(post)
        return savedPost
    }

    suspend fun addCategory(postId: Long, categoryId: Long) {
        findById(postId)
        categoryRepository.findById(categoryId) ?: throw IllegalArgumentException("Category not found")
        postRepository.addCategory(postId, categoryId)
    }

    suspend fun update(id: Long, dto: PostRequestDto): Post = coroutineScope {
        val post = findById(id)
        dto.title?.let { post.title = it }
        dto.content?.let { post.content = it }
        val categoryUpdateAsync = dto.categoryId?.let {
            async {
                addCategory(id, it)
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
}