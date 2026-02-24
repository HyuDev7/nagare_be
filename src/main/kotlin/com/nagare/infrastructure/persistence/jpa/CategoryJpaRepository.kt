package com.nagare.infrastructure.persistence.jpa

import com.nagare.domain.model.CategoryType
import com.nagare.infrastructure.persistence.jpa.entity.CategoryEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface CategoryJpaRepository : JpaRepository<CategoryEntity, String> {

    /**
     * タイプでカテゴリを取得
     */
    fun findByType(type: CategoryType): List<CategoryEntity>
}
