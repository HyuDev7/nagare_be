package com.nagare.infrastructure.persistence.jpa.entity

import com.nagare.domain.model.PaymentMethod
import com.nagare.domain.model.PaymentMethodType
import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "payment_methods")
data class PaymentMethodEntity(
    @Id
    @Column(length = 50)
    val id: String,

    @Column(nullable = false, length = 100)
    val name: String,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    val type: PaymentMethodType,

    @Column(nullable = false, length = 50)
    val assetAccountId: String,

    val closingDay: Int? = null,

    val withdrawalDay: Int? = null,

    @Column(columnDefinition = "TEXT")
    val memo: String? = null,

    @Column(nullable = false)
    val createdAt: LocalDateTime,

    @Column(nullable = false)
    val updatedAt: LocalDateTime
) {
    companion object {
        fun fromDomain(paymentMethod: PaymentMethod): PaymentMethodEntity {
            return PaymentMethodEntity(
                id = paymentMethod.id,
                name = paymentMethod.name,
                type = paymentMethod.type,
                assetAccountId = paymentMethod.assetAccountId,
                closingDay = paymentMethod.closingDay,
                withdrawalDay = paymentMethod.withdrawalDay,
                memo = paymentMethod.memo,
                createdAt = paymentMethod.createdAt,
                updatedAt = paymentMethod.updatedAt
            )
        }
    }

    fun toDomain(): PaymentMethod {
        return PaymentMethod(
            id = id,
            name = name,
            type = type,
            assetAccountId = assetAccountId,
            closingDay = closingDay,
            withdrawalDay = withdrawalDay,
            memo = memo,
            createdAt = createdAt,
            updatedAt = updatedAt
        )
    }
}
