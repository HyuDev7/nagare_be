package com.paymentflow.domain.repository

import com.paymentflow.domain.model.Category
import com.paymentflow.domain.model.CategoryType

/**
 * カテゴリリポジトリインターフェース
 */
interface CategoryRepository {
    /**
     * すべてのカテゴリを取得
     */
    fun findAll(): List<Category>

    /**
     * IDでカテゴリを取得
     */
    fun findById(id: String): Category?

    /**
     * タイプでカテゴリを取得
     */
    fun findByType(type: CategoryType): List<Category>

    /**
     * カテゴリを保存
     */
    fun save(category: Category): Category

    /**
     * カテゴリを削除
     */
    fun deleteById(id: String): Boolean
}
