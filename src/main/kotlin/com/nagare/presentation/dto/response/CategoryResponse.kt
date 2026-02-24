package com.nagare.presentation.dto.response

import com.nagare.domain.model.Category
import com.nagare.domain.model.CategoryType
import java.time.LocalDateTime

/**
 * カテゴリレスポンス
 */
data class CategoryResponse(
    val id: String,
    val name: String,
    val type: CategoryType,
    val createdAt: LocalDateTime
) {
    companion object {
        fun from(category: Category): CategoryResponse {
            return CategoryResponse(
                id = category.id,
                name = category.name,
                type = category.type,
                createdAt = category.createdAt
            )
        }
    }
}
