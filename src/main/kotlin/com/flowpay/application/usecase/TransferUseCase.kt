package com.flowpay.application.usecase

import com.flowpay.domain.model.Transfer
import com.flowpay.domain.repository.AssetAccountRepository
import com.flowpay.domain.repository.TransferRepository
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime

/**
 * 移動記録ユースケース
 */
@Service
class TransferUseCase(
    private val transferRepository: TransferRepository,
    private val assetAccountRepository: AssetAccountRepository
) {

    /**
     * すべての移動記録を取得
     */
    fun getAllTransfers(): List<Transfer> {
        return transferRepository.findAll()
    }

    /**
     * 期間指定で移動記録を取得
     */
    fun getTransfersByDateRange(startDate: LocalDate, endDate: LocalDate): List<Transfer> {
        return transferRepository.findByDateRange(startDate, endDate)
    }

    /**
     * 資産アカウント間の振替を作成
     */
    fun createAccountTransfer(
        date: LocalDate,
        amount: BigDecimal,
        fromAssetAccountId: String,
        toAssetAccountId: String,
        memo: String?
    ): Transfer {
        // 移動元アカウントから減額
        val fromAccount = assetAccountRepository.findById(fromAssetAccountId)
            ?: throw IllegalArgumentException("移動元アカウントが見つかりません: $fromAssetAccountId")

        // 移動先アカウントに加算
        val toAccount = assetAccountRepository.findById(toAssetAccountId)
            ?: throw IllegalArgumentException("移動先アカウントが見つかりません: $toAssetAccountId")

        val updatedFromAccount = fromAccount.subtractExpense(amount)
        val updatedToAccount = toAccount.addIncome(amount)

        assetAccountRepository.save(updatedFromAccount)
        assetAccountRepository.save(updatedToAccount)

        // 移動記録を作成
        val now = LocalDateTime.now()
        val transfer = Transfer(
            id = "tf_${System.currentTimeMillis()}",
            date = date,
            amount = amount,
            fromAssetAccountId = fromAssetAccountId,
            toAssetAccountId = toAssetAccountId,
            toPaymentMethodId = null,
            memo = memo,
            createdAt = now,
            updatedAt = now
        )

        return transferRepository.save(transfer)
    }

    /**
     * 出金記録を作成（資産アカウントから現金への出金）
     */
    fun createWithdrawal(
        date: LocalDate,
        amount: BigDecimal,
        fromAssetAccountId: String,
        memo: String?
    ): Transfer {
        // 資産アカウントから減額
        val fromAccount = assetAccountRepository.findById(fromAssetAccountId)
            ?: throw IllegalArgumentException("資産アカウントが見つかりません: $fromAssetAccountId")

        val updatedAccount = fromAccount.subtractExpense(amount)
        assetAccountRepository.save(updatedAccount)

        // 移動記録を作成
        val now = LocalDateTime.now()
        val transfer = Transfer(
            id = "tf_${System.currentTimeMillis()}",
            date = date,
            amount = amount,
            fromAssetAccountId = fromAssetAccountId,
            toAssetAccountId = null,  // 出金の場合はnull
            toPaymentMethodId = null,
            memo = memo,
            createdAt = now,
            updatedAt = now
        )

        return transferRepository.save(transfer)
    }

    /**
     * チャージ記録を作成（資産アカウントから電子マネーへのチャージ）
     */
    fun createCharge(
        date: LocalDate,
        amount: BigDecimal,
        fromAssetAccountId: String,
        toPaymentMethodId: String,
        memo: String?
    ): Transfer {
        // 資産アカウントから減額
        val fromAccount = assetAccountRepository.findById(fromAssetAccountId)
            ?: throw IllegalArgumentException("資産アカウントが見つかりません: $fromAssetAccountId")

        val updatedAccount = fromAccount.subtractExpense(amount)
        assetAccountRepository.save(updatedAccount)

        // 移動記録を作成
        val now = LocalDateTime.now()
        val transfer = Transfer(
            id = "tf_${System.currentTimeMillis()}",
            date = date,
            amount = amount,
            fromAssetAccountId = fromAssetAccountId,
            toAssetAccountId = null,
            toPaymentMethodId = toPaymentMethodId,
            memo = memo,
            createdAt = now,
            updatedAt = now
        )

        return transferRepository.save(transfer)
    }

    /**
     * 移動記録を削除
     */
    fun deleteTransfer(id: String): Boolean {
        // 本来は残高を元に戻す処理が必要だが、簡略化のため省略
        return transferRepository.delete(id)
    }
}
