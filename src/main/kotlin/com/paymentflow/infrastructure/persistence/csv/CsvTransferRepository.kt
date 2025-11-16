package com.paymentflow.infrastructure.persistence.csv

import com.paymentflow.domain.model.Transfer
import com.paymentflow.domain.repository.TransferRepository
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Repository
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/**
 * 移動記録リポジトリのCSV実装
 */
@Repository
class CsvTransferRepository(
    private val csvHelper: CsvHelper,
    @Value("\${csv.data.path:src/main/resources/data}") private val dataPath: String
) : TransferRepository {

    private val filePath: String
        get() = "$dataPath/transfers.csv"

    private val dateFormatter = DateTimeFormatter.ISO_DATE
    private val dateTimeFormatter = DateTimeFormatter.ISO_DATE_TIME

    override fun findAll(): List<Transfer> {
        val data = csvHelper.readCsvAsMap(filePath)
        return data.map { mapToTransfer(it) }
    }

    override fun findById(id: String): Transfer? {
        val data = csvHelper.readCsvAsMap(filePath)
        return data.find { it["id"] == id }?.let { mapToTransfer(it) }
    }

    override fun findByDateRange(startDate: LocalDate, endDate: LocalDate): List<Transfer> {
        return findAll().filter { it.date in startDate..endDate }
    }

    override fun save(transfer: Transfer): Transfer {
        val headers = listOf(
            "id", "date", "amount", "fromAssetAccountId", "toAssetAccountId",
            "toPaymentMethodId", "memo", "createdAt", "updatedAt"
        )
        val allTransfers = findAll().toMutableList()
        val index = allTransfers.indexOfFirst { it.id == transfer.id }

        if (index >= 0) {
            allTransfers[index] = transfer
        } else {
            allTransfers.add(transfer)
        }

        val data = allTransfers.map { transferToMap(it) }
        csvHelper.writeCsvFromMap(filePath, headers, data)
        return transfer
    }

    override fun delete(id: String): Boolean {
        val headers = listOf(
            "id", "date", "amount", "fromAssetAccountId", "toAssetAccountId",
            "toPaymentMethodId", "memo", "createdAt", "updatedAt"
        )
        val allTransfers = findAll()
        val filtered = allTransfers.filter { it.id != id }

        if (filtered.size == allTransfers.size) {
            return false
        }

        val data = filtered.map { transferToMap(it) }
        csvHelper.writeCsvFromMap(filePath, headers, data)
        return true
    }

    private fun mapToTransfer(map: Map<String, String>): Transfer {
        return Transfer(
            id = map["id"] ?: throw IllegalArgumentException("id is required"),
            date = map["date"]?.let { LocalDate.parse(it, dateFormatter) }
                ?: throw IllegalArgumentException("date is required"),
            amount = map["amount"]?.let { BigDecimal(it) }
                ?: throw IllegalArgumentException("amount is required"),
            fromAssetAccountId = map["fromAssetAccountId"]
                ?: throw IllegalArgumentException("fromAssetAccountId is required"),
            toAssetAccountId = map["toAssetAccountId"]?.takeIf { it.isNotBlank() },
            toPaymentMethodId = map["toPaymentMethodId"]?.takeIf { it.isNotBlank() },
            memo = map["memo"]?.takeIf { it.isNotBlank() },
            createdAt = map["createdAt"]?.let { LocalDateTime.parse(it, dateTimeFormatter) }
                ?: throw IllegalArgumentException("createdAt is required"),
            updatedAt = map["updatedAt"]?.let { LocalDateTime.parse(it, dateTimeFormatter) }
                ?: throw IllegalArgumentException("updatedAt is required")
        )
    }

    private fun transferToMap(transfer: Transfer): Map<String, String> {
        return mapOf(
            "id" to transfer.id,
            "date" to transfer.date.format(dateFormatter),
            "amount" to transfer.amount.toString(),
            "fromAssetAccountId" to transfer.fromAssetAccountId,
            "toAssetAccountId" to (transfer.toAssetAccountId ?: ""),
            "toPaymentMethodId" to (transfer.toPaymentMethodId ?: ""),
            "memo" to (transfer.memo ?: ""),
            "createdAt" to transfer.createdAt.format(dateTimeFormatter),
            "updatedAt" to transfer.updatedAt.format(dateTimeFormatter)
        )
    }
}
