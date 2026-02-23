package com.flowpay.infrastructure.persistence.jpa

import com.flowpay.domain.model.AssetAccount
import com.flowpay.domain.repository.AssetAccountRepository
import com.flowpay.infrastructure.persistence.jpa.entity.AssetAccountEntity
import org.springframework.data.repository.findByIdOrNull

class JpaAssetAccountRepositoryImpl(
    private val jpaRepository: AssetAccountJpaRepository
) : AssetAccountRepository {

    override fun find(): AssetAccount? {
        // Phase 1互換: 最初のアカウントを返す
        return jpaRepository.findAll()
            .firstOrNull()
            ?.toDomain()
    }

    override fun findAll(): List<AssetAccount> {
        return jpaRepository.findAll()
            .map { it.toDomain() }
    }

    override fun findById(id: String): AssetAccount? {
        return jpaRepository.findByIdOrNull(id)
            ?.toDomain()
    }

    override fun save(assetAccount: AssetAccount): AssetAccount {
        val entity = AssetAccountEntity.fromDomain(assetAccount)
        val saved = jpaRepository.save(entity)
        return saved.toDomain()
    }

    override fun delete(id: String): Boolean {
        return if (jpaRepository.existsById(id)) {
            jpaRepository.deleteById(id)
            true
        } else {
            false
        }
    }
}
