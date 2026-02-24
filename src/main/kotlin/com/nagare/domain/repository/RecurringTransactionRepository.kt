package com.nagare.domain.repository

import com.nagare.domain.model.RecurringTransaction

/**
 * 繰り返し取引リポジトリインターフェース
 */
interface RecurringTransactionRepository {
    fun findAll(): List<RecurringTransaction>
    fun findById(id: String): RecurringTransaction?
    fun findActive(): List<RecurringTransaction>
    fun save(recurringTransaction: RecurringTransaction): RecurringTransaction
    fun delete(id: String): Boolean
}
