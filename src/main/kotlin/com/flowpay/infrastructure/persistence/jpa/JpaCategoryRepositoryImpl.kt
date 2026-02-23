package com.flowpay.infrastructure.persistence.jpa

import com.flowpay.domain.model.Category
import com.flowpay.domain.model.CategoryType
import com.flowpay.domain.repository.CategoryRepository
import com.flowpay.infrastructure.persistence.jpa.entity.CategoryEntity
import org.springframework.data.repository.findByIdOrNull

class JpaCategoryRepositoryImpl(
    private val jpaRepository: CategoryJpaRepository
) : CategoryRepository {

    override fun findAll(): List<Category> {
        return jpaRepository.findAll()
            .map { it.toDomain() }
    }

    override fun findById(id: String): Category? {
        return jpaRepository.findByIdOrNull(id)
            ?.toDomain()
    }

    override fun findByType(type: CategoryType): List<Category> {
        return jpaRepository.findByType(type)
            .map { it.toDomain() }
    }

    override fun save(category: Category): Category {
        val entity = CategoryEntity.fromDomain(category)
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
