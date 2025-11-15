package com.paymentflow.presentation.dto.request

import com.paymentflow.domain.model.TransactionType
import java.math.BigDecimal
import java.time.LocalDate

/**
 * 取引更新リクエスト
 */
data class UpdateTransactionRequest(
    val date: LocalDate,
    val amount: BigDecimal,
    val type: TransactionType,
    val paymentMethodId: String,
    val categoryId: String,
    val memo: String? = null
)
