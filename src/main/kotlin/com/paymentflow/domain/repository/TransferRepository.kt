package com.paymentflow.domain.repository

import com.paymentflow.domain.model.Transfer
import java.time.LocalDate

/**
 * 移動記録リポジトリインターフェース
 */
interface TransferRepository {
    /**
     * すべての移動記録を取得
     */
    fun findAll(): List<Transfer>

    /**
     * IDで移動記録を取得
     */
    fun findById(id: String): Transfer?

    /**
     * 期間指定で移動記録を取得
     */
    fun findByDateRange(startDate: LocalDate, endDate: LocalDate): List<Transfer>

    /**
     * 移動記録を保存
     */
    fun save(transfer: Transfer): Transfer

    /**
     * 移動記録を削除
     */
    fun delete(id: String): Boolean
}
