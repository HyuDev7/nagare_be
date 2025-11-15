package com.paymentflow.application.usecase

import com.paymentflow.domain.model.AssetAccount
import com.paymentflow.domain.repository.AssetAccountRepository
import org.springframework.stereotype.Service
import java.math.BigDecimal

/**
 * 資産アカウントユースケース
 */
@Service
class AssetAccountUseCase(
    private val assetAccountRepository: AssetAccountRepository
) {
    /**
     * 資産アカウントを取得
     * Phase 1では単一のアカウントのみ
     */
    fun getAssetAccount(): AssetAccount? {
        return assetAccountRepository.find()
    }

    /**
     * 資産アカウントの残高を更新
     */
    fun updateBalance(newBalance: BigDecimal): AssetAccount {
        val account = assetAccountRepository.find()
            ?: throw IllegalStateException("資産アカウントが存在しません")

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
}
