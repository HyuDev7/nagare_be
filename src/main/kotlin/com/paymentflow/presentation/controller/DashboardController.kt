package com.paymentflow.presentation.controller

import com.paymentflow.application.usecase.DashboardUseCase
import com.paymentflow.presentation.dto.response.DashboardResponse
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

/**
 * ダッシュボードコントローラー
 */
@RestController
@RequestMapping("/api/dashboard")
class DashboardController(
    private val dashboardUseCase: DashboardUseCase
) {
    /**
     * ダッシュボードデータ取得
     */
    @GetMapping
    fun getDashboard(): ResponseEntity<DashboardResponse> {
        val dashboardData = dashboardUseCase.getDashboardData()
        return ResponseEntity.ok(DashboardResponse.from(dashboardData))
    }
}
