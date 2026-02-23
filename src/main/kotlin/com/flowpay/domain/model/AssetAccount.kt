package com.flowpay.domain.model

import java.math.BigDecimal
import java.time.LocalDateTime

/**
 * 資産アカウント
 * Phase 1では単一の資産アカウントのみ扱う
 */
data class AssetAccount(
    val id: String,
    val name: String,
    val balance: BigDecimal,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
) {
    /**
     * 残高を更新した新しいインスタンスを返す
     */
    fun updateBalance(newBalance: BigDecimal): AssetAccount {
        return copy(
            balance = newBalance,
            updatedAt = LocalDateTime.now()
        )
    }

    /**
     * 収入を追加した新しいインスタンスを返す
     */
    fun addIncome(amount: BigDecimal): AssetAccount {
        return updateBalance(balance + amount)
    }

    /**
     * 支出を減算した新しいインスタンスを返す
     */
    fun subtractExpense(amount: BigDecimal): AssetAccount {
        return updateBalance(balance - amount)
    }
}
