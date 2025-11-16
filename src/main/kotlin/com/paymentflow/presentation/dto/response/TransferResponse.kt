package com.paymentflow.presentation.dto.response

import com.paymentflow.domain.model.Transfer
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime

/**
 * 移動記録レスポンス
 */
data class TransferResponse(
    val id: String,
    val date: LocalDate,
    val amount: BigDecimal,
    val fromAssetAccountId: String,
    val toAssetAccountId: String?,
    val toPaymentMethodId: String?,
    val memo: String?,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
) {
    companion object {
        fun from(transfer: Transfer): TransferResponse {
            return TransferResponse(
                id = transfer.id,
                date = transfer.date,
                amount = transfer.amount,
                fromAssetAccountId = transfer.fromAssetAccountId,
                toAssetAccountId = transfer.toAssetAccountId,
                toPaymentMethodId = transfer.toPaymentMethodId,
                memo = transfer.memo,
                createdAt = transfer.createdAt,
                updatedAt = transfer.updatedAt
            )
        }
    }
}
