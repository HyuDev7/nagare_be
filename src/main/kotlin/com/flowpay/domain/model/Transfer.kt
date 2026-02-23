package com.flowpay.domain.model

import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime

/**
 * 移動記録（資産アカウント間の振替、出金、チャージなど）
 */
data class Transfer(
    val id: String,
    val date: LocalDate,  // 移動日
    val amount: BigDecimal,  // 移動金額
    val fromAssetAccountId: String,  // 移動元資産アカウントID
    val toAssetAccountId: String?,  // 移動先資産アカウントID（nullの場合は出金）
    val toPaymentMethodId: String?,  // 移動先支払い手段ID（チャージの場合）
    val memo: String?,  // メモ
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
)
