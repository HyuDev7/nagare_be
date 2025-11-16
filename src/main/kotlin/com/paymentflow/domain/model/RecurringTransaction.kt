package com.paymentflow.domain.model

import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime

/**
 * 繰り返し取引の頻度
 */
enum class RecurringFrequency {
    DAILY,      // 毎日
    WEEKLY,     // 毎週
    MONTHLY,    // 毎月
    YEARLY      // 毎年
}

/**
 * 繰り返し取引テンプレート
 */
data class RecurringTransaction(
    val id: String,
    val name: String,  // テンプレート名（例: Netflix月額）
    val amount: BigDecimal,
    val type: TransactionType,
    val paymentMethodId: String,
    val categoryId: String,
    val frequency: RecurringFrequency,  // 頻度
    val startDate: LocalDate,  // 開始日
    val endDate: LocalDate?,  // 終了日（nullの場合は無期限）
    val dayOfMonth: Int?,  // 毎月の実行日（1-31、MONTHLYの場合のみ）
    val dayOfWeek: Int?,  // 毎週の曜日（1-7、WEEKLYの場合のみ）
    val memo: String?,
    val isActive: Boolean,  // アクティブフラグ
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
)
