package com.paymentflow.presentation.dto.request

import com.paymentflow.domain.model.TransactionType
import java.math.BigDecimal
import java.time.LocalDate

/**
 * 取引作成リクエスト
 */
data class CreateTransactionRequest(
    val date: LocalDate,
    val amount: BigDecimal,
    val type: TransactionType,
    val paymentMethodId: String,
    val categoryId: String,
    val assetAccountId: String,
    val memo: String? = null
)
