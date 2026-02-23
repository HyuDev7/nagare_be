package com.flowpay.presentation.controller

import com.flowpay.application.usecase.DashboardUseCase
import com.flowpay.presentation.dto.response.DashboardResponse
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
    fun getDashboard(
        @RequestParam(required = false) assetAccountId: String?
    ): ResponseEntity<DashboardResponse> {
        val dashboardData = dashboardUseCase.getDashboardData(assetAccountId)
        return ResponseEntity.ok(DashboardResponse.from(dashboardData))
    }
}
