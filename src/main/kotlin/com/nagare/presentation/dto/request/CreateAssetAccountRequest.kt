package com.nagare.presentation.dto.request

import java.math.BigDecimal

/**
 * 資産アカウント作成リクエスト
 */
data class CreateAssetAccountRequest(
    val name: String,
    val initialBalance: BigDecimal
)
