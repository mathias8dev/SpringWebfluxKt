package com.mathias8dev.springwebfluxkt.services

import com.mathias8dev.springwebfluxkt.dtos.comment.CommentRequestDto
import com.mathias8dev.springwebfluxkt.dtos.post.PostRequestDto
import com.mathias8dev.springwebfluxkt.dtos.post.PostResponseDto
import com.mathias8dev.springwebfluxkt.dtos.user.*
import com.mathias8dev.springwebfluxkt.exceptions.HttpException
import com.mathias8dev.springwebfluxkt.filtering.FilterCriteria
import com.mathias8dev.springwebfluxkt.filtering.FilterMode
import com.mathias8dev.springwebfluxkt.models.Comment
import com.mathias8dev.springwebfluxkt.models.Post
import com.mathias8dev.springwebfluxkt.models.User
import com.mathias8dev.springwebfluxkt.repositories.user.UserRepository
import com.mathias8dev.springwebfluxkt.utils.otherwise
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional


@Service
@Transactional
class UserService(
    private val userRepository: UserRepository,
    private val commentService: CommentService,
    private val postService: PostService,
) {


    suspend fun findById(id: Long) = userRepository.findById(id).otherwise {
        throw HttpException(HttpStatus.BAD_REQUEST, "The user with id $id was not found")
    }

    suspend fun insert(userRequestDto: UserRequestDto): User {
        val user = User(
            username = userRequestDto.username!!,
            email = userRequestDto.email!!,
            firstname = userRequestDto.firstname,
            lastname = userRequestDto.lastname
        )

        return userRepository.save(user)
    }

    suspend fun update(userRequestDto: UserRequestDto): User {
        val user = findById(userRequestDto.id!!)
        userRequestDto.username?.let {
            user.username = it
        }
        userRequestDto.email?.let {
            user.email = it
        }
        userRequestDto.firstname?.let {
            user.firstname = it
        }
        userRequestDto.lastname?.let {
            user.lastname = it
        }
        return userRepository.save(user)
    }

    suspend fun addPost(dto: AddPostRequestDto): Post {
        return postService.add(dto)
    }

    suspend fun updatePost(dto: UpdatePostRequestDto): Post {
        findById(dto.authorId)
        return postService.update(
            PostRequestDto(
                id = dto.id,
                title = dto.title,
                content = dto.content,
            )
        )
    }

    suspend fun deletePost(dto: DeletePostRequestDto): Post {
        findById(dto.authorId)
        return postService.delete(dto.id)
    }

    suspend fun findPosts(
        authorId: Long,
        pageable: Pageable,
        filterCriteria: List<FilterCriteria> = emptyList(),
        filterMode: FilterMode = FilterMode.AND
    ): Page<PostResponseDto> {
        return postService.findAllByAuthorId(
            authorId,
            pageable,
            filterCriteria,
            filterMode
        )
    }

    suspend fun addComment(dto: AddCommentRequestDto): Comment {
        return userRepository.addComment(dto)
    }

    suspend fun updateComment(dto: UpdateCommentRequestDto): Comment {
        findById(dto.authorId)
        return commentService.update(
            CommentRequestDto(
                id = dto.id,
                comment = dto.content,
            )
        )
    }

    suspend fun deleteComment(dto: DeleteCommentRequestDto): Comment {
        findById(dto.authorId)
        return commentService.delete(dto.id)
    }
}