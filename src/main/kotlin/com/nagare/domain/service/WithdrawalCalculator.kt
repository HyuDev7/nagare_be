package com.nagare.domain.service

import com.nagare.domain.model.PaymentMethod
import com.nagare.domain.model.PaymentMethodType
import org.springframework.stereotype.Service
import java.time.LocalDate
import java.time.YearMonth

/**
 * 引き落とし日計算サービス
 */
@Service
class WithdrawalCalculator {

    /**
     * 取引の引き落とし日を計算する
     *
     * @param transactionDate 利用日
     * @param paymentMethod 支払い手段
     * @return 引き落とし日
     */
    fun calculateWithdrawalDate(
        transactionDate: LocalDate,
        paymentMethod: PaymentMethod
    ): LocalDate {
        // クレジットカード以外は即時引き落とし
        if (paymentMethod.type != PaymentMethodType.CREDIT_CARD) {
            return transactionDate
        }

        // クレジットカードの場合は締め日・引き落とし日から計算
        val closingDay = paymentMethod.closingDay
            ?: throw IllegalArgumentException("クレジットカードの締め日が設定されていません")
        val withdrawalDay = paymentMethod.withdrawalDay
            ?: throw IllegalArgumentException("クレジットカードの引き落とし日が設定されていません")

        // 締め日を計算
        val closingDate = calculateClosingDate(transactionDate, closingDay)

        // 引き落とし日を計算（締め日の翌月）
        val withdrawalYearMonth = YearMonth.from(closingDate).plusMonths(1)
        return calculateDayInMonth(withdrawalYearMonth, withdrawalDay)
    }

    /**
     * 利用日に対する締め日を計算する
     *
     * @param transactionDate 利用日
     * @param closingDay 締め日（1-31）
     * @return 締め日
     */
    private fun calculateClosingDate(transactionDate: LocalDate, closingDay: Int): LocalDate {
        val transactionYearMonth = YearMonth.from(transactionDate)
        val closingDateInSameMonth = calculateDayInMonth(transactionYearMonth, closingDay)

        // 利用日が締め日以前なら同月の締め日、締め日より後なら翌月の締め日
        return if (transactionDate <= closingDateInSameMonth) {
            closingDateInSameMonth
        } else {
            val nextYearMonth = transactionYearMonth.plusMonths(1)
            calculateDayInMonth(nextYearMonth, closingDay)
        }
    }

    /**
     * 指定された年月と日から実際の日付を計算する
     * 月末を超える場合は月末日にする（例: 2月31日 → 2月28日/29日）
     *
     * @param yearMonth 年月
     * @param day 日（1-31）
     * @return 実際の日付
     */
    private fun calculateDayInMonth(yearMonth: YearMonth, day: Int): LocalDate {
        val lastDayOfMonth = yearMonth.lengthOfMonth()
        val actualDay = minOf(day, lastDayOfMonth)
        return yearMonth.atDay(actualDay)
    }
}
