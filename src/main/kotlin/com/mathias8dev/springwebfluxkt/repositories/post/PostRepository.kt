package com.mathias8dev.springwebfluxkt.repositories.post

import com.mathias8dev.springwebfluxkt.models.Post
import org.springframework.data.repository.kotlin.CoroutineCrudRepository

interface PostRepository : CoroutineCrudRepository<Post, Long>, CustomPostRepository