package com.nagare.application.usecase

import com.nagare.domain.model.Transaction
import com.nagare.domain.model.TransactionType
import com.nagare.domain.repository.AssetAccountRepository
import com.nagare.domain.repository.PaymentMethodRepository
import com.nagare.domain.repository.TransactionRepository
import com.nagare.domain.service.WithdrawalCalculator
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*

/**
 * 取引ユースケース
 */
@Service
class TransactionUseCase(
    private val transactionRepository: TransactionRepository,
    private val paymentMethodRepository: PaymentMethodRepository,
    private val assetAccountRepository: AssetAccountRepository,
    private val withdrawalCalculator: WithdrawalCalculator
) {
    /**
     * すべての取引を取得
     */
    fun getAllTransactions(): List<Transaction> {
        return transactionRepository.findAll()
            .sortedByDescending { it.date }
    }

    /**
     * IDで取引を取得
     */
    fun getTransactionById(id: String): Transaction? {
        return transactionRepository.findById(id)
    }

    /**
     * フィルタで取引を取得
     */
    fun getTransactionsByFilter(
        from: LocalDate? = null,
        to: LocalDate? = null,
        categoryId: String? = null,
        paymentMethodId: String? = null,
        type: TransactionType? = null
    ): List<Transaction> {
        return transactionRepository.findByFilter(from, to, categoryId, paymentMethodId, type)
            .sortedByDescending { it.date }
    }

    /**
     * 取引を作成
     */
    fun createTransaction(
        date: LocalDate,
        amount: BigDecimal,
        type: TransactionType,
        paymentMethodId: String,
        categoryId: String,
        assetAccountId: String,
        memo: String? = null
    ): Transaction {
        // 支払い手段を取得
        val paymentMethod = paymentMethodRepository.findById(paymentMethodId)
            ?: throw IllegalArgumentException("支払い手段が見つかりません: $paymentMethodId")

        // 資産アカウントを取得
        val assetAccount = assetAccountRepository.findById(assetAccountId)
            ?: throw IllegalArgumentException("資産アカウントが見つかりません: $assetAccountId")

        // 引き落とし日を計算
        val withdrawalDate = withdrawalCalculator.calculateWithdrawalDate(date, paymentMethod)

        // 即時引き落としかどうか
        val isImmediateWithdrawal = paymentMethod.isImmediateWithdrawal()

        val now = LocalDateTime.now()
        val transaction = Transaction(
            id = "tx_${UUID.randomUUID()}",
            date = date,
            amount = amount,
            type = type,
            paymentMethodId = paymentMethodId,
            categoryId = categoryId,
            assetAccountId = assetAccountId,
            memo = memo,
            withdrawalDate = withdrawalDate,
            isWithdrawn = isImmediateWithdrawal,
            createdAt = now,
            updatedAt = now
        )

        // 取引を保存
        val savedTransaction = transactionRepository.save(transaction)

        // 資産アカウントの残高を更新
        updateAssetAccountBalance(savedTransaction, isImmediateWithdrawal)

        return savedTransaction
    }

    /**
     * 取引を更新
     */
    fun updateTransaction(
        id: String,
        date: LocalDate,
        amount: BigDecimal,
        type: TransactionType,
        paymentMethodId: String,
        categoryId: String,
        assetAccountId: String,
        memo: String? = null
    ): Transaction {
        val existing = transactionRepository.findById(id)
            ?: throw IllegalArgumentException("取引が見つかりません: $id")

        // 既存の取引が引き落とし済みの場合は更新不可
        if (existing.isWithdrawn) {
            throw IllegalStateException("引き落とし済みの取引は更新できません")
        }

        // 支払い手段を取得
        val paymentMethod = paymentMethodRepository.findById(paymentMethodId)
            ?: throw IllegalArgumentException("支払い手段が見つかりません: $paymentMethodId")

        // 資産アカウントを取得
        val assetAccount = assetAccountRepository.findById(assetAccountId)
            ?: throw IllegalArgumentException("資産アカウントが見つかりません: $assetAccountId")

        // 引き落とし日を再計算
        val withdrawalDate = withdrawalCalculator.calculateWithdrawalDate(date, paymentMethod)
        val isImmediateWithdrawal = paymentMethod.isImmediateWithdrawal()

        val updated = Transaction(
            id = existing.id,
            date = date,
            amount = amount,
            type = type,
            paymentMethodId = paymentMethodId,
            categoryId = categoryId,
            assetAccountId = assetAccountId,
            memo = memo,
            withdrawalDate = withdrawalDate,
            isWithdrawn = isImmediateWithdrawal,
            createdAt = existing.createdAt,
            updatedAt = LocalDateTime.now()
        )

        return transactionRepository.save(updated)
    }

    /**
     * 取引を削除
     */
    fun deleteTransaction(id: String): Boolean {
        val transaction = transactionRepository.findById(id)
            ?: return false

        // 引き落とし済みの場合は削除不可
        if (transaction.isWithdrawn) {
            throw IllegalStateException("引き落とし済みの取引は削除できません")
        }

        return transactionRepository.deleteById(id)
    }

    /**
     * 取引をキャンセル（論理削除）
     * 引き落とし済みの取引もキャンセル可能で、資産残高を調整する
     */
    fun cancelTransaction(id: String): Transaction {
        val transaction = transactionRepository.findById(id)
            ?: throw IllegalArgumentException("取引が見つかりません: $id")

        // すでにキャンセル済みの場合はエラー
        if (transaction.isDeleted) {
            throw IllegalStateException("この取引はすでにキャンセルされています")
        }

        // 引き落とし済みの場合は資産残高を戻す
        if (transaction.isWithdrawn) {
            val account = assetAccountRepository.findById(transaction.assetAccountId)
                ?: throw IllegalStateException("資産アカウントが存在しません: ${transaction.assetAccountId}")

            val updatedAccount = when (transaction.type) {
                TransactionType.EXPENSE -> {
                    // 支出をキャンセルするので資産を戻す
                    account.addIncome(transaction.amount)
                }
                TransactionType.INCOME -> {
                    // 収入をキャンセルするので資産から減らす
                    account.subtractExpense(transaction.amount)
                }
            }
            assetAccountRepository.save(updatedAccount)
        }

        // 論理削除
        val cancelled = transaction.copy(
            isDeleted = true,
            updatedAt = LocalDateTime.now()
        )

        return transactionRepository.save(cancelled)
    }

    /**
     * 引き落とし処理を実行
     * 指定日までの未引き落とし取引を処理
     */
    fun processWithdrawals(untilDate: LocalDate = LocalDate.now()): List<Transaction> {
        val pendingTransactions = transactionRepository.findPendingWithdrawals(untilDate)
        val processedTransactions = mutableListOf<Transaction>()

        pendingTransactions.forEach { transaction ->
            // 資産から引き落とし
            if (transaction.type == TransactionType.EXPENSE) {
                val account = assetAccountRepository.findById(transaction.assetAccountId)
                    ?: throw IllegalStateException("資産アカウントが存在しません: ${transaction.assetAccountId}")
                val updatedAccount = account.subtractExpense(transaction.amount)
                assetAccountRepository.save(updatedAccount)
            }

            // 取引を引き落とし済みに
            val withdrawn = transaction.markAsWithdrawn()
            processedTransactions.add(transactionRepository.save(withdrawn))
        }

        return processedTransactions
    }

    /**
     * 未引き落としの取引を取得
     */
    fun getPendingWithdrawals(): List<Transaction> {
        return transactionRepository.findNotWithdrawn()
            .filter { it.type == TransactionType.EXPENSE }
            .sortedBy { it.withdrawalDate }
    }

    /**
     * 資産アカウントの残高を更新
     */
    private fun updateAssetAccountBalance(transaction: Transaction, isImmediateWithdrawal: Boolean) {
        val account = assetAccountRepository.findById(transaction.assetAccountId)
            ?: throw IllegalStateException("資産アカウントが存在しません: ${transaction.assetAccountId}")

        val updatedAccount = when {
            // 収入の場合は即座に資産に追加
            transaction.type == TransactionType.INCOME -> {
                account.addIncome(transaction.amount)
            }
            // 即時引き落としの支出の場合は即座に資産から減算
            transaction.type == TransactionType.EXPENSE && isImmediateWithdrawal -> {
                account.subtractExpense(transaction.amount)
            }
            // クレジットカードなどの後日引き落としの場合は残高を変更しない
            else -> account
        }

        assetAccountRepository.save(updatedAccount)
    }
}
