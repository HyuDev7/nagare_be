package com.flowpay.presentation.controller

import com.flowpay.application.usecase.PaymentMethodUseCase
import com.flowpay.presentation.dto.request.CreatePaymentMethodRequest
import com.flowpay.presentation.dto.request.UpdatePaymentMethodRequest
import com.flowpay.presentation.dto.response.PaymentMethodResponse
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

/**
 * 支払い手段コントローラー
 */
@RestController
@RequestMapping("/api/payment-methods")
class PaymentMethodController(
    private val paymentMethodUseCase: PaymentMethodUseCase
) {
    /**
     * 支払い手段一覧取得
     */
    @GetMapping
    fun getAllPaymentMethods(): ResponseEntity<List<PaymentMethodResponse>> {
        val paymentMethods = paymentMethodUseCase.getAllPaymentMethods()
        val response = paymentMethods.map { PaymentMethodResponse.from(it) }
        return ResponseEntity.ok(response)
    }

    /**
     * 支払い手段取得
     */
    @GetMapping("/{id}")
    fun getPaymentMethod(@PathVariable id: String): ResponseEntity<PaymentMethodResponse> {
        val paymentMethod = paymentMethodUseCase.getPaymentMethodById(id)
            ?: return ResponseEntity.notFound().build()

        return ResponseEntity.ok(PaymentMethodResponse.from(paymentMethod))
    }

    /**
     * 支払い手段登録
     */
    @PostMapping
    fun createPaymentMethod(
        @RequestBody request: CreatePaymentMethodRequest
    ): ResponseEntity<PaymentMethodResponse> {
        val paymentMethod = paymentMethodUseCase.createPaymentMethod(
            name = request.name,
            type = request.type,
            assetAccountId = request.assetAccountId,
            closingDay = request.closingDay,
            withdrawalDay = request.withdrawalDay,
            memo = request.memo
        )

        return ResponseEntity.status(HttpStatus.CREATED)
            .body(PaymentMethodResponse.from(paymentMethod))
    }

    /**
     * 支払い手段更新
     */
    @PutMapping("/{id}")
    fun updatePaymentMethod(
        @PathVariable id: String,
        @RequestBody request: UpdatePaymentMethodRequest
    ): ResponseEntity<PaymentMethodResponse> {
        val paymentMethod = paymentMethodUseCase.updatePaymentMethod(
            id = id,
            name = request.name,
            type = request.type,
            assetAccountId = request.assetAccountId,
            closingDay = request.closingDay,
            withdrawalDay = request.withdrawalDay,
            memo = request.memo
        )

        return ResponseEntity.ok(PaymentMethodResponse.from(paymentMethod))
    }

    /**
     * 支払い手段削除
     */
    @DeleteMapping("/{id}")
    fun deletePaymentMethod(@PathVariable id: String): ResponseEntity<Void> {
        val deleted = paymentMethodUseCase.deletePaymentMethod(id)
        return if (deleted) {
            ResponseEntity.noContent().build()
        } else {
            ResponseEntity.notFound().build()
        }
    }
}
