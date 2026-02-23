package com.flowpay.domain.repository

import java.math.BigDecimal
import java.time.LocalDate

/**
 * 設定リポジトリインターフェース
 */
interface SettingsRepository {
    /**
     * 設定値を取得
     */
    fun get(key: String): String?

    /**
     * 設定値を保存
     */
    fun set(key: String, value: String)

    /**
     * 月次予算を取得
     */
    fun getMonthlyBudget(): BigDecimal?

    /**
     * 月次予算を設定
     */
    fun setMonthlyBudget(budget: BigDecimal)

    /**
     * 定期取引の最終チェック日を取得
     */
    fun getLastRecurringTransactionCheckDate(): LocalDate?

    /**
     * 定期取引の最終チェック日を設定
     */
    fun setLastRecurringTransactionCheckDate(date: LocalDate)
}
