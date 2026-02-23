package com.flowpay.infrastructure.persistence.jpa

import com.flowpay.domain.model.CategoryType
import com.flowpay.infrastructure.persistence.jpa.entity.CategoryEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface CategoryJpaRepository : JpaRepository<CategoryEntity, String> {

    /**
     * タイプでカテゴリを取得
     */
    fun findByType(type: CategoryType): List<CategoryEntity>
}
