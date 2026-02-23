package com.flowpay.domain.repository

import com.flowpay.domain.model.PaymentMethod

/**
 * 支払い手段リポジトリインターフェース
 */
interface PaymentMethodRepository {
    /**
     * すべての支払い手段を取得
     */
    fun findAll(): List<PaymentMethod>

    /**
     * IDで支払い手段を取得
     */
    fun findById(id: String): PaymentMethod?

    /**
     * 支払い手段を保存
     */
    fun save(paymentMethod: PaymentMethod): PaymentMethod

    /**
     * 支払い手段を削除
     */
    fun deleteById(id: String): Boolean
}
