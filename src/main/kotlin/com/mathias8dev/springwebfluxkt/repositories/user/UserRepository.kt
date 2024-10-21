package com.mathias8dev.springwebfluxkt.repositories.user

import com.mathias8dev.springwebfluxkt.models.User
import org.springframework.data.repository.kotlin.CoroutineCrudRepository

interface UserRepository : CoroutineCrudRepository<User, Long>, CustomUserRepository