package com.nagare.infrastructure.persistence.jpa.entity

import com.nagare.domain.model.RecurringFrequency
import com.nagare.domain.model.RecurringTransaction
import com.nagare.domain.model.TransactionType
import jakarta.persistence.*
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime

@Entity
@Table(name = "recurring_transactions")
data class RecurringTransactionEntity(
    @Id
    @Column(length = 50)
    val id: String,

    @Column(nullable = false, length = 200)
    val name: String,

    @Column(nullable = false, precision = 15, scale = 2)
    val amount: BigDecimal,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    val type: TransactionType,

    @Column(nullable = false, length = 50)
    val paymentMethodId: String,

    @Column(nullable = false, length = 50)
    val categoryId: String,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    val frequency: RecurringFrequency,

    @Column(nullable = false)
    val startDate: LocalDate,

    val endDate: LocalDate? = null,

    val dayOfMonth: Int? = null,

    val dayOfWeek: Int? = null,

    @Column(columnDefinition = "TEXT")
    val memo: String? = null,

    @Column(nullable = false)
    val isActive: Boolean = true,

    @Column(nullable = false)
    val createdAt: LocalDateTime,

    @Column(nullable = false)
    val updatedAt: LocalDateTime
) {
    companion object {
        fun fromDomain(recurringTransaction: RecurringTransaction): RecurringTransactionEntity {
            return RecurringTransactionEntity(
                id = recurringTransaction.id,
                name = recurringTransaction.name,
                amount = recurringTransaction.amount,
                type = recurringTransaction.type,
                paymentMethodId = recurringTransaction.paymentMethodId,
                categoryId = recurringTransaction.categoryId,
                frequency = recurringTransaction.frequency,
                startDate = recurringTransaction.startDate,
                endDate = recurringTransaction.endDate,
                dayOfMonth = recurringTransaction.dayOfMonth,
                dayOfWeek = recurringTransaction.dayOfWeek,
                memo = recurringTransaction.memo,
                isActive = recurringTransaction.isActive,
                createdAt = recurringTransaction.createdAt,
                updatedAt = recurringTransaction.updatedAt
            )
        }
    }

    fun toDomain(): RecurringTransaction {
        return RecurringTransaction(
            id = id,
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
            isActive = isActive,
            createdAt = createdAt,
            updatedAt = updatedAt
        )
    }
}
