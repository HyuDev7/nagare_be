package com.paymentflow.domain.model

import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime

/**
 * 取引
 */
data class Transaction(
    val id: String,
    val date: LocalDate,                    // 利用日
    val amount: BigDecimal,                 // 金額（正の数）
    val type: TransactionType,              // 支出/収入
    val paymentMethodId: String,            // 支払い手段ID
    val categoryId: String,                 // カテゴリID
    val memo: String? = null,               // メモ
    val withdrawalDate: LocalDate? = null,  // 実際の引き落とし日
    val isWithdrawn: Boolean = false,       // 引き落とし済みフラグ
    val isDeleted: Boolean = false,         // 削除フラグ（論理削除）
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
) {
    init {
        require(amount > BigDecimal.ZERO) {
            "金額は正の数である必要があります"
        }
    }

    /**
     * 引き落とし済みに変更した新しいインスタンスを返す
     */
    fun markAsWithdrawn(): Transaction {
        return copy(
            isWithdrawn = true,
            updatedAt = LocalDateTime.now()
        )
    }

    /**
     * 引き落とし日を設定した新しいインスタンスを返す
     */
    fun withWithdrawalDate(date: LocalDate): Transaction {
        return copy(
            withdrawalDate = date,
            updatedAt = LocalDateTime.now()
        )
    }

    /**
     * 即時引き落としとして設定した新しいインスタンスを返す
     */
    fun asImmediateWithdrawal(): Transaction {
        return copy(
            withdrawalDate = date,
            isWithdrawn = true,
            updatedAt = LocalDateTime.now()
        )
    }
}
