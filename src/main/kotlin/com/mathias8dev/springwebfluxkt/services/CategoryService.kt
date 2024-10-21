package com.mathias8dev.springwebfluxkt.services

import com.mathias8dev.springwebfluxkt.dtos.category.CategoryRequestDto
import com.mathias8dev.springwebfluxkt.models.Category
import com.mathias8dev.springwebfluxkt.repositories.category.CategoryRepository
import org.springframework.stereotype.Service


@Service
class CategoryService(
    private val categoryRepository: CategoryRepository
) {

    suspend fun insert(dto: CategoryRequestDto): Category {
        val category = Category(name = dto.name)
        return categoryRepository.save(category)
    }
}