package com.mathias8dev.springwebfluxkt.repositories.category

import com.mathias8dev.springwebfluxkt.models.Category
import org.springframework.data.repository.kotlin.CoroutineCrudRepository

interface CategoryRepository : CoroutineCrudRepository<Category, Long>