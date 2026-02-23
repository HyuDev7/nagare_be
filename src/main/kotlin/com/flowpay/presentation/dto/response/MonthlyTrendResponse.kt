package com.flowpay.presentation.dto.response

import java.math.BigDecimal

/**
 * 月次推移レスポンス
 */
data class MonthlyTrendResponse(
    val months: List<MonthlyData>
) {
    data class MonthlyData(
        val month: String,  // YYYY-MM形式
        val income: BigDecimal,
        val expense: BigDecimal,
        val balance: BigDecimal  // 収入 - 支出
    )
}
