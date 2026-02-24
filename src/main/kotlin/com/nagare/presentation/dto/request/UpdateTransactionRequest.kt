package com.nagare.presentation.dto.request

import com.nagare.domain.model.TransactionType
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
    val assetAccountId: String,
    val memo: String? = null
)
