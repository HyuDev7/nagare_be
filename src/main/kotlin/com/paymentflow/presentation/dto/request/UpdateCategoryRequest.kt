package com.paymentflow.presentation.dto.request

import com.paymentflow.domain.model.CategoryType

/**
 * カテゴリ更新リクエスト
 */
data class UpdateCategoryRequest(
    val name: String,
    val type: CategoryType
)
