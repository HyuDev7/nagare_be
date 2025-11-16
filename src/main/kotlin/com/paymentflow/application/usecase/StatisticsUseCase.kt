package com.paymentflow.application.usecase

import com.paymentflow.domain.model.TransactionType
import com.paymentflow.domain.repository.CategoryRepository
import com.paymentflow.domain.repository.PaymentMethodRepository
import com.paymentflow.domain.repository.TransactionRepository
import com.paymentflow.presentation.dto.response.CategoryTrendResponse
import com.paymentflow.presentation.dto.response.MonthlyTrendResponse
import com.paymentflow.presentation.dto.response.PaymentMethodStatisticsResponse
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.math.RoundingMode
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter

/**
 * 統計ユースケース
 */
@Service
class StatisticsUseCase(
    private val transactionRepository: TransactionRepository,
    private val categoryRepository: CategoryRepository,
    private val paymentMethodRepository: PaymentMethodRepository
) {
    private val monthFormatter = DateTimeFormatter.ofPattern("yyyy-MM")

    /**
     * 月次推移データ取得
     */
    fun getMonthlyTrend(months: Int): MonthlyTrendResponse {
        val now = YearMonth.now()
        val monthlyData = (0 until months).map { i ->
            val targetMonth = now.minusMonths(i.toLong())
            val startDate = targetMonth.atDay(1)
            val endDate = targetMonth.atEndOfMonth()

            val transactions = transactionRepository.findByDateRange(startDate, endDate)

            val income = transactions
                .filter { it.type == TransactionType.INCOME }
                .sumOf { it.amount }

            val expense = transactions
                .filter { it.type == TransactionType.EXPENSE }
                .sumOf { it.amount }

            MonthlyTrendResponse.MonthlyData(
                month = targetMonth.format(monthFormatter),
                income = income,
                expense = expense,
                balance = income - expense
            )
        }.reversed()

        return MonthlyTrendResponse(monthlyData)
    }

    /**
     * カテゴリ別トレンドデータ取得
     */
    fun getCategoryTrend(months: Int): CategoryTrendResponse {
        val now = YearMonth.now()
        val categories = categoryRepository.findAll()

        val categoryTrendData = categories.map { category ->
            val monthlyData = (0 until months).map { i ->
                val targetMonth = now.minusMonths(i.toLong())
                val startDate = targetMonth.atDay(1)
                val endDate = targetMonth.atEndOfMonth()

                val amount = transactionRepository
                    .findByDateRange(startDate, endDate)
                    .filter { it.categoryId == category.id }
                    .sumOf { it.amount }

                CategoryTrendResponse.MonthlyAmount(
                    month = targetMonth.format(monthFormatter),
                    amount = amount
                )
            }.reversed()

            CategoryTrendResponse.CategoryTrendData(
                categoryId = category.id,
                categoryName = category.name,
                monthlyData = monthlyData
            )
        }

        return CategoryTrendResponse(categoryTrendData)
    }

    /**
     * 支払い手段別統計データ取得
     */
    fun getPaymentMethodStatistics(months: Int): PaymentMethodStatisticsResponse {
        val now = YearMonth.now()
        val startDate = now.minusMonths((months - 1).toLong()).atDay(1)
        val endDate = now.atEndOfMonth()

        val transactions = transactionRepository.findByDateRange(startDate, endDate)
        val paymentMethods = paymentMethodRepository.findAll()

        val statistics = paymentMethods.map { pm ->
            val pmTransactions = transactions.filter { it.paymentMethodId == pm.id }
            val totalAmount = pmTransactions.sumOf { it.amount }
            val count = pmTransactions.size
            val averageAmount = if (count > 0) {
                totalAmount.divide(BigDecimal(count), 2, RoundingMode.HALF_UP)
            } else {
                BigDecimal.ZERO
            }

            PaymentMethodStatisticsResponse.PaymentMethodStatData(
                paymentMethodId = pm.id,
                paymentMethodName = pm.name,
                totalAmount = totalAmount,
                transactionCount = count,
                averageAmount = averageAmount
            )
        }.sortedByDescending { it.totalAmount }

        return PaymentMethodStatisticsResponse(statistics)
    }
}
