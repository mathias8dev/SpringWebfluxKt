package com.mathias8dev.springwebfluxkt.repositories.comment

import com.mathias8dev.springwebfluxkt.models.Comment
import org.springframework.data.repository.kotlin.CoroutineCrudRepository

interface CommentRepository : CoroutineCrudRepository<Comment, Long>