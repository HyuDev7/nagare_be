package com.nagare.infrastructure.migration

import com.nagare.domain.model.*
import com.nagare.infrastructure.persistence.csv.*
import com.nagare.infrastructure.persistence.jpa.*
import org.slf4j.LoggerFactory
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.stereotype.Component

/**
 * CSVからPostgreSQLへのデータ移行ツール
 *
 * 使用方法:
 * ./gradlew bootRun --args='--spring.profiles.active=jpa --migration.enabled=true'
 */
@Component
@ConditionalOnProperty(name = ["migration.enabled"], havingValue = "true")
class DataMigrationTool(
    private val csvHelper: CsvHelper,
    private val transactionJpaRepository: TransactionJpaRepository,
    private val assetAccountJpaRepository: AssetAccountJpaRepository,
    private val paymentMethodJpaRepository: PaymentMethodJpaRepository,
    private val categoryJpaRepository: CategoryJpaRepository,
    private val recurringTransactionJpaRepository: RecurringTransactionJpaRepository
) : CommandLineRunner {

    private val logger = LoggerFactory.getLogger(DataMigrationTool::class.java)
    private val dataPath = "src/main/resources/data"

    override fun run(vararg args: String?) {
        logger.info("========================================")
        logger.info("Starting data migration from CSV to PostgreSQL")
        logger.info("========================================")

        try {
            migrateAssetAccounts()
            migrateCategories()
            migratePaymentMethods()
            migrateTransactions()
            migrateRecurringTransactions()

            logger.info("========================================")
            logger.info("Migration completed successfully!")
            logger.info("========================================")
            logger.info("Next steps:")
            logger.info("1. Verify the data in PostgreSQL")
            logger.info("2. Update application.yaml to use 'jpa' profile by default")
            logger.info("3. Restart the application without --migration.enabled flag")
        } catch (e: Exception) {
            logger.error("Migration failed: ${e.message}", e)
            throw e
        }
    }

    private fun migrateAssetAccounts() {
        logger.info("Migrating asset accounts...")
        val csvRepo = CsvAssetAccountRepository(csvHelper, dataPath)
        val accounts = csvRepo.findAll()

        accounts.forEach { account ->
            val entity = com.flowpay.infrastructure.persistence.jpa.entity.AssetAccountEntity.fromDomain(account)
            assetAccountJpaRepository.save(entity)
        }

        logger.info("Migrated ${accounts.size} asset accounts")
    }

    private fun migrateCategories() {
        logger.info("Migrating categories...")
        val csvRepo = CsvCategoryRepository(csvHelper, dataPath)
        val categories = csvRepo.findAll()

        categories.forEach { category ->
            val entity = com.flowpay.infrastructure.persistence.jpa.entity.CategoryEntity.fromDomain(category)
            categoryJpaRepository.save(entity)
        }

        logger.info("Migrated ${categories.size} categories")
    }

    private fun migratePaymentMethods() {
        logger.info("Migrating payment methods...")
        val csvRepo = CsvPaymentMethodRepository(csvHelper, dataPath)
        val paymentMethods = csvRepo.findAll()

        paymentMethods.forEach { paymentMethod ->
            val entity = com.flowpay.infrastructure.persistence.jpa.entity.PaymentMethodEntity.fromDomain(paymentMethod)
            paymentMethodJpaRepository.save(entity)
        }

        logger.info("Migrated ${paymentMethods.size} payment methods")
    }

    private fun migrateTransactions() {
        logger.info("Migrating transactions...")
        val csvRepo = CsvTransactionRepository(csvHelper, dataPath)
        val transactions = csvRepo.findAll()

        transactions.forEach { transaction ->
            val entity = com.flowpay.infrastructure.persistence.jpa.entity.TransactionEntity.fromDomain(transaction)
            transactionJpaRepository.save(entity)
        }

        logger.info("Migrated ${transactions.size} transactions")
    }

    private fun migrateRecurringTransactions() {
        logger.info("Migrating recurring transactions...")
        val csvRepo = CsvRecurringTransactionRepository(csvHelper, dataPath)
        val recurringTransactions = csvRepo.findAll()

        recurringTransactions.forEach { recurringTransaction ->
            val entity = com.flowpay.infrastructure.persistence.jpa.entity.RecurringTransactionEntity.fromDomain(recurringTransaction)
            recurringTransactionJpaRepository.save(entity)
        }

        logger.info("Migrated ${recurringTransactions.size} recurring transactions")
    }
}
