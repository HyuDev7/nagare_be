package com.paymentflow.presentation.dto.request

import com.paymentflow.domain.model.CategoryType

/**
 * カテゴリ作成リクエスト
 */
data class CreateCategoryRequest(
    val name: String,
    val type: CategoryType
)
