package com.flowpay.presentation.dto.request

import com.flowpay.domain.model.CategoryType

/**
 * カテゴリ作成リクエスト
 */
data class CreateCategoryRequest(
    val name: String,
    val type: CategoryType
)
