package com.flowpay.presentation.dto.request

import com.flowpay.domain.model.PaymentMethodType

/**
 * 支払い手段更新リクエスト
 */
data class UpdatePaymentMethodRequest(
    val name: String,
    val type: PaymentMethodType,
    val assetAccountId: String,
    val closingDay: Int? = null,
    val withdrawalDay: Int? = null,
    val memo: String? = null
)
