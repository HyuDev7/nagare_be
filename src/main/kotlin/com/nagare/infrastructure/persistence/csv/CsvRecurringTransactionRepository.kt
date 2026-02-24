package com.nagare.infrastructure.persistence.csv

import com.nagare.domain.model.RecurringFrequency
import com.nagare.domain.model.RecurringTransaction
import com.nagare.domain.model.TransactionType
import com.nagare.domain.repository.RecurringTransactionRepository
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Repository
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Repository
class CsvRecurringTransactionRepository(
    private val csvHelper: CsvHelper,
    @Value("\${csv.data.path:src/main/resources/data}") private val dataPath: String
) : RecurringTransactionRepository {

    private val filePath: String
        get() = "$dataPath/recurring_transactions.csv"

    private val dateFormatter = DateTimeFormatter.ISO_DATE
    private val dateTimeFormatter = DateTimeFormatter.ISO_DATE_TIME

    override fun findAll(): List<RecurringTransaction> {
        val data = csvHelper.readCsvAsMap(filePath)
        return data.map { mapToRecurringTransaction(it) }
    }

    override fun findById(id: String): RecurringTransaction? {
        return findAll().find { it.id == id }
    }

    override fun findActive(): List<RecurringTransaction> {
        return findAll().filter { it.isActive }
    }

    override fun save(recurringTransaction: RecurringTransaction): RecurringTransaction {
        val headers = listOf(
            "id", "name", "amount", "type", "paymentMethodId", "categoryId",
            "frequency", "startDate", "endDate", "dayOfMonth", "dayOfWeek",
            "memo", "isActive", "createdAt", "updatedAt"
        )
        val all = findAll().toMutableList()
        val index = all.indexOfFirst { it.id == recurringTransaction.id }

        if (index >= 0) {
            all[index] = recurringTransaction
        } else {
            all.add(recurringTransaction)
        }

        val data = all.map { toMap(it) }
        csvHelper.writeCsvFromMap(filePath, headers, data)
        return recurringTransaction
    }

    override fun delete(id: String): Boolean {
        val headers = listOf(
            "id", "name", "amount", "type", "paymentMethodId", "categoryId",
            "frequency", "startDate", "endDate", "dayOfMonth", "dayOfWeek",
            "memo", "isActive", "createdAt", "updatedAt"
        )
        val all = findAll()
        val filtered = all.filter { it.id != id }

        if (filtered.size == all.size) {
            return false
        }

        val data = filtered.map { toMap(it) }
        csvHelper.writeCsvFromMap(filePath, headers, data)
        return true
    }

    private fun mapToRecurringTransaction(map: Map<String, String>): RecurringTransaction {
        return RecurringTransaction(
            id = map["id"] ?: throw IllegalArgumentException("id is required"),
            name = map["name"] ?: throw IllegalArgumentException("name is required"),
            amount = map["amount"]?.let { BigDecimal(it) }
                ?: throw IllegalArgumentException("amount is required"),
            type = map["type"]?.let { TransactionType.valueOf(it) }
                ?: throw IllegalArgumentException("type is required"),
            paymentMethodId = map["paymentMethodId"]
                ?: throw IllegalArgumentException("paymentMethodId is required"),
            categoryId = map["categoryId"]
                ?: throw IllegalArgumentException("categoryId is required"),
            frequency = map["frequency"]?.let { RecurringFrequency.valueOf(it) }
                ?: throw IllegalArgumentException("frequency is required"),
            startDate = map["startDate"]?.let { LocalDate.parse(it, dateFormatter) }
                ?: throw IllegalArgumentException("startDate is required"),
            endDate = map["endDate"]?.takeIf { it.isNotBlank() }
                ?.let { LocalDate.parse(it, dateFormatter) },
            dayOfMonth = map["dayOfMonth"]?.takeIf { it.isNotBlank() }?.toInt(),
            dayOfWeek = map["dayOfWeek"]?.takeIf { it.isNotBlank() }?.toInt(),
            memo = map["memo"]?.takeIf { it.isNotBlank() },
            isActive = map["isActive"]?.toBoolean() ?: true,
            createdAt = map["createdAt"]?.let { LocalDateTime.parse(it, dateTimeFormatter) }
                ?: throw IllegalArgumentException("createdAt is required"),
            updatedAt = map["updatedAt"]?.let { LocalDateTime.parse(it, dateTimeFormatter) }
                ?: throw IllegalArgumentException("updatedAt is required")
        )
    }

    private fun toMap(rt: RecurringTransaction): Map<String, String> {
        return mapOf(
            "id" to rt.id,
            "name" to rt.name,
            "amount" to rt.amount.toString(),
            "type" to rt.type.name,
            "paymentMethodId" to rt.paymentMethodId,
            "categoryId" to rt.categoryId,
            "frequency" to rt.frequency.name,
            "startDate" to rt.startDate.format(dateFormatter),
            "endDate" to (rt.endDate?.format(dateFormatter) ?: ""),
            "dayOfMonth" to (rt.dayOfMonth?.toString() ?: ""),
            "dayOfWeek" to (rt.dayOfWeek?.toString() ?: ""),
            "memo" to (rt.memo ?: ""),
            "isActive" to rt.isActive.toString(),
            "createdAt" to rt.createdAt.format(dateTimeFormatter),
            "updatedAt" to rt.updatedAt.format(dateTimeFormatter)
        )
    }
}
