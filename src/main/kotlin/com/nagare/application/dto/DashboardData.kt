package com.nagare.application.dto

import java.math.BigDecimal
import java.time.LocalDate

/**
 * ダッシュボードデータ
 */
data class DashboardData(
    val assetAccount: AssetAccountData,
    val currentMonth: CurrentMonthData,
    val pendingWithdrawals: PendingWithdrawalsData,
    val upcomingWithdrawals: List<UpcomingWithdrawalData>,
    val categoryBreakdown: List<CategoryBreakdownData>
)

/**
 * 資産アカウントデータ
 */
data class AssetAccountData(
    val id: String,
    val name: String,
    val balance: BigDecimal
)

/**
 * 当月データ
 */
data class CurrentMonthData(
    val totalExpense: BigDecimal,
    val totalIncome: BigDecimal,
    val budget: BigDecimal?,
    val budgetRemaining: BigDecimal?,
    val budgetUsageRate: Double?
)

/**
 * 未確定支出データ
 */
data class PendingWithdrawalsData(
    val total: BigDecimal,
    val availableBalance: BigDecimal
)

/**
 * 今後の引き落とし予定データ
 */
data class UpcomingWithdrawalData(
    val date: LocalDate,
    val paymentMethodName: String,
    val amount: BigDecimal
)

/**
 * カテゴリ別内訳データ
 */
data class CategoryBreakdownData(
    val categoryName: String,
    val amount: BigDecimal,
    val percentage: Double
)
