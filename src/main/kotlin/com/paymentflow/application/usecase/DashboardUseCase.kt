package com.paymentflow.application.usecase

import com.paymentflow.application.dto.*
import com.paymentflow.domain.model.TransactionType
import com.paymentflow.domain.repository.*
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.math.RoundingMode
import java.time.LocalDate
import java.time.YearMonth

/**
 * ダッシュボードユースケース
 */
@Service
class DashboardUseCase(
    private val assetAccountRepository: AssetAccountRepository,
    private val transactionRepository: TransactionRepository,
    private val paymentMethodRepository: PaymentMethodRepository,
    private val categoryRepository: CategoryRepository,
    private val settingsRepository: SettingsRepository
) {
    /**
     * ダッシュボードデータを取得
     */
    fun getDashboardData(): DashboardData {
        val assetAccount = assetAccountRepository.find()
            ?: throw IllegalStateException("資産アカウントが存在しません")

        // 当月の開始日と終了日
        val currentMonth = YearMonth.now()
        val monthStart = currentMonth.atDay(1)
        val monthEnd = currentMonth.atEndOfMonth()

        // 当月の取引を取得
        val currentMonthTransactions = transactionRepository.findByDateRange(monthStart, monthEnd)

        // 当月の支出・収入合計
        val totalExpense = currentMonthTransactions
            .filter { it.type == TransactionType.EXPENSE }
            .sumOf { it.amount }

        val totalIncome = currentMonthTransactions
            .filter { it.type == TransactionType.INCOME }
            .sumOf { it.amount }

        // 予算情報
        val budget = settingsRepository.getMonthlyBudget()
        val budgetRemaining = budget?.let { it - totalExpense }
        val budgetUsageRate = budget?.let {
            if (it > BigDecimal.ZERO) {
                (totalExpense.divide(it, 4, RoundingMode.HALF_UP) * BigDecimal(100)).toDouble()
            } else {
                0.0
            }
        }

        // 未確定の支出（未引き落としの支出取引）
        val pendingWithdrawals = transactionRepository.findNotWithdrawn()
            .filter { it.type == TransactionType.EXPENSE }
            .sumOf { it.amount }

        val availableBalance = assetAccount.balance - pendingWithdrawals

        // 今後30日間の引き落とし予定
        val today = LocalDate.now()
        val in30Days = today.plusDays(30)
        val upcomingWithdrawals = transactionRepository.findNotWithdrawn()
            .filter { it.type == TransactionType.EXPENSE && it.withdrawalDate != null }
            .filter { it.withdrawalDate!! in today..in30Days }
            .groupBy { it.withdrawalDate!! to it.paymentMethodId }
            .map { (key, transactions) ->
                val (date, paymentMethodId) = key
                val paymentMethod = paymentMethodRepository.findById(paymentMethodId)
                val amount = transactions.sumOf { it.amount }
                UpcomingWithdrawalData(
                    date = date,
                    paymentMethodName = paymentMethod?.name ?: "不明",
                    amount = amount
                )
            }
            .sortedBy { it.date }

        // カテゴリ別内訳（当月の支出のみ）
        val expensesByCategory = currentMonthTransactions
            .filter { it.type == TransactionType.EXPENSE }
            .groupBy { it.categoryId }
            .map { (categoryId, transactions) ->
                val category = categoryRepository.findById(categoryId)
                val amount = transactions.sumOf { it.amount }
                val percentage = if (totalExpense > BigDecimal.ZERO) {
                    (amount.divide(totalExpense, 4, RoundingMode.HALF_UP) * BigDecimal(100)).toDouble()
                } else {
                    0.0
                }
                CategoryBreakdownData(
                    categoryName = category?.name ?: "不明",
                    amount = amount,
                    percentage = percentage
                )
            }
            .sortedByDescending { it.amount }

        return DashboardData(
            assetAccount = AssetAccountData(
                id = assetAccount.id,
                name = assetAccount.name,
                balance = assetAccount.balance
            ),
            currentMonth = CurrentMonthData(
                totalExpense = totalExpense,
                totalIncome = totalIncome,
                budget = budget,
                budgetRemaining = budgetRemaining,
                budgetUsageRate = budgetUsageRate
            ),
            pendingWithdrawals = PendingWithdrawalsData(
                total = pendingWithdrawals,
                availableBalance = availableBalance
            ),
            upcomingWithdrawals = upcomingWithdrawals,
            categoryBreakdown = expensesByCategory
        )
    }
}
