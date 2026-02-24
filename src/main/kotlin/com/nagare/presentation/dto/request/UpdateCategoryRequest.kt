package com.nagare.presentation.dto.request

import com.nagare.domain.model.CategoryType

/**
 * カテゴリ更新リクエスト
 */
data class UpdateCategoryRequest(
    val name: String,
    val type: CategoryType
)
