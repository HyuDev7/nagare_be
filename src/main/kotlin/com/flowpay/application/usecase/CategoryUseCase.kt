package com.flowpay.application.usecase

import com.flowpay.domain.model.Category
import com.flowpay.domain.model.CategoryType
import com.flowpay.domain.repository.CategoryRepository
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.util.*

/**
 * カテゴリユースケース
 */
@Service
class CategoryUseCase(
    private val categoryRepository: CategoryRepository
) {
    /**
     * すべてのカテゴリを取得
     */
    fun getAllCategories(): List<Category> {
        return categoryRepository.findAll()
    }

    /**
     * IDでカテゴリを取得
     */
    fun getCategoryById(id: String): Category? {
        return categoryRepository.findById(id)
    }

    /**
     * タイプでカテゴリを取得
     */
    fun getCategoriesByType(type: CategoryType): List<Category> {
        return categoryRepository.findByType(type)
    }

    /**
     * カテゴリを作成
     */
    fun createCategory(name: String, type: CategoryType): Category {
        val category = Category(
            id = "cat_${UUID.randomUUID()}",
            name = name,
            type = type,
            createdAt = LocalDateTime.now()
        )

        return categoryRepository.save(category)
    }

    /**
     * カテゴリを更新
     */
    fun updateCategory(id: String, name: String, type: CategoryType): Category {
        val existing = categoryRepository.findById(id)
            ?: throw IllegalArgumentException("カテゴリが見つかりません: $id")

        val updated = Category(
            id = existing.id,
            name = name,
            type = type,
            createdAt = existing.createdAt
        )

        return categoryRepository.save(updated)
    }

    /**
     * カテゴリを削除
     */
    fun deleteCategory(id: String): Boolean {
        return categoryRepository.deleteById(id)
    }
}
