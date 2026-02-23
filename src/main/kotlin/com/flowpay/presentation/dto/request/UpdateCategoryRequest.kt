package com.flowpay.presentation.dto.request

import com.flowpay.domain.model.CategoryType

/**
 * カテゴリ更新リクエスト
 */
data class UpdateCategoryRequest(
    val name: String,
    val type: CategoryType
)
