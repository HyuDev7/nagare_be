package com.flowpay.domain.model

/**
 * 支払い手段の種類
 */
enum class PaymentMethodType {
    /**
     * クレジットカード（締め日・引き落とし日あり）
     */
    CREDIT_CARD,

    /**
     * デビットカード（即時引き落とし）
     */
    DEBIT_CARD,

    /**
     * 電子マネー/ICカード
     */
    E_MONEY,

    /**
     * 現金
     */
    CASH,

    /**
     * 銀行振込
     */
    BANK_TRANSFER
}
