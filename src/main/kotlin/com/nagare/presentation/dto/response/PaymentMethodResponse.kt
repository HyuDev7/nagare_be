package com.nagare.presentation.dto.response

import com.nagare.domain.model.PaymentMethod
import com.nagare.domain.model.PaymentMethodType
import java.time.LocalDateTime

/**
 * 支払い手段レスポンス
 */
data class PaymentMethodResponse(
    val id: String,
    val name: String,
    val type: PaymentMethodType,
    val assetAccountId: String,
    val closingDay: Int?,
    val withdrawalDay: Int?,
    val memo: String?,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
) {
    companion object {
        fun from(paymentMethod: PaymentMethod): PaymentMethodResponse {
            return PaymentMethodResponse(
                id = paymentMethod.id,
                name = paymentMethod.name,
                type = paymentMethod.type,
                assetAccountId = paymentMethod.assetAccountId,
                closingDay = paymentMethod.closingDay,
                withdrawalDay = paymentMethod.withdrawalDay,
                memo = paymentMethod.memo,
                createdAt = paymentMethod.createdAt,
                updatedAt = paymentMethod.updatedAt
            )
        }
    }
}
