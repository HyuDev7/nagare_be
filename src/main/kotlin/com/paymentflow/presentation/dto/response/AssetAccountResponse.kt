package com.paymentflow.presentation.dto.response

import com.paymentflow.domain.model.AssetAccount
import java.math.BigDecimal
import java.time.LocalDateTime

/**
 * 資産アカウントレスポンス
 */
data class AssetAccountResponse(
    val id: String,
    val name: String,
    val balance: BigDecimal,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
) {
    companion object {
        fun from(assetAccount: AssetAccount): AssetAccountResponse {
            return AssetAccountResponse(
                id = assetAccount.id,
                name = assetAccount.name,
                balance = assetAccount.balance,
                createdAt = assetAccount.createdAt,
                updatedAt = assetAccount.updatedAt
            )
        }
    }
}
