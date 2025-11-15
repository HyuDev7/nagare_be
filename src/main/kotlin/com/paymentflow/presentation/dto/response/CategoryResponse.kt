package com.paymentflow.presentation.dto.response

import com.paymentflow.domain.model.Category
import com.paymentflow.domain.model.CategoryType
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
