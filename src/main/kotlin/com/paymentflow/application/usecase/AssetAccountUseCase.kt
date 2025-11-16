package com.paymentflow.application.usecase

import com.paymentflow.domain.model.AssetAccount
import com.paymentflow.domain.repository.AssetAccountRepository
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.time.LocalDateTime

/**
 * 資産アカウントユースケース
 */
@Service
class AssetAccountUseCase(
    private val assetAccountRepository: AssetAccountRepository
) {
    /**
     * 資産アカウントを取得
     * Phase 1互換: 最初のアカウントを返す
     */
    fun getAssetAccount(): AssetAccount? {
        return assetAccountRepository.find()
    }

    /**
     * すべての資産アカウントを取得
     */
    fun getAllAssetAccounts(): List<AssetAccount> {
        return assetAccountRepository.findAll()
    }

    /**
     * IDで資産アカウントを取得
     */
    fun getAssetAccountById(id: String): AssetAccount {
        return assetAccountRepository.findById(id)
            ?: throw IllegalArgumentException("資産アカウントが見つかりません: $id")
    }

    /**
     * 資産アカウントを作成
     */
    fun createAssetAccount(name: String, initialBalance: BigDecimal): AssetAccount {
        val now = LocalDateTime.now()
        val newId = "acc_${System.currentTimeMillis()}"

        val assetAccount = AssetAccount(
            id = newId,
            name = name,
            balance = initialBalance,
            createdAt = now,
            updatedAt = now
        )

        return assetAccountRepository.save(assetAccount)
    }

    /**
     * 資産アカウント名を更新
     */
    fun updateAssetAccountName(id: String, name: String): AssetAccount {
        val account = getAssetAccountById(id)
        val updated = account.copy(
            name = name,
            updatedAt = LocalDateTime.now()
        )
        return assetAccountRepository.save(updated)
    }

    /**
     * 資産アカウントの残高を調整（Phase 1互換）
     */
    fun updateBalance(newBalance: BigDecimal): AssetAccount {
        val account = assetAccountRepository.find()
            ?: throw IllegalStateException("資産アカウントが存在しません")

        val updatedAccount = account.updateBalance(newBalance)
        return assetAccountRepository.save(updatedAccount)
    }

    /**
     * 資産アカウントの残高を調整（ID指定）
     */
    fun updateBalanceById(id: String, newBalance: BigDecimal): AssetAccount {
        val account = getAssetAccountById(id)
        val updatedAccount = account.updateBalance(newBalance)
        return assetAccountRepository.save(updatedAccount)
    }

    /**
     * 資産アカウントに収入を追加
     */
    fun addIncome(amount: BigDecimal): AssetAccount {
        val account = assetAccountRepository.find()
            ?: throw IllegalStateException("資産アカウントが存在しません")

        val updatedAccount = account.addIncome(amount)
        return assetAccountRepository.save(updatedAccount)
    }

    /**
     * 資産アカウントから支出を減算
     */
    fun subtractExpense(amount: BigDecimal): AssetAccount {
        val account = assetAccountRepository.find()
            ?: throw IllegalStateException("資産アカウントが存在しません")

        val updatedAccount = account.subtractExpense(amount)
        return assetAccountRepository.save(updatedAccount)
    }

    /**
     * 資産アカウントを削除
     */
    fun deleteAssetAccount(id: String): Boolean {
        return assetAccountRepository.delete(id)
    }
}
