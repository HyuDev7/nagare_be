package com.flowpay.infrastructure.persistence.jpa.entity

import com.flowpay.domain.model.Transaction
import com.flowpay.domain.model.TransactionType
import jakarta.persistence.*
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime

@Entity
@Table(name = "transactions")
data class TransactionEntity(
    @Id
    @Column(length = 50)
    val id: String,

    @Column(nullable = false)
    val date: LocalDate,

    @Column(nullable = false, precision = 15, scale = 2)
    val amount: BigDecimal,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    val type: TransactionType,

    @Column(nullable = false, length = 50)
    val paymentMethodId: String,

    @Column(nullable = false, length = 50)
    val categoryId: String,

    @Column(nullable = false, length = 50)
    val assetAccountId: String,

    @Column(columnDefinition = "TEXT")
    val memo: String? = null,

    val withdrawalDate: LocalDate? = null,

    @Column(nullable = false)
    val isWithdrawn: Boolean = false,

    @Column(nullable = false)
    val isDeleted: Boolean = false,

    @Column(nullable = false)
    val createdAt: LocalDateTime,

    @Column(nullable = false)
    val updatedAt: LocalDateTime
) {
    companion object {
        fun fromDomain(transaction: Transaction): TransactionEntity {
            return TransactionEntity(
                id = transaction.id,
                date = transaction.date,
                amount = transaction.amount,
                type = transaction.type,
                paymentMethodId = transaction.paymentMethodId,
                categoryId = transaction.categoryId,
                assetAccountId = transaction.assetAccountId,
                memo = transaction.memo,
                withdrawalDate = transaction.withdrawalDate,
                isWithdrawn = transaction.isWithdrawn,
                isDeleted = transaction.isDeleted,
                createdAt = transaction.createdAt,
                updatedAt = transaction.updatedAt
            )
        }
    }

    fun toDomain(): Transaction {
        return Transaction(
            id = id,
            date = date,
            amount = amount,
            type = type,
            paymentMethodId = paymentMethodId,
            categoryId = categoryId,
            assetAccountId = assetAccountId,
            memo = memo,
            withdrawalDate = withdrawalDate,
            isWithdrawn = isWithdrawn,
            isDeleted = isDeleted,
            createdAt = createdAt,
            updatedAt = updatedAt
        )
    }
}
