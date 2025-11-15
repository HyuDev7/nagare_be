package com.paymentflow.domain.model

import java.time.LocalDateTime

/**
 * 支払い手段
 */
data class PaymentMethod(
    val id: String,
    val name: String,
    val type: PaymentMethodType,
    val assetAccountId: String,
    val closingDay: Int? = null,       // 締め日（1-31、クレカのみ）
    val withdrawalDay: Int? = null,    // 引き落とし日（1-31、クレカのみ）
    val memo: String? = null,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
) {
    init {
        // クレジットカードの場合は締め日と引き落とし日が必須
        if (type == PaymentMethodType.CREDIT_CARD) {
            require(closingDay != null && closingDay in 1..31) {
                "クレジットカードの締め日は1-31の範囲で指定してください"
            }
            require(withdrawalDay != null && withdrawalDay in 1..31) {
                "クレジットカードの引き落とし日は1-31の範囲で指定してください"
            }
        }
    }

    /**
     * 即時引き落としかどうか
     * クレジットカード以外は即時引き落とし
     */
    fun isImmediateWithdrawal(): Boolean {
        return type != PaymentMethodType.CREDIT_CARD
    }
}
