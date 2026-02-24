package com.nagare.domain.model

import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime

/**
 * 通知タイプ
 */
enum class NotificationType {
    WITHDRAWAL_REMINDER,  // 引き落とし予定のリマインダー
    BUDGET_WARNING,       // 予算超過警告
    RECURRING_REMINDER    // 繰り返し取引のリマインダー
}

/**
 * 通知
 */
data class Notification(
    val id: String,
    val type: NotificationType,
    val title: String,
    val message: String,
    val date: LocalDate,
    val amount: BigDecimal?,
    val isRead: Boolean,
    val createdAt: LocalDateTime
)
