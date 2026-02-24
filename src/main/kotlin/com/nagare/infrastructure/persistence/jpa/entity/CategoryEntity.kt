package com.nagare.infrastructure.persistence.jpa.entity

import com.nagare.domain.model.Category
import com.nagare.domain.model.CategoryType
import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "categories")
data class CategoryEntity(
    @Id
    @Column(length = 50)
    val id: String,

    @Column(nullable = false, length = 100)
    val name: String,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    val type: CategoryType,

    @Column(nullable = false)
    val createdAt: LocalDateTime
) {
    companion object {
        fun fromDomain(category: Category): CategoryEntity {
            return CategoryEntity(
                id = category.id,
                name = category.name,
                type = category.type,
                createdAt = category.createdAt
            )
        }
    }

    fun toDomain(): Category {
        return Category(
            id = id,
            name = name,
            type = type,
            createdAt = createdAt
        )
    }
}
