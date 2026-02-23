package com.flowpay.infrastructure.persistence.csv

import com.flowpay.domain.model.Category
import com.flowpay.domain.model.CategoryType
import com.flowpay.domain.repository.CategoryRepository
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Repository
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/**
 * カテゴリリポジトリのCSV実装
 */
@Repository
class CsvCategoryRepository(
    private val csvHelper: CsvHelper,
    @Value("\${csv.data.path:src/main/resources/data}") private val dataPath: String
) : CategoryRepository {

    private val filePath: String
        get() = "$dataPath/categories.csv"

    private val dateTimeFormatter = DateTimeFormatter.ISO_DATE_TIME

    override fun findAll(): List<Category> {
        val data = csvHelper.readCsvAsMap(filePath)
        return data.map { mapToCategory(it) }
    }

    override fun findById(id: String): Category? {
        return findAll().find { it.id == id }
    }

    override fun findByType(type: CategoryType): List<Category> {
        return findAll().filter { it.type == type }
    }

    override fun save(category: Category): Category {
        val all = findAll().toMutableList()
        val index = all.indexOfFirst { it.id == category.id }

        if (index >= 0) {
            all[index] = category
        } else {
            all.add(category)
        }

        saveAll(all)
        return category
    }

    override fun deleteById(id: String): Boolean {
        val all = findAll().toMutableList()
        val removed = all.removeIf { it.id == id }

        if (removed) {
            saveAll(all)
        }

        return removed
    }

    private fun saveAll(categories: List<Category>) {
        val headers = listOf("id", "name", "type", "createdAt")
        val data = categories.map { categoryToMap(it) }
        csvHelper.writeCsvFromMap(filePath, headers, data)
    }

    private fun mapToCategory(map: Map<String, String>): Category {
        return Category(
            id = map["id"] ?: throw IllegalArgumentException("id is required"),
            name = map["name"] ?: throw IllegalArgumentException("name is required"),
            type = map["type"]?.let { CategoryType.valueOf(it) }
                ?: throw IllegalArgumentException("type is required"),
            createdAt = map["createdAt"]?.let { LocalDateTime.parse(it, dateTimeFormatter) }
                ?: throw IllegalArgumentException("createdAt is required")
        )
    }

    private fun categoryToMap(category: Category): Map<String, String> {
        return mapOf(
            "id" to category.id,
            "name" to category.name,
            "type" to category.type.name,
            "createdAt" to category.createdAt.format(dateTimeFormatter)
        )
    }
}
