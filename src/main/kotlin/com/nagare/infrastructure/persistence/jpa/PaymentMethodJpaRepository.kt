package com.nagare.infrastructure.persistence.jpa

import com.nagare.infrastructure.persistence.jpa.entity.PaymentMethodEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface PaymentMethodJpaRepository : JpaRepository<PaymentMethodEntity, String>
