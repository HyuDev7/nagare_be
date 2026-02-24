package com.nagare.presentation.config

import com.nagare.domain.repository.*
import com.nagare.infrastructure.persistence.csv.*
import com.nagare.infrastructure.persistence.jpa.*
import jakarta.persistence.EntityManager
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary

/**
 * リポジトリの実装を切り替える設定クラス
 * app.storage.type プロパティで切り替え
 * - csv: CSV実装を使用（デフォルト）
 * - jpa: PostgreSQL実装を使用
 */
@Configuration
class RepositoryConfig {

    @Value("\${csv.data.path:src/main/resources/data}")
    private lateinit var dataPath: String

    // ========================================
    // CSV実装（デフォルト）
    // ========================================

    @Bean
    @Primary
    @ConditionalOnProperty(name = ["app.storage.type"], havingValue = "csv", matchIfMissing = true)
    fun csvTransactionRepository(csvHelper: CsvHelper): TransactionRepository {
        return CsvTransactionRepository(csvHelper, dataPath)
    }

    @Bean
    @Primary
    @ConditionalOnProperty(name = ["app.storage.type"], havingValue = "csv", matchIfMissing = true)
    fun csvAssetAccountRepository(csvHelper: CsvHelper): AssetAccountRepository {
        return CsvAssetAccountRepository(csvHelper, dataPath)
    }

    @Bean
    @Primary
    @ConditionalOnProperty(name = ["app.storage.type"], havingValue = "csv", matchIfMissing = true)
    fun csvPaymentMethodRepository(csvHelper: CsvHelper): PaymentMethodRepository {
        return CsvPaymentMethodRepository(csvHelper, dataPath)
    }

    @Bean
    @Primary
    @ConditionalOnProperty(name = ["app.storage.type"], havingValue = "csv", matchIfMissing = true)
    fun csvCategoryRepository(csvHelper: CsvHelper): CategoryRepository {
        return CsvCategoryRepository(csvHelper, dataPath)
    }

    @Bean
    @Primary
    @ConditionalOnProperty(name = ["app.storage.type"], havingValue = "csv", matchIfMissing = true)
    fun csvRecurringTransactionRepository(csvHelper: CsvHelper): RecurringTransactionRepository {
        return CsvRecurringTransactionRepository(csvHelper, dataPath)
    }

    @Bean
    @Primary
    @ConditionalOnProperty(name = ["app.storage.type"], havingValue = "csv", matchIfMissing = true)
    fun csvTransferRepository(csvHelper: CsvHelper): TransferRepository {
        return CsvTransferRepository(csvHelper, dataPath)
    }

    @Bean
    @Primary
    @ConditionalOnProperty(name = ["app.storage.type"], havingValue = "csv", matchIfMissing = true)
    fun csvSettingsRepository(csvHelper: CsvHelper): SettingsRepository {
        return CsvSettingsRepository(csvHelper, dataPath)
    }

    // ========================================
    // PostgreSQL実装
    // ========================================

    @Bean
    @Primary
    @ConditionalOnProperty(name = ["app.storage.type"], havingValue = "jpa")
    fun jpaTransactionRepository(
        jpaRepository: TransactionJpaRepository,
        entityManager: EntityManager
    ): TransactionRepository {
        return JpaTransactionRepositoryImpl(jpaRepository, entityManager)
    }

    @Bean
    @Primary
    @ConditionalOnProperty(name = ["app.storage.type"], havingValue = "jpa")
    fun jpaAssetAccountRepository(jpaRepository: AssetAccountJpaRepository): AssetAccountRepository {
        return JpaAssetAccountRepositoryImpl(jpaRepository)
    }

    @Bean
    @Primary
    @ConditionalOnProperty(name = ["app.storage.type"], havingValue = "jpa")
    fun jpaPaymentMethodRepository(jpaRepository: PaymentMethodJpaRepository): PaymentMethodRepository {
        return JpaPaymentMethodRepositoryImpl(jpaRepository)
    }

    @Bean
    @Primary
    @ConditionalOnProperty(name = ["app.storage.type"], havingValue = "jpa")
    fun jpaCategoryRepository(jpaRepository: CategoryJpaRepository): CategoryRepository {
        return JpaCategoryRepositoryImpl(jpaRepository)
    }

    @Bean
    @Primary
    @ConditionalOnProperty(name = ["app.storage.type"], havingValue = "jpa")
    fun jpaRecurringTransactionRepository(jpaRepository: RecurringTransactionJpaRepository): RecurringTransactionRepository {
        return JpaRecurringTransactionRepositoryImpl(jpaRepository)
    }

    // Note: TransferRepository and SettingsRepository are not yet implemented for JPA
    // They will continue to use CSV implementation for now
}
