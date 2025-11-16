package com.paymentflow.infrastructure.persistence.csv

import com.paymentflow.domain.model.Transaction
import com.paymentflow.domain.model.TransactionType
import com.paymentflow.domain.repository.TransactionRepository
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Repository
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/**
 * 取引リポジトリのCSV実装
 */
@Repository
class CsvTransactionRepository(
    private val csvHelper: CsvHelper,
    @Value("\${csv.data.path:src/main/resources/data}") private val dataPath: String
) : TransactionRepository {

    private val filePath: String
        get() = "$dataPath/transactions.csv"

    private val dateFormatter = DateTimeFormatter.ISO_DATE
    private val dateTimeFormatter = DateTimeFormatter.ISO_DATE_TIME

    override fun findAll(): List<Transaction> {
        val data = csvHelper.readCsvAsMap(filePath)
        return data.map { mapToTransaction(it) }.filter { !it.isDeleted }
    }

    override fun findById(id: String): Transaction? {
        return findAll().find { it.id == id }
    }

    override fun findByFilter(
        from: LocalDate?,
        to: LocalDate?,
        categoryId: String?,
        paymentMethodId: String?,
        type: TransactionType?
    ): List<Transaction> {
        return findAll().filter { transaction ->
            (from == null || transaction.date >= from) &&
                    (to == null || transaction.date <= to) &&
                    (categoryId == null || transaction.categoryId == categoryId) &&
                    (paymentMethodId == null || transaction.paymentMethodId == paymentMethodId) &&
                    (type == null || transaction.type == type)
        }
    }

    override fun findPendingWithdrawals(untilDate: LocalDate): List<Transaction> {
        return findAll().filter { transaction ->
            !transaction.isWithdrawn &&
                    transaction.withdrawalDate != null &&
                    transaction.withdrawalDate <= untilDate
        }
    }

    override fun findNotWithdrawn(): List<Transaction> {
        return findAll().filter { !it.isWithdrawn }
    }

    override fun findByDateRange(from: LocalDate, to: LocalDate): List<Transaction> {
        return findAll().filter { transaction ->
            transaction.date >= from && transaction.date <= to
        }
    }

    override fun save(transaction: Transaction): Transaction {
        val all = findAll().toMutableList()
        val index = all.indexOfFirst { it.id == transaction.id }

        if (index >= 0) {
            all[index] = transaction
        } else {
            all.add(transaction)
        }

        saveAll(all)
        return transaction
    }

    override fun deleteById(id: String): Boolean {
        val all = findAll().toMutableList()
        val removed = all.removeIf { it.id == id }

        if (removed) {
            saveAll(all)
        }

        return removed
    }

    private fun saveAll(transactions: List<Transaction>) {
        val headers = listOf(
            "id", "date", "amount", "type", "paymentMethodId",
            "categoryId", "memo", "withdrawalDate", "isWithdrawn", "isDeleted", "createdAt", "updatedAt"
        )
        val data = transactions.map { transactionToMap(it) }
        csvHelper.writeCsvFromMap(filePath, headers, data)
    }

    private fun mapToTransaction(map: Map<String, String>): Transaction {
        return Transaction(
            id = map["id"] ?: throw IllegalArgumentException("id is required"),
            date = map["date"]?.let { LocalDate.parse(it, dateFormatter) }
                ?: throw IllegalArgumentException("date is required"),
            amount = map["amount"]?.let { BigDecimal(it) }
                ?: throw IllegalArgumentException("amount is required"),
            type = map["type"]?.let { TransactionType.valueOf(it) }
                ?: throw IllegalArgumentException("type is required"),
            paymentMethodId = map["paymentMethodId"]
                ?: throw IllegalArgumentException("paymentMethodId is required"),
            categoryId = map["categoryId"]
                ?: throw IllegalArgumentException("categoryId is required"),
            memo = map["memo"]?.takeIf { it.isNotBlank() },
            withdrawalDate = map["withdrawalDate"]?.takeIf { it.isNotBlank() }
                ?.let { LocalDate.parse(it, dateFormatter) },
            isWithdrawn = map["isWithdrawn"]?.toBoolean() ?: false,
            isDeleted = map["isDeleted"]?.toBoolean() ?: false,
            createdAt = map["createdAt"]?.let { LocalDateTime.parse(it, dateTimeFormatter) }
                ?: throw IllegalArgumentException("createdAt is required"),
            updatedAt = map["updatedAt"]?.let { LocalDateTime.parse(it, dateTimeFormatter) }
                ?: throw IllegalArgumentException("updatedAt is required")
        )
    }

    private fun transactionToMap(transaction: Transaction): Map<String, String> {
        return mapOf(
            "id" to transaction.id,
            "date" to transaction.date.format(dateFormatter),
            "amount" to transaction.amount.toString(),
            "type" to transaction.type.name,
            "paymentMethodId" to transaction.paymentMethodId,
            "categoryId" to transaction.categoryId,
            "memo" to (transaction.memo ?: ""),
            "withdrawalDate" to (transaction.withdrawalDate?.format(dateFormatter) ?: ""),
            "isWithdrawn" to transaction.isWithdrawn.toString(),
            "isDeleted" to transaction.isDeleted.toString(),
            "createdAt" to transaction.createdAt.format(dateTimeFormatter),
            "updatedAt" to transaction.updatedAt.format(dateTimeFormatter)
        )
    }
}
