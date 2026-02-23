package com.flowpay.application.usecase

import com.flowpay.domain.model.Notification
import com.flowpay.domain.model.NotificationType
import com.flowpay.domain.repository.TransactionRepository
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime

/**
 * 通知ユースケース
 */
@Service
class NotificationUseCase(
    private val transactionRepository: TransactionRepository
) {

    /**
     * 今日の通知を取得
     */
    fun getTodayNotifications(): List<Notification> {
        val notifications = mutableListOf<Notification>()
        val today = LocalDate.now()
        val tomorrow = today.plusDays(1)

        // 明日引き落とし予定の取引を取得
        val upcomingTransactions = transactionRepository.findAll()
            .filter { !it.isWithdrawn && !it.isDeleted }
            .filter { 
                it.withdrawalDate != null && 
                (it.withdrawalDate == today || it.withdrawalDate == tomorrow)
            }

        if (upcomingTransactions.isNotEmpty()) {
            val totalAmount = upcomingTransactions
                .sumOf { it.amount }

            val dateText = if (upcomingTransactions.first().withdrawalDate == today) "今日" else "明日"
            
            notifications.add(
                Notification(
                    id = "notif_${System.currentTimeMillis()}",
                    type = NotificationType.WITHDRAWAL_REMINDER,
                    title = "${dateText}の引き落とし予定",
                    message = "${upcomingTransactions.size}件の取引、合計${totalAmount}円の引き落とし予定があります",
                    date = upcomingTransactions.first().withdrawalDate!!,
                    amount = totalAmount,
                    isRead = false,
                    createdAt = LocalDateTime.now()
                )
            )
        }

        return notifications
    }

    /**
     * 今週の引き落とし予定を取得
     */
    fun getWeeklyWithdrawalReminders(): List<Notification> {
        val notifications = mutableListOf<Notification>()
        val today = LocalDate.now()
        val weekEnd = today.plusDays(7)

        val upcomingTransactions = transactionRepository.findAll()
            .filter { !it.isWithdrawn && !it.isDeleted }
            .filter { 
                it.withdrawalDate != null && 
                it.withdrawalDate!! in today..weekEnd
            }
            .groupBy { it.withdrawalDate }

        for ((date, transactions) in upcomingTransactions) {
            val totalAmount = transactions.sumOf { it.amount }
            
            notifications.add(
                Notification(
                    id = "notif_${date}_${System.currentTimeMillis()}",
                    type = NotificationType.WITHDRAWAL_REMINDER,
                    title = "${date}の引き落とし予定",
                    message = "${transactions.size}件の取引、合計${totalAmount}円",
                    date = date!!,
                    amount = totalAmount,
                    isRead = false,
                    createdAt = LocalDateTime.now()
                )
            )
        }

        return notifications.sortedBy { it.date }
    }
}
