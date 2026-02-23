package com.flowpay.presentation.dto.response

/**
 * ページネーション対応レスポンス
 */
data class PaginatedResponse<T>(
    val data: List<T>,
    val page: Int,
    val pageSize: Int,
    val totalCount: Int,
    val totalPages: Int
)
