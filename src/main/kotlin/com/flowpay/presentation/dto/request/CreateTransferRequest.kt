package com.flowpay.presentation.dto.request

import java.math.BigDecimal
import java.time.LocalDate

/**
 * 移動記録作成リクエスト（資産アカウント間振替）
 */
data class CreateAccountTransferRequest(
    val date: LocalDate,
    val amount: BigDecimal,
    val fromAssetAccountId: String,
    val toAssetAccountId: String,
    val memo: String?
)

/**
 * 出金記録作成リクエスト
 */
data class CreateWithdrawalRequest(
    val date: LocalDate,
    val amount: BigDecimal,
    val fromAssetAccountId: String,
    val memo: String?
)

/**
 * チャージ記録作成リクエスト
 */
data class CreateChargeRequest(
    val date: LocalDate,
    val amount: BigDecimal,
    val fromAssetAccountId: String,
    val toPaymentMethodId: String,
    val memo: String?
)
