package com.flowpay.presentation.dto.request

import java.math.BigDecimal

/**
 * 資産アカウント更新リクエスト
 */
data class UpdateAssetAccountRequest(
    val balance: BigDecimal
)
