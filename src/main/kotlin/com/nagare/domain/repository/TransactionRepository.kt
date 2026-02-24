package com.nagare.domain.repository

import com.nagare.domain.model.Transaction
import com.nagare.domain.model.TransactionType
import java.time.LocalDate

/**
 * 取引リポジトリインターフェース
 */
interface TransactionRepository {
    /**
     * すべての取引を取得
     */
    fun findAll(): List<Transaction>

    /**
     * IDで取引を取得
     */
    fun findById(id: String): Transaction?

    /**
     * 条件でフィルタリングして取引を取得
     */
    fun findByFilter(
        from: LocalDate? = null,
        to: LocalDate? = null,
        categoryId: String? = null,
        paymentMethodId: String? = null,
        type: TransactionType? = null
    ): List<Transaction>

    /**
     * 引き落とし予定の取引を取得（未引き落としで引き落とし日が指定日以前）
     */
    fun findPendingWithdrawals(untilDate: LocalDate): List<Transaction>

    /**
     * 未引き落としの取引を取得
     */
    fun findNotWithdrawn(): List<Transaction>

    /**
     * 期間内の取引を取得
     */
    fun findByDateRange(from: LocalDate, to: LocalDate): List<Transaction>

    /**
     * 取引を保存
     */
    fun save(transaction: Transaction): Transaction

    /**
     * 取引を削除
     */
    fun deleteById(id: String): Boolean
}
