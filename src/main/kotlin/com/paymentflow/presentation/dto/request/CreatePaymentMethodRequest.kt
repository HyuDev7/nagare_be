package com.paymentflow.presentation.dto.request

import com.paymentflow.domain.model.PaymentMethodType

/**
 * 支払い手段作成リクエスト
 */
data class CreatePaymentMethodRequest(
    val name: String,
    val type: PaymentMethodType,
    val assetAccountId: String,
    val closingDay: Int? = null,
    val withdrawalDay: Int? = null,
    val memo: String? = null
)
