package com.paymentflow.application.usecase

import com.paymentflow.domain.model.*
import com.paymentflow.domain.repository.AssetAccountRepository
import com.paymentflow.domain.repository.PaymentMethodRepository
import com.paymentflow.domain.repository.TransactionRepository
import com.paymentflow.domain.service.WithdrawalCalculator
import io.mockk.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class TransactionUseCaseTest {

    private lateinit var transactionRepository: TransactionRepository
    private lateinit var paymentMethodRepository: PaymentMethodRepository
    private lateinit var assetAccountRepository: AssetAccountRepository
    private lateinit var withdrawalCalculator: WithdrawalCalculator
    private lateinit var transactionUseCase: TransactionUseCase

    @BeforeEach
    fun setup() {
        transactionRepository = mockk()
        paymentMethodRepository = mockk()
        assetAccountRepository = mockk()
        withdrawalCalculator = mockk()
        transactionUseCase = TransactionUseCase(
            transactionRepository,
            paymentMethodRepository,
            assetAccountRepository,
            withdrawalCalculator
        )
    }

    @Test
    fun `createTransaction should save transaction with immediate withdrawal`() {
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

        val assetAccount = AssetAccount(
            id = "acc_001",
            name = "メイン資産",
            balance = BigDecimal("100000"),
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now()
        )

        every { paymentMethodRepository.findById("pm_001") } returns paymentMethod
        every { withdrawalCalculator.calculateWithdrawalDate(any(), paymentMethod) } returns LocalDate.now()
        every { assetAccountRepository.find() } returns assetAccount
        every { transactionRepository.save(any()) } answers { firstArg() }
        every { assetAccountRepository.save(any()) } answers { firstArg() }

        // Act
        val result = transactionUseCase.createTransaction(
            date = LocalDate.now(),
            amount = BigDecimal("1000"),
            type = TransactionType.EXPENSE,
            paymentMethodId = "pm_001",
            categoryId = "cat_001",
            memo = "テスト"
        )

        // Assert
        verify { transactionRepository.save(any()) }
        verify { assetAccountRepository.save(any()) }
        assertTrue(result.isWithdrawn)
    }

    @Test
    fun `deleteTransaction should throw exception for withdrawn transaction`() {
        // Arrange
        val transaction = Transaction(
            id = "tx_001",
            date = LocalDate.now(),
            amount = BigDecimal("1000"),
            type = TransactionType.EXPENSE,
            paymentMethodId = "pm_001",
            categoryId = "cat_001",
            memo = null,
            withdrawalDate = LocalDate.now(),
            isWithdrawn = true,
            isDeleted = false,
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now()
        )

        every { transactionRepository.findById("tx_001") } returns transaction

        // Act & Assert
        assertThrows<IllegalStateException> {
            transactionUseCase.deleteTransaction("tx_001")
        }
    }

    @Test
    fun `cancelTransaction should mark as deleted and adjust balance`() {
        // Arrange
        val transaction = Transaction(
            id = "tx_001",
            date = LocalDate.now(),
            amount = BigDecimal("1000"),
            type = TransactionType.EXPENSE,
            paymentMethodId = "pm_001",
            categoryId = "cat_001",
            memo = null,
            withdrawalDate = LocalDate.now(),
            isWithdrawn = true,
            isDeleted = false,
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now()
        )

        val assetAccount = AssetAccount(
            id = "acc_001",
            name = "メイン資産",
            balance = BigDecimal("100000"),
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now()
        )

        every { transactionRepository.findById("tx_001") } returns transaction
        every { assetAccountRepository.find() } returns assetAccount
        every { transactionRepository.save(any()) } answers { firstArg() }
        every { assetAccountRepository.save(any()) } answers { firstArg() }

        // Act
        val result = transactionUseCase.cancelTransaction("tx_001")

        // Assert
        assertTrue(result.isDeleted)
        verify { assetAccountRepository.save(match { it.balance == BigDecimal("101000") }) }
    }

    @Test
    fun `getAllTransactions should return sorted transactions`() {
        // Arrange
        val transactions = listOf(
            Transaction(
                id = "tx_001",
                date = LocalDate.of(2025, 1, 1),
                amount = BigDecimal("1000"),
                type = TransactionType.EXPENSE,
                paymentMethodId = "pm_001",
                categoryId = "cat_001",
                memo = null,
                withdrawalDate = LocalDate.now(),
                isWithdrawn = false,
                isDeleted = false,
                createdAt = LocalDateTime.now(),
                updatedAt = LocalDateTime.now()
            ),
            Transaction(
                id = "tx_002",
                date = LocalDate.of(2025, 1, 15),
                amount = BigDecimal("2000"),
                type = TransactionType.EXPENSE,
                paymentMethodId = "pm_001",
                categoryId = "cat_001",
                memo = null,
                withdrawalDate = LocalDate.now(),
                isWithdrawn = false,
                isDeleted = false,
                createdAt = LocalDateTime.now(),
                updatedAt = LocalDateTime.now()
            )
        )

        every { transactionRepository.findAll() } returns transactions

        // Act
        val result = transactionUseCase.getAllTransactions()

        // Assert
        assertEquals(2, result.size)
        assertEquals("tx_002", result[0].id) // 降順ソート
    }
}
