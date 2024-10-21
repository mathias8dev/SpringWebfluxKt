package com.mathias8dev.springwebfluxkt.dtos.category

import com.fasterxml.jackson.annotation.JsonInclude
import com.mathias8dev.springwebfluxkt.dtos.post.PostResponseDto

@JsonInclude(JsonInclude.Include.NON_NULL)
data class CategoryResponseDto(
    var id: Long,
    var name: String,
    var posts: List<PostResponseDto>? = null
) {
    companion object {
        fun fromRow(row: Map<String, Any?>): CategoryResponseDto {
            return CategoryResponseDto(
                id = row["category_id"] as Long,
                name = row["category_name"] as String
            )
        }

        fun fromRowPrefixed(row: Map<String, Any?>): CategoryResponseDto {
            return CategoryResponseDto(
                id = row["category_id"] as Long,
                name = row["category_name"] as String
            )
        }
    }
}
