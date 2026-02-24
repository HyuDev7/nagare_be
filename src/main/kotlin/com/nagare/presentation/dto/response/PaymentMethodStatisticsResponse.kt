package com.nagare.presentation.dto.response

import java.math.BigDecimal

/**
 * 支払い手段別統計レスポンス
 */
data class PaymentMethodStatisticsResponse(
    val paymentMethods: List<PaymentMethodStatData>
) {
    data class PaymentMethodStatData(
        val paymentMethodId: String,
        val paymentMethodName: String,
        val totalAmount: BigDecimal,
        val transactionCount: Int,
        val averageAmount: BigDecimal
    )
}
