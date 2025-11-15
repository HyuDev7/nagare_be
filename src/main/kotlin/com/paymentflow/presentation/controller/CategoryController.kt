package com.paymentflow.presentation.controller

import com.paymentflow.application.usecase.CategoryUseCase
import com.paymentflow.presentation.dto.request.CreateCategoryRequest
import com.paymentflow.presentation.dto.request.UpdateCategoryRequest
import com.paymentflow.presentation.dto.response.CategoryResponse
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

/**
 * カテゴリコントローラー
 */
@RestController
@RequestMapping("/api/categories")
class CategoryController(
    private val categoryUseCase: CategoryUseCase
) {
    /**
     * カテゴリ一覧取得
     */
    @GetMapping
    fun getAllCategories(): ResponseEntity<List<CategoryResponse>> {
        val categories = categoryUseCase.getAllCategories()
        val response = categories.map { CategoryResponse.from(it) }
        return ResponseEntity.ok(response)
    }

    /**
     * カテゴリ取得
     */
    @GetMapping("/{id}")
    fun getCategory(@PathVariable id: String): ResponseEntity<CategoryResponse> {
        val category = categoryUseCase.getCategoryById(id)
            ?: return ResponseEntity.notFound().build()

        return ResponseEntity.ok(CategoryResponse.from(category))
    }

    /**
     * カテゴリ登録
     */
    @PostMapping
    fun createCategory(
        @RequestBody request: CreateCategoryRequest
    ): ResponseEntity<CategoryResponse> {
        val category = categoryUseCase.createCategory(
            name = request.name,
            type = request.type
        )

        return ResponseEntity.status(HttpStatus.CREATED)
            .body(CategoryResponse.from(category))
    }

    /**
     * カテゴリ更新
     */
    @PutMapping("/{id}")
    fun updateCategory(
        @PathVariable id: String,
        @RequestBody request: UpdateCategoryRequest
    ): ResponseEntity<CategoryResponse> {
        val category = categoryUseCase.updateCategory(
            id = id,
            name = request.name,
            type = request.type
        )

        return ResponseEntity.ok(CategoryResponse.from(category))
    }

    /**
     * カテゴリ削除
     */
    @DeleteMapping("/{id}")
    fun deleteCategory(@PathVariable id: String): ResponseEntity<Void> {
        val deleted = categoryUseCase.deleteCategory(id)
        return if (deleted) {
            ResponseEntity.noContent().build()
        } else {
            ResponseEntity.notFound().build()
        }
    }
}
