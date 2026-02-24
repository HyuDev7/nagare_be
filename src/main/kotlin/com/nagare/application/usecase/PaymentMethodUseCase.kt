package com.nagare.application.usecase

import com.nagare.domain.model.PaymentMethod
import com.nagare.domain.model.PaymentMethodType
import com.nagare.domain.repository.PaymentMethodRepository
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.util.*

/**
 * 支払い手段ユースケース
 */
@Service
class PaymentMethodUseCase(
    private val paymentMethodRepository: PaymentMethodRepository
) {
    /**
     * すべての支払い手段を取得
     */
    fun getAllPaymentMethods(): List<PaymentMethod> {
        return paymentMethodRepository.findAll()
    }

    /**
     * IDで支払い手段を取得
     */
    fun getPaymentMethodById(id: String): PaymentMethod? {
        return paymentMethodRepository.findById(id)
    }

    /**
     * 支払い手段を作成
     */
    fun createPaymentMethod(
        name: String,
        type: PaymentMethodType,
        assetAccountId: String,
        closingDay: Int? = null,
        withdrawalDay: Int? = null,
        memo: String? = null
    ): PaymentMethod {
        val now = LocalDateTime.now()
        val paymentMethod = PaymentMethod(
            id = "pm_${UUID.randomUUID()}",
            name = name,
            type = type,
            assetAccountId = assetAccountId,
            closingDay = closingDay,
            withdrawalDay = withdrawalDay,
            memo = memo,
            createdAt = now,
            updatedAt = now
        )

        return paymentMethodRepository.save(paymentMethod)
    }

    /**
     * 支払い手段を更新
     */
    fun updatePaymentMethod(
        id: String,
        name: String,
        type: PaymentMethodType,
        assetAccountId: String,
        closingDay: Int? = null,
        withdrawalDay: Int? = null,
        memo: String? = null
    ): PaymentMethod {
        val existing = paymentMethodRepository.findById(id)
            ?: throw IllegalArgumentException("支払い手段が見つかりません: $id")

        val updated = PaymentMethod(
            id = existing.id,
            name = name,
            type = type,
            assetAccountId = assetAccountId,
            closingDay = closingDay,
            withdrawalDay = withdrawalDay,
            memo = memo,
            createdAt = existing.createdAt,
            updatedAt = LocalDateTime.now()
        )

        return paymentMethodRepository.save(updated)
    }

    /**
     * 支払い手段を削除
     */
    fun deletePaymentMethod(id: String): Boolean {
        return paymentMethodRepository.deleteById(id)
    }
}
