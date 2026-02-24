package com.nagare.infrastructure.persistence.jpa

import com.nagare.domain.model.PaymentMethod
import com.nagare.domain.repository.PaymentMethodRepository
import com.nagare.infrastructure.persistence.jpa.entity.PaymentMethodEntity
import org.springframework.data.repository.findByIdOrNull

class JpaPaymentMethodRepositoryImpl(
    private val jpaRepository: PaymentMethodJpaRepository
) : PaymentMethodRepository {

    override fun findAll(): List<PaymentMethod> {
        return jpaRepository.findAll()
            .map { it.toDomain() }
    }

    override fun findById(id: String): PaymentMethod? {
        return jpaRepository.findByIdOrNull(id)
            ?.toDomain()
    }

    override fun save(paymentMethod: PaymentMethod): PaymentMethod {
        val entity = PaymentMethodEntity.fromDomain(paymentMethod)
        val saved = jpaRepository.save(entity)
        return saved.toDomain()
    }

    override fun deleteById(id: String): Boolean {
        return if (jpaRepository.existsById(id)) {
            jpaRepository.deleteById(id)
            true
        } else {
            false
        }
    }
}
