package com.mathias8dev.springwebfluxkt.controllers

import com.mathias8dev.springwebfluxkt.dtos.post.PostRequestDto
import com.mathias8dev.springwebfluxkt.dtos.post.PostResponseDto
import com.mathias8dev.springwebfluxkt.models.Post
import com.mathias8dev.springwebfluxkt.services.PostService
import org.springframework.data.domain.Page
import org.springframework.web.bind.annotation.*


@RestController
@RequestMapping("/posts")
class PostController(
    private val postService: PostService
) {


    @GetMapping
    fun findAll() = postService.findAll()

    @GetMapping("/paginated")
    suspend fun findAllPaginated(
        @RequestParam page: Int,
        @RequestParam pageSize: Int = 20,
        @RequestParam(required = false) sort: String? = null,
        @RequestParam(required = false) filter: String? = null,
        @RequestParam(required = false) filterMode: String? = null
    ): Page<PostResponseDto> {
        return postService.findAllPaginated(
            page,
            pageSize,
            sort,
            filter,
            filterMode
        )
    }

    @PutMapping("/{id}")
    suspend fun update(
        @PathVariable id: Long,
        @RequestPart dto: PostRequestDto
    ): Post {
        return postService.update(id, dto)
    }

    @PostMapping
    suspend fun insert(@RequestPart("dto") dto: PostRequestDto): Post {
        return postService.insert(dto)
    }
}