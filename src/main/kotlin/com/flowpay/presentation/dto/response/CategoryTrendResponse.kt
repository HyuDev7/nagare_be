package com.flowpay.presentation.dto.response

import java.math.BigDecimal

/**
 * カテゴリ別トレンドレスポンス
 */
data class CategoryTrendResponse(
    val categories: List<CategoryTrendData>
) {
    data class CategoryTrendData(
        val categoryId: String,
        val categoryName: String,
        val monthlyData: List<MonthlyAmount>
    )

    data class MonthlyAmount(
        val month: String,  // YYYY-MM形式
        val amount: BigDecimal
    )
}
