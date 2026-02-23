package com.flowpay.infrastructure.persistence.jpa

import com.flowpay.domain.model.Transaction
import com.flowpay.domain.model.TransactionType
import com.flowpay.domain.repository.TransactionRepository
import com.flowpay.infrastructure.persistence.jpa.entity.TransactionEntity
import jakarta.persistence.EntityManager
import jakarta.persistence.criteria.Predicate
import org.springframework.data.repository.findByIdOrNull
import java.time.LocalDate

class JpaTransactionRepositoryImpl(
    private val jpaRepository: TransactionJpaRepository,
    private val entityManager: EntityManager
) : TransactionRepository {

    override fun findAll(): List<Transaction> {
        return jpaRepository.findByIsDeletedFalse()
            .map { it.toDomain() }
    }

    override fun findById(id: String): Transaction? {
        return jpaRepository.findByIdOrNull(id)
            ?.takeIf { !it.isDeleted }
            ?.toDomain()
    }

    override fun findByFilter(
        from: LocalDate?,
        to: LocalDate?,
        categoryId: String?,
        paymentMethodId: String?,
        type: TransactionType?
    ): List<Transaction> {
        val cb = entityManager.criteriaBuilder
        val cq = cb.createQuery(TransactionEntity::class.java)
        val root = cq.from(TransactionEntity::class.java)

        val predicates = mutableListOf<Predicate>()

        // 削除されていない
        predicates.add(cb.equal(root.get<Boolean>("isDeleted"), false))

        // 日付範囲フィルター
        from?.let {
            predicates.add(cb.greaterThanOrEqualTo(root.get("date"), it))
        }
        to?.let {
            predicates.add(cb.lessThanOrEqualTo(root.get("date"), it))
        }

        // カテゴリフィルター
        categoryId?.let {
            predicates.add(cb.equal(root.get<String>("categoryId"), it))
        }

        // 支払い手段フィルター
        paymentMethodId?.let {
            predicates.add(cb.equal(root.get<String>("paymentMethodId"), it))
        }

        // タイプフィルター
        type?.let {
            predicates.add(cb.equal(root.get<TransactionType>("type"), it))
        }

        cq.where(*predicates.toTypedArray())
        cq.orderBy(
            cb.desc(root.get<LocalDate>("date")),
            cb.desc(root.get<LocalDate>("createdAt"))
        )

        return entityManager.createQuery(cq)
            .resultList
            .map { it.toDomain() }
    }

    override fun findPendingWithdrawals(untilDate: LocalDate): List<Transaction> {
        return jpaRepository.findPendingWithdrawals(untilDate)
            .map { it.toDomain() }
    }

    override fun findNotWithdrawn(): List<Transaction> {
        return jpaRepository.findByIsDeletedFalseAndIsWithdrawnFalse()
            .map { it.toDomain() }
    }

    override fun findByDateRange(from: LocalDate, to: LocalDate): List<Transaction> {
        return jpaRepository.findByDateBetweenAndIsDeletedFalse(from, to)
            .map { it.toDomain() }
    }

    override fun save(transaction: Transaction): Transaction {
        val entity = TransactionEntity.fromDomain(transaction)
        val saved = jpaRepository.save(entity)
        return saved.toDomain()
    }

    override fun deleteById(id: String): Boolean {
        val entity = jpaRepository.findByIdOrNull(id) ?: return false
        // 論理削除
        val deleted = entity.copy(isDeleted = true)
        jpaRepository.save(deleted)
        return true
    }
}
