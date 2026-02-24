package com.nagare.application.usecase

import com.nagare.domain.model.RecurringFrequency
import com.nagare.domain.model.RecurringTransaction
import com.nagare.domain.model.TransactionType
import com.nagare.domain.repository.RecurringTransactionRepository
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime

@Service
class RecurringTransactionUseCase(
    private val recurringTransactionRepository: RecurringTransactionRepository,
    private val transactionUseCase: TransactionUseCase,
    private val settingsRepository: com.flowpay.domain.repository.SettingsRepository,
    private val assetAccountRepository: com.flowpay.domain.repository.AssetAccountRepository
) {

    /**
     * すべての繰り返し取引を取得
     */
    fun getAllRecurringTransactions(): List<RecurringTransaction> {
        return recurringTransactionRepository.findAll()
    }

    /**
     * アクティブな繰り返し取引を取得
     */
    fun getActiveRecurringTransactions(): List<RecurringTransaction> {
        return recurringTransactionRepository.findActive()
    }

    /**
     * 繰り返し取引を作成
     */
    fun createRecurringTransaction(
        name: String,
        amount: BigDecimal,
        type: TransactionType,
        paymentMethodId: String,
        categoryId: String,
        frequency: RecurringFrequency,
        startDate: LocalDate,
        endDate: LocalDate?,
        dayOfMonth: Int?,
        dayOfWeek: Int?,
        memo: String?
    ): RecurringTransaction {
        val now = LocalDateTime.now()
        val recurring = RecurringTransaction(
            id = "rt_${System.currentTimeMillis()}",
            name = name,
            amount = amount,
            type = type,
            paymentMethodId = paymentMethodId,
            categoryId = categoryId,
            frequency = frequency,
            startDate = startDate,
            endDate = endDate,
            dayOfMonth = dayOfMonth,
            dayOfWeek = dayOfWeek,
            memo = memo,
            isActive = true,
            createdAt = now,
            updatedAt = now
        )

        return recurringTransactionRepository.save(recurring)
    }

    /**
     * 繰り返し取引を更新（部分更新）
     */
    fun updateRecurringTransaction(
        id: String,
        name: String?,
        amount: BigDecimal?,
        isActive: Boolean?
    ): RecurringTransaction {
        val existing = recurringTransactionRepository.findById(id)
            ?: throw IllegalArgumentException("繰り返し取引が見つかりません: $id")

        val updated = existing.copy(
            name = name ?: existing.name,
            amount = amount ?: existing.amount,
            isActive = isActive ?: existing.isActive,
            updatedAt = LocalDateTime.now()
        )

        return recurringTransactionRepository.save(updated)
    }

    /**
     * 繰り返し取引を更新（完全更新）
     */
    fun updateRecurringTransactionFull(
        id: String,
        name: String,
        amount: BigDecimal,
        type: TransactionType,
        paymentMethodId: String,
        categoryId: String,
        frequency: RecurringFrequency,
        startDate: LocalDate,
        endDate: LocalDate?,
        dayOfMonth: Int?,
        dayOfWeek: Int?,
        memo: String?
    ): RecurringTransaction {
        val existing = recurringTransactionRepository.findById(id)
            ?: throw IllegalArgumentException("繰り返し取引が見つかりません: $id")

        val updated = existing.copy(
            name = name,
            amount = amount,
            type = type,
            paymentMethodId = paymentMethodId,
            categoryId = categoryId,
            frequency = frequency,
            startDate = startDate,
            endDate = endDate,
            dayOfMonth = dayOfMonth,
            dayOfWeek = dayOfWeek,
            memo = memo,
            updatedAt = LocalDateTime.now()
        )

        return recurringTransactionRepository.save(updated)
    }

    /**
     * 繰り返し取引を削除
     */
    fun deleteRecurringTransaction(id: String): Boolean {
        return recurringTransactionRepository.delete(id)
    }

    /**
     * 今日実行すべき繰り返し取引を実行
     */
    fun executeRecurringTransactionsForToday() {
        val today = LocalDate.now()
        executeRecurringTransactionsForDate(today)
    }

    /**
     * 指定日に実行すべき繰り返し取引を実行
     */
    fun executeRecurringTransactionsForDate(targetDate: LocalDate) {
        val activeRecurring = getActiveRecurringTransactions()

        // デフォルトの資産アカウントを取得（最初のアカウント）
        val defaultAssetAccount = assetAccountRepository.findAll().firstOrNull()
            ?: throw IllegalStateException("資産アカウントが存在しません")

        for (recurring in activeRecurring) {
            if (shouldExecuteToday(recurring, targetDate)) {
                transactionUseCase.createTransaction(
                    date = targetDate,
                    amount = recurring.amount,
                    type = recurring.type,
                    paymentMethodId = recurring.paymentMethodId,
                    categoryId = recurring.categoryId,
                    assetAccountId = defaultAssetAccount.id,
                    memo = "[自動] ${recurring.name}"
                )
            }
        }
    }

    /**
     * 未実行の定期取引をチェックして実行
     * ユーザーアクセス時に呼び出される
     */
    fun checkAndExecutePendingRecurringTransactions() {
        val lastCheckDate = settingsRepository.getLastRecurringTransactionCheckDate()
        val today = LocalDate.now()

        // 初回実行時は今日のみ実行
        if (lastCheckDate == null) {
            executeRecurringTransactionsForDate(today)
            settingsRepository.setLastRecurringTransactionCheckDate(today)
            return
        }

        // 最終チェック日が今日なら何もしない
        if (!lastCheckDate.isBefore(today)) {
            return
        }

        // 最終チェック日の翌日から今日までの定期取引を実行
        var currentDate = lastCheckDate.plusDays(1)
        while (!currentDate.isAfter(today)) {
            executeRecurringTransactionsForDate(currentDate)
            currentDate = currentDate.plusDays(1)
        }

        // 最終チェック日を更新
        settingsRepository.setLastRecurringTransactionCheckDate(today)
    }

    private fun shouldExecuteToday(recurring: RecurringTransaction, today: LocalDate): Boolean {
        // 開始日より前または終了日より後の場合は実行しない
        if (today.isBefore(recurring.startDate)) return false
        if (recurring.endDate != null && today.isAfter(recurring.endDate)) return false

        return when (recurring.frequency) {
            RecurringFrequency.DAILY -> true
            RecurringFrequency.WEEKLY -> {
                recurring.dayOfWeek == today.dayOfWeek.value
            }
            RecurringFrequency.MONTHLY -> {
                recurring.dayOfMonth == today.dayOfMonth
            }
            RecurringFrequency.YEARLY -> {
                today.monthValue == recurring.startDate.monthValue &&
                        today.dayOfMonth == recurring.startDate.dayOfMonth
            }
        }
    }
}
