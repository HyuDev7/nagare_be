package com.paymentflow.presentation.dto.response

import com.paymentflow.domain.model.RecurringFrequency
import com.paymentflow.domain.model.RecurringTransaction
import com.paymentflow.domain.model.TransactionType
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime

data class RecurringTransactionResponse(
    val id: String,
    val name: String,
    val amount: BigDecimal,
    val type: TransactionType,
    val paymentMethodId: String,
    val categoryId: String,
    val frequency: RecurringFrequency,
    val startDate: LocalDate,
    val endDate: LocalDate?,
    val dayOfMonth: Int?,
    val dayOfWeek: Int?,
    val memo: String?,
    val isActive: Boolean,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
) {
    companion object {
        fun from(rt: RecurringTransaction): RecurringTransactionResponse {
            return RecurringTransactionResponse(
                id = rt.id,
                name = rt.name,
                amount = rt.amount,
                type = rt.type,
                paymentMethodId = rt.paymentMethodId,
                categoryId = rt.categoryId,
                frequency = rt.frequency,
                startDate = rt.startDate,
                endDate = rt.endDate,
                dayOfMonth = rt.dayOfMonth,
                dayOfWeek = rt.dayOfWeek,
                memo = rt.memo,
                isActive = rt.isActive,
                createdAt = rt.createdAt,
                updatedAt = rt.updatedAt
            )
        }
    }
}
