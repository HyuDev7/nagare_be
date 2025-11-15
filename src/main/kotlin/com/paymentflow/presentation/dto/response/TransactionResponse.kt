package com.paymentflow.presentation.dto.response

import com.paymentflow.domain.model.Category
import com.paymentflow.domain.model.PaymentMethod
import com.paymentflow.domain.model.Transaction
import com.paymentflow.domain.model.TransactionType
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime

/**
 * 取引レスポンス
 */
data class TransactionResponse(
    val id: String,
    val date: LocalDate,
    val amount: BigDecimal,
    val type: TransactionType,
    val paymentMethod: PaymentMethodInfo,
    val category: CategoryInfo,
    val memo: String?,
    val withdrawalDate: LocalDate?,
    val isWithdrawn: Boolean,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
) {
    data class PaymentMethodInfo(
        val id: String,
        val name: String
    )

    data class CategoryInfo(
        val id: String,
        val name: String
    )

    companion object {
        fun from(
            transaction: Transaction,
            paymentMethod: PaymentMethod,
            category: Category
        ): TransactionResponse {
            return TransactionResponse(
                id = transaction.id,
                date = transaction.date,
                amount = transaction.amount,
                type = transaction.type,
                paymentMethod = PaymentMethodInfo(
                    id = paymentMethod.id,
                    name = paymentMethod.name
                ),
                category = CategoryInfo(
                    id = category.id,
                    name = category.name
                ),
                memo = transaction.memo,
                withdrawalDate = transaction.withdrawalDate,
                isWithdrawn = transaction.isWithdrawn,
                createdAt = transaction.createdAt,
                updatedAt = transaction.updatedAt
            )
        }
    }
}
