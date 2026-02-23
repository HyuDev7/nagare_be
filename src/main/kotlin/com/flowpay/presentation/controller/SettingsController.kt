package com.flowpay.presentation.controller

import com.flowpay.application.usecase.SettingsUseCase
import com.flowpay.presentation.dto.request.UpdateSettingsRequest
import com.flowpay.presentation.dto.response.SettingsResponse
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

/**
 * 設定コントローラー
 */
@RestController
@RequestMapping("/api/settings")
class SettingsController(
    private val settingsUseCase: SettingsUseCase
) {
    /**
     * 設定情報取得
     */
    @GetMapping
    fun getSettings(): ResponseEntity<SettingsResponse> {
        val monthlyBudget = settingsUseCase.getMonthlyBudget()
        return ResponseEntity.ok(SettingsResponse(monthlyBudget = monthlyBudget))
    }

    /**
     * 設定情報更新
     */
    @PutMapping
    fun updateSettings(
        @RequestBody request: UpdateSettingsRequest
    ): ResponseEntity<SettingsResponse> {
        settingsUseCase.setMonthlyBudget(request.monthlyBudget)
        return ResponseEntity.ok(SettingsResponse(monthlyBudget = request.monthlyBudget))
    }
}
