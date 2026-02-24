package com.nagare.presentation.dto.request

import com.nagare.domain.model.CategoryType

/**
 * カテゴリ作成リクエスト
 */
data class CreateCategoryRequest(
    val name: String,
    val type: CategoryType
)
