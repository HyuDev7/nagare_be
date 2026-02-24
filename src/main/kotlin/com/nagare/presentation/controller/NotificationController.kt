package com.nagare.presentation.controller

import com.nagare.application.usecase.NotificationUseCase
import com.nagare.presentation.dto.response.NotificationResponse
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

/**
 * 通知コントローラー
 */
@RestController
@RequestMapping("/api/notifications")
class NotificationController(
    private val notificationUseCase: NotificationUseCase
) {

    /**
     * 今日の通知を取得
     */
    @GetMapping("/today")
    fun getTodayNotifications(): ResponseEntity<List<NotificationResponse>> {
        val notifications = notificationUseCase.getTodayNotifications()
        return ResponseEntity.ok(notifications.map { NotificationResponse.from(it) })
    }

    /**
     * 今週の引き落とし予定を取得
     */
    @GetMapping("/weekly")
    fun getWeeklyNotifications(): ResponseEntity<List<NotificationResponse>> {
        val notifications = notificationUseCase.getWeeklyWithdrawalReminders()
        return ResponseEntity.ok(notifications.map { NotificationResponse.from(it) })
    }
}
