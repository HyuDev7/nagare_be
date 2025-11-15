package com.paymentflow.presentation.dto.response

import com.paymentflow.application.dto.DashboardData
import java.math.BigDecimal
import java.time.LocalDate

/**
 * ダッシュボードレスポンス
 */
data class DashboardResponse(
    val assetAccount: AssetAccountData,
    val currentMonth: CurrentMonthData,
    val pendingWithdrawals: PendingWithdrawalsData,
    val upcomingWithdrawals: List<UpcomingWithdrawalData>,
    val categoryBreakdown: List<CategoryBreakdownData>
) {
    data class AssetAccountData(
        val id: String,
        val name: String,
        val balance: BigDecimal
    )

    data class CurrentMonthData(
        val totalExpense: BigDecimal,
        val totalIncome: BigDecimal,
        val budget: BigDecimal?,
        val budgetRemaining: BigDecimal?,
        val budgetUsageRate: Double?
    )

    data class PendingWithdrawalsData(
        val total: BigDecimal,
        val availableBalance: BigDecimal
    )

    data class UpcomingWithdrawalData(
        val date: LocalDate,
        val paymentMethodName: String,
        val amount: BigDecimal
    )

    data class CategoryBreakdownData(
        val categoryName: String,
        val amount: BigDecimal,
        val percentage: Double
    )

    companion object {
        fun from(dashboardData: DashboardData): DashboardResponse {
            return DashboardResponse(
                assetAccount = AssetAccountData(
                    id = dashboardData.assetAccount.id,
                    name = dashboardData.assetAccount.name,
                    balance = dashboardData.assetAccount.balance
                ),
                currentMonth = CurrentMonthData(
                    totalExpense = dashboardData.currentMonth.totalExpense,
                    totalIncome = dashboardData.currentMonth.totalIncome,
                    budget = dashboardData.currentMonth.budget,
                    budgetRemaining = dashboardData.currentMonth.budgetRemaining,
                    budgetUsageRate = dashboardData.currentMonth.budgetUsageRate
                ),
                pendingWithdrawals = PendingWithdrawalsData(
                    total = dashboardData.pendingWithdrawals.total,
                    availableBalance = dashboardData.pendingWithdrawals.availableBalance
                ),
                upcomingWithdrawals = dashboardData.upcomingWithdrawals.map {
                    UpcomingWithdrawalData(
                        date = it.date,
                        paymentMethodName = it.paymentMethodName,
                        amount = it.amount
                    )
                },
                categoryBreakdown = dashboardData.categoryBreakdown.map {
                    CategoryBreakdownData(
                        categoryName = it.categoryName,
                        amount = it.amount,
                        percentage = it.percentage
                    )
                }
            )
        }
    }
}
