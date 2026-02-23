package com.flowpay.infrastructure.persistence.jpa.entity

import com.flowpay.domain.model.AssetAccount
import jakarta.persistence.*
import java.math.BigDecimal
import java.time.LocalDateTime

@Entity
@Table(name = "asset_accounts")
data class AssetAccountEntity(
    @Id
    @Column(length = 50)
    val id: String,

    @Column(nullable = false, length = 100)
    val name: String,

    @Column(nullable = false, precision = 15, scale = 2)
    val balance: BigDecimal,

    @Column(nullable = false)
    val createdAt: LocalDateTime,

    @Column(nullable = false)
    val updatedAt: LocalDateTime
) {
    companion object {
        fun fromDomain(assetAccount: AssetAccount): AssetAccountEntity {
            return AssetAccountEntity(
                id = assetAccount.id,
                name = assetAccount.name,
                balance = assetAccount.balance,
                createdAt = assetAccount.createdAt,
                updatedAt = assetAccount.updatedAt
            )
        }
    }

    fun toDomain(): AssetAccount {
        return AssetAccount(
            id = id,
            name = name,
            balance = balance,
            createdAt = createdAt,
            updatedAt = updatedAt
        )
    }
}
