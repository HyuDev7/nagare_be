package com.flowpay.application.usecase

import com.flowpay.domain.repository.SettingsRepository
import org.springframework.stereotype.Service
import java.math.BigDecimal

/**
 * 設定ユースケース
 */
@Service
class SettingsUseCase(
    private val settingsRepository: SettingsRepository
) {
    /**
     * 設定値を取得
     */
    fun getSetting(key: String): String? {
        return settingsRepository.get(key)
    }

    /**
     * 設定値を保存
     */
    fun setSetting(key: String, value: String) {
        settingsRepository.set(key, value)
    }

    /**
     * 月次予算を取得
     */
    fun getMonthlyBudget(): BigDecimal? {
        return settingsRepository.getMonthlyBudget()
    }

    /**
     * 月次予算を設定
     */
    fun setMonthlyBudget(budget: BigDecimal) {
        require(budget >= BigDecimal.ZERO) {
            "予算は0以上である必要があります"
        }
        settingsRepository.setMonthlyBudget(budget)
    }
}
