package com.paymentflow.infrastructure.persistence.csv

import com.paymentflow.domain.model.AssetAccount
import com.paymentflow.domain.repository.AssetAccountRepository
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Repository
import java.math.BigDecimal
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/**
 * 資産アカウントリポジトリのCSV実装
 */
@Repository
class CsvAssetAccountRepository(
    private val csvHelper: CsvHelper,
    @Value("\${csv.data.path:src/main/resources/data}") private val dataPath: String
) : AssetAccountRepository {

    private val filePath: String
        get() = "$dataPath/asset_accounts.csv"

    private val dateTimeFormatter = DateTimeFormatter.ISO_DATE_TIME

    override fun find(): AssetAccount? {
        val data = csvHelper.readCsvAsMap(filePath)
        return data.firstOrNull()?.let { mapToAssetAccount(it) }
    }

    override fun save(assetAccount: AssetAccount): AssetAccount {
        val headers = listOf("id", "name", "balance", "createdAt", "updatedAt")
        val data = listOf(assetAccountToMap(assetAccount))
        csvHelper.writeCsvFromMap(filePath, headers, data)
        return assetAccount
    }

    private fun mapToAssetAccount(map: Map<String, String>): AssetAccount {
        return AssetAccount(
            id = map["id"] ?: throw IllegalArgumentException("id is required"),
            name = map["name"] ?: throw IllegalArgumentException("name is required"),
            balance = map["balance"]?.let { BigDecimal(it) }
                ?: throw IllegalArgumentException("balance is required"),
            createdAt = map["createdAt"]?.let { LocalDateTime.parse(it, dateTimeFormatter) }
                ?: throw IllegalArgumentException("createdAt is required"),
            updatedAt = map["updatedAt"]?.let { LocalDateTime.parse(it, dateTimeFormatter) }
                ?: throw IllegalArgumentException("updatedAt is required")
        )
    }

    private fun assetAccountToMap(assetAccount: AssetAccount): Map<String, String> {
        return mapOf(
            "id" to assetAccount.id,
            "name" to assetAccount.name,
            "balance" to assetAccount.balance.toString(),
            "createdAt" to assetAccount.createdAt.format(dateTimeFormatter),
            "updatedAt" to assetAccount.updatedAt.format(dateTimeFormatter)
        )
    }
}
