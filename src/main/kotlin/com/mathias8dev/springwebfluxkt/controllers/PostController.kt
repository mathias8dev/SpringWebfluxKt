package com.mathias8dev.springwebfluxkt.controllers

import com.mathias8dev.springwebfluxkt.dtos.post.PostRequestDto
import com.mathias8dev.springwebfluxkt.dtos.post.PostResponseDto
import com.mathias8dev.springwebfluxkt.models.Post
import com.mathias8dev.springwebfluxkt.services.PostService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import org.springframework.data.domain.Page
import org.springframework.web.bind.annotation.*


@RestController
@RequestMapping("/posts")
class PostController(
    private val postService: PostService
) {


    @GetMapping("/paginated")
    @Operation(summary = "Get paginated posts", description = "Retrieve a paginated list of posts")
    @ApiResponses(
        ApiResponse(responseCode = "200", description = "Successful operation"),
        ApiResponse(responseCode = "400", description = "Invalid parameters")
    )
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

    @GetMapping
    @Operation(summary = "Get all posts", description = "Retrieve a list of all posts")
    @ApiResponse(responseCode = "200", description = "Successful operation")
    fun findAll() = postService.findAll()


    @PutMapping("/{id}")
    @Operation(summary = "Update a post", description = "Update an existing post by ID")
    @ApiResponses(
        ApiResponse(responseCode = "200", description = "Successful operation"),
        ApiResponse(responseCode = "404", description = "Post not found")
    )
    suspend fun update(
        @PathVariable id: Long,
        @RequestPart dto: PostRequestDto
    ): Post {
        return postService.update(dto.copy(id = id))
    }
}