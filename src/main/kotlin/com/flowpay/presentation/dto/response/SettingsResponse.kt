package com.flowpay.presentation.dto.response

import java.math.BigDecimal

/**
 * 設定レスポンス
 */
data class SettingsResponse(
    val monthlyBudget: BigDecimal?
)
