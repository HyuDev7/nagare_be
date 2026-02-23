package com.flowpay.infrastructure.persistence.jpa

import com.flowpay.infrastructure.persistence.jpa.entity.PaymentMethodEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface PaymentMethodJpaRepository : JpaRepository<PaymentMethodEntity, String>
