package com.paymentflow.infrastructure.persistence.csv

import com.paymentflow.domain.model.PaymentMethod
import com.paymentflow.domain.model.PaymentMethodType
import com.paymentflow.domain.repository.PaymentMethodRepository
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Repository
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/**
 * 支払い手段リポジトリのCSV実装
 */
@Repository
class CsvPaymentMethodRepository(
    private val csvHelper: CsvHelper,
    @Value("\${csv.data.path:src/main/resources/data}") private val dataPath: String
) : PaymentMethodRepository {

    private val filePath: String
        get() = "$dataPath/payment_methods.csv"

    private val dateTimeFormatter = DateTimeFormatter.ISO_DATE_TIME

    override fun findAll(): List<PaymentMethod> {
        val data = csvHelper.readCsvAsMap(filePath)
        return data.map { mapToPaymentMethod(it) }
    }

    override fun findById(id: String): PaymentMethod? {
        return findAll().find { it.id == id }
    }

    override fun save(paymentMethod: PaymentMethod): PaymentMethod {
        val all = findAll().toMutableList()
        val index = all.indexOfFirst { it.id == paymentMethod.id }

        if (index >= 0) {
            all[index] = paymentMethod
        } else {
            all.add(paymentMethod)
        }

        saveAll(all)
        return paymentMethod
    }

    override fun deleteById(id: String): Boolean {
        val all = findAll().toMutableList()
        val removed = all.removeIf { it.id == id }

        if (removed) {
            saveAll(all)
        }

        return removed
    }

    private fun saveAll(paymentMethods: List<PaymentMethod>) {
        val headers = listOf(
            "id", "name", "type", "assetAccountId",
            "closingDay", "withdrawalDay", "memo", "createdAt", "updatedAt"
        )
        val data = paymentMethods.map { paymentMethodToMap(it) }
        csvHelper.writeCsvFromMap(filePath, headers, data)
    }

    private fun mapToPaymentMethod(map: Map<String, String>): PaymentMethod {
        return PaymentMethod(
            id = map["id"] ?: throw IllegalArgumentException("id is required"),
            name = map["name"] ?: throw IllegalArgumentException("name is required"),
            type = map["type"]?.let { PaymentMethodType.valueOf(it) }
                ?: throw IllegalArgumentException("type is required"),
            assetAccountId = map["assetAccountId"]
                ?: throw IllegalArgumentException("assetAccountId is required"),
            closingDay = map["closingDay"]?.takeIf { it.isNotBlank() }?.toInt(),
            withdrawalDay = map["withdrawalDay"]?.takeIf { it.isNotBlank() }?.toInt(),
            memo = map["memo"]?.takeIf { it.isNotBlank() },
            createdAt = map["createdAt"]?.let { LocalDateTime.parse(it, dateTimeFormatter) }
                ?: throw IllegalArgumentException("createdAt is required"),
            updatedAt = map["updatedAt"]?.let { LocalDateTime.parse(it, dateTimeFormatter) }
                ?: throw IllegalArgumentException("updatedAt is required")
        )
    }

    private fun paymentMethodToMap(paymentMethod: PaymentMethod): Map<String, String> {
        return mapOf(
            "id" to paymentMethod.id,
            "name" to paymentMethod.name,
            "type" to paymentMethod.type.name,
            "assetAccountId" to paymentMethod.assetAccountId,
            "closingDay" to (paymentMethod.closingDay?.toString() ?: ""),
            "withdrawalDay" to (paymentMethod.withdrawalDay?.toString() ?: ""),
            "memo" to (paymentMethod.memo ?: ""),
            "createdAt" to paymentMethod.createdAt.format(dateTimeFormatter),
            "updatedAt" to paymentMethod.updatedAt.format(dateTimeFormatter)
        )
    }
}
