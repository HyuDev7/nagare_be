package com.nagare.infrastructure.persistence.jpa

import com.nagare.domain.model.TransactionType
import com.nagare.infrastructure.persistence.jpa.entity.TransactionEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.time.LocalDate

@Repository
interface TransactionJpaRepository : JpaRepository<TransactionEntity, String> {

    /**
     * 削除されていない取引のみ取得
     */
    fun findByIsDeletedFalse(): List<TransactionEntity>

    /**
     * 期間内の取引を取得（削除されていないもののみ）
     */
    fun findByDateBetweenAndIsDeletedFalse(from: LocalDate, to: LocalDate): List<TransactionEntity>

    /**
     * 引き落とし予定の取引を取得
     * 削除されておらず、未引き落としで、引き落とし日が指定日以前のもの
     */
    @Query(
        """
        SELECT t FROM TransactionEntity t
        WHERE t.isDeleted = false
        AND t.isWithdrawn = false
        AND t.withdrawalDate IS NOT NULL
        AND t.withdrawalDate <= :untilDate
        """
    )
    fun findPendingWithdrawals(@Param("untilDate") untilDate: LocalDate): List<TransactionEntity>

    /**
     * 未引き落としの取引を取得
     */
    fun findByIsDeletedFalseAndIsWithdrawnFalse(): List<TransactionEntity>

    // findByFilter is now implemented using Criteria API in JpaTransactionRepositoryImpl
    // to avoid PostgreSQL type inference issues with nullable parameters
}
