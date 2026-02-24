package com.nagare.presentation.dto.request

import java.math.BigDecimal

/**
 * 設定更新リクエスト
 */
data class UpdateSettingsRequest(
    val monthlyBudget: BigDecimal
)
