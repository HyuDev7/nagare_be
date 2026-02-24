package com.nagare.domain.model

import java.time.LocalDateTime

/**
 * カテゴリ
 */
data class Category(
    val id: String,
    val name: String,
    val type: CategoryType,
    val createdAt: LocalDateTime
)
