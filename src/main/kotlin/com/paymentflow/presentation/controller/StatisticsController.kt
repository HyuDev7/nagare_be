package com.paymentflow.presentation.controller

import com.paymentflow.application.usecase.StatisticsUseCase
import com.paymentflow.presentation.dto.response.MonthlyTrendResponse
import com.paymentflow.presentation.dto.response.CategoryTrendResponse
import com.paymentflow.presentation.dto.response.PaymentMethodStatisticsResponse
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

/**
 * 統計コントローラー
 */
@RestController
@RequestMapping("/api/statistics")
class StatisticsController(
    private val statisticsUseCase: StatisticsUseCase
) {
    /**
     * 月次推移データ取得
     */
    @GetMapping("/monthly-trend")
    fun getMonthlyTrend(
        @RequestParam(required = false, defaultValue = "6") months: Int
    ): ResponseEntity<MonthlyTrendResponse> {
        val data = statisticsUseCase.getMonthlyTrend(months)
        return ResponseEntity.ok(data)
    }

    /**
     * カテゴリ別トレンドデータ取得
     */
    @GetMapping("/category-trend")
    fun getCategoryTrend(
        @RequestParam(required = false, defaultValue = "3") months: Int
    ): ResponseEntity<CategoryTrendResponse> {
        val data = statisticsUseCase.getCategoryTrend(months)
        return ResponseEntity.ok(data)
    }

    /**
     * 支払い手段別統計データ取得
     */
    @GetMapping("/payment-method-statistics")
    fun getPaymentMethodStatistics(
        @RequestParam(required = false, defaultValue = "3") months: Int
    ): ResponseEntity<PaymentMethodStatisticsResponse> {
        val data = statisticsUseCase.getPaymentMethodStatistics(months)
        return ResponseEntity.ok(data)
    }
}
