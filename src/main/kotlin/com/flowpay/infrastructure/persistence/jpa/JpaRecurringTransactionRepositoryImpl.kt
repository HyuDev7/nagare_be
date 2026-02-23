package com.flowpay.infrastructure.persistence.jpa

import com.flowpay.domain.model.RecurringTransaction
import com.flowpay.domain.repository.RecurringTransactionRepository
import com.flowpay.infrastructure.persistence.jpa.entity.RecurringTransactionEntity
import org.springframework.data.repository.findByIdOrNull

class JpaRecurringTransactionRepositoryImpl(
    private val jpaRepository: RecurringTransactionJpaRepository
) : RecurringTransactionRepository {

    override fun findAll(): List<RecurringTransaction> {
        return jpaRepository.findAll()
            .map { it.toDomain() }
    }

    override fun findById(id: String): RecurringTransaction? {
        return jpaRepository.findByIdOrNull(id)
            ?.toDomain()
    }

    override fun findActive(): List<RecurringTransaction> {
        return jpaRepository.findByIsActiveTrue()
            .map { it.toDomain() }
    }

    override fun save(recurringTransaction: RecurringTransaction): RecurringTransaction {
        val entity = RecurringTransactionEntity.fromDomain(recurringTransaction)
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
