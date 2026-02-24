package com.nagare.presentation.dto.response

import com.nagare.domain.model.Notification
import com.nagare.domain.model.NotificationType
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime

data class NotificationResponse(
    val id: String,
    val type: NotificationType,
    val title: String,
    val message: String,
    val date: LocalDate,
    val amount: BigDecimal?,
    val isRead: Boolean,
    val createdAt: LocalDateTime
) {
    companion object {
        fun from(notification: Notification): NotificationResponse {
            return NotificationResponse(
                id = notification.id,
                type = notification.type,
                title = notification.title,
                message = notification.message,
                date = notification.date,
                amount = notification.amount,
                isRead = notification.isRead,
                createdAt = notification.createdAt
            )
        }
    }
}
