package com.nagare.infrastructure.persistence.jpa

import com.nagare.infrastructure.persistence.jpa.entity.RecurringTransactionEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface RecurringTransactionJpaRepository : JpaRepository<RecurringTransactionEntity, String> {

    /**
     * アクティブな定期取引を取得
     */
    fun findByIsActiveTrue(): List<RecurringTransactionEntity>
}
