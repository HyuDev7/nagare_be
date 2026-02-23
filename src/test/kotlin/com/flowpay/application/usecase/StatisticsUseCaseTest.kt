package com.flowpay.application.usecase

import com.flowpay.domain.model.*
import com.flowpay.domain.repository.CategoryRepository
import com.flowpay.domain.repository.PaymentMethodRepository
import com.flowpay.domain.repository.TransactionRepository
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.YearMonth
import kotlin.test.assertEquals

class StatisticsUseCaseTest {

    private lateinit var transactionRepository: TransactionRepository
    private lateinit var categoryRepository: CategoryRepository
    private lateinit var paymentMethodRepository: PaymentMethodRepository
    private lateinit var statisticsUseCase: StatisticsUseCase

    @BeforeEach
    fun setup() {
        transactionRepository = mockk()
        categoryRepository = mockk()
        paymentMethodRepository = mockk()
        statisticsUseCase = StatisticsUseCase(
            transactionRepository,
            categoryRepository,
            paymentMethodRepository
        )
    }

    @Test
    fun `getMonthlyTrend should return monthly data`() {
        // Arrange
        val currentMonthTransactions = listOf(
            createTransaction("tx_001", LocalDate.now(), BigDecimal("10000"), TransactionType.INCOME),
            createTransaction("tx_002", LocalDate.now(), BigDecimal("5000"), TransactionType.EXPENSE)
        )

        every {
            transactionRepository.findByDateRange(any(), any())
        } returns currentMonthTransactions

        // Act
        val result = statisticsUseCase.getMonthlyTrend(1)

        // Assert
        assertEquals(1, result.months.size)
        assertEquals(BigDecimal("10000"), result.months[0].income)
        assertEquals(BigDecimal("5000"), result.months[0].expense)
        assertEquals(BigDecimal("5000"), result.months[0].balance)
    }

    @Test
    fun `getPaymentMethodStatistics should calculate correctly`() {
        // Arrange
        val paymentMethod = PaymentMethod(
            id = "pm_001",
            name = "現金",
            type = PaymentMethodType.CASH,
            assetAccountId = "acc_001",
            closingDay = null,
            withdrawalDay = null,
            memo = null,
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now()
        )

        val transactions = listOf(
            createTransaction("tx_001", LocalDate.now(), BigDecimal("1000"), TransactionType.EXPENSE, "pm_001"),
            createTransaction("tx_002", LocalDate.now(), BigDecimal("2000"), TransactionType.EXPENSE, "pm_001")
        )

        every { transactionRepository.findByDateRange(any(), any()) } returns transactions
        every { paymentMethodRepository.findAll() } returns listOf(paymentMethod)

        // Act
        val result = statisticsUseCase.getPaymentMethodStatistics(3)

        // Assert
        assertEquals(1, result.paymentMethods.size)
        assertEquals(BigDecimal("3000"), result.paymentMethods[0].totalAmount)
        assertEquals(2, result.paymentMethods[0].transactionCount)
    }

    private fun createTransaction(
        id: String,
        date: LocalDate,
        amount: BigDecimal,
        type: TransactionType,
        paymentMethodId: String = "pm_001"
    ): Transaction {
        return Transaction(
            id = id,
            date = date,
            amount = amount,
            type = type,
            paymentMethodId = paymentMethodId,
            categoryId = "cat_001",
            assetAccountId = "acc_001",
            memo = null,
            withdrawalDate = date,
            isWithdrawn = false,
            isDeleted = false,
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now()
        )
    }
}
