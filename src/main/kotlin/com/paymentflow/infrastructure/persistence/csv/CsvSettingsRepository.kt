package com.paymentflow.infrastructure.persistence.csv

import com.paymentflow.domain.repository.SettingsRepository
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Repository
import java.math.BigDecimal
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/**
 * 設定リポジトリのCSV実装
 */
@Repository
class CsvSettingsRepository(
    private val csvHelper: CsvHelper,
    @Value("\${csv.data.path:src/main/resources/data}") private val dataPath: String
) : SettingsRepository {

    private val filePath: String
        get() = "$dataPath/settings.csv"

    private val dateTimeFormatter = DateTimeFormatter.ISO_DATE_TIME

    companion object {
        private const val MONTHLY_BUDGET_KEY = "monthly_budget"
    }

    override fun get(key: String): String? {
        val settings = readAllSettings()
        return settings[key]
    }

    override fun set(key: String, value: String) {
        val settings = readAllSettings().toMutableMap()
        settings[key] = value
        writeAllSettings(settings)
    }

    override fun getMonthlyBudget(): BigDecimal? {
        return get(MONTHLY_BUDGET_KEY)?.let { BigDecimal(it) }
    }

    override fun setMonthlyBudget(budget: BigDecimal) {
        set(MONTHLY_BUDGET_KEY, budget.toString())
    }

    private fun readAllSettings(): Map<String, String> {
        val data = csvHelper.readCsvAsMap(filePath)
        return data.associate { (it["key"] ?: "") to (it["value"] ?: "") }
    }

    private fun writeAllSettings(settings: Map<String, String>) {
        val headers = listOf("key", "value", "updatedAt")
        val now = LocalDateTime.now().format(dateTimeFormatter)
        val data = settings.map { (key, value) ->
            mapOf(
                "key" to key,
                "value" to value,
                "updatedAt" to now
            )
        }
        csvHelper.writeCsvFromMap(filePath, headers, data)
    }
}
