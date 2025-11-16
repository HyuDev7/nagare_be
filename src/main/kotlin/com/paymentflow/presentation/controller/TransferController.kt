package com.paymentflow.presentation.controller

import com.paymentflow.application.usecase.TransferUseCase
import com.paymentflow.presentation.dto.request.CreateAccountTransferRequest
import com.paymentflow.presentation.dto.request.CreateChargeRequest
import com.paymentflow.presentation.dto.request.CreateWithdrawalRequest
import com.paymentflow.presentation.dto.response.TransferResponse
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.time.LocalDate

/**
 * 移動記録コントローラー
 */
@RestController
@RequestMapping("/api/transfers")
class TransferController(
    private val transferUseCase: TransferUseCase
) {

    /**
     * すべての移動記録を取得
     */
    @GetMapping
    fun getAllTransfers(
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) startDate: LocalDate?,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) endDate: LocalDate?
    ): ResponseEntity<List<TransferResponse>> {
        val transfers = if (startDate != null && endDate != null) {
            transferUseCase.getTransfersByDateRange(startDate, endDate)
        } else {
            transferUseCase.getAllTransfers()
        }

        return ResponseEntity.ok(transfers.map { TransferResponse.from(it) })
    }

    /**
     * 資産アカウント間の振替を作成
     */
    @PostMapping("/account-transfer")
    fun createAccountTransfer(
        @RequestBody request: CreateAccountTransferRequest
    ): ResponseEntity<TransferResponse> {
        val transfer = transferUseCase.createAccountTransfer(
            date = request.date,
            amount = request.amount,
            fromAssetAccountId = request.fromAssetAccountId,
            toAssetAccountId = request.toAssetAccountId,
            memo = request.memo
        )
        return ResponseEntity.ok(TransferResponse.from(transfer))
    }

    /**
     * 出金記録を作成
     */
    @PostMapping("/withdrawal")
    fun createWithdrawal(
        @RequestBody request: CreateWithdrawalRequest
    ): ResponseEntity<TransferResponse> {
        val transfer = transferUseCase.createWithdrawal(
            date = request.date,
            amount = request.amount,
            fromAssetAccountId = request.fromAssetAccountId,
            memo = request.memo
        )
        return ResponseEntity.ok(TransferResponse.from(transfer))
    }

    /**
     * チャージ記録を作成
     */
    @PostMapping("/charge")
    fun createCharge(
        @RequestBody request: CreateChargeRequest
    ): ResponseEntity<TransferResponse> {
        val transfer = transferUseCase.createCharge(
            date = request.date,
            amount = request.amount,
            fromAssetAccountId = request.fromAssetAccountId,
            toPaymentMethodId = request.toPaymentMethodId,
            memo = request.memo
        )
        return ResponseEntity.ok(TransferResponse.from(transfer))
    }

    /**
     * 移動記録を削除
     */
    @DeleteMapping("/{id}")
    fun deleteTransfer(@PathVariable id: String): ResponseEntity<Void> {
        transferUseCase.deleteTransfer(id)
        return ResponseEntity.noContent().build()
    }
}
