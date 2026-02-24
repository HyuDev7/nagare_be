package com.nagare.presentation.dto.request

import com.nagare.domain.model.RecurringFrequency
import com.nagare.domain.model.TransactionType
import java.math.BigDecimal
import java.time.LocalDate

data class CreateRecurringTransactionRequest(
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
    val memo: String?
)
