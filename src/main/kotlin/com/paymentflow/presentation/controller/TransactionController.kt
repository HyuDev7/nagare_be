package com.paymentflow.presentation.controller

import com.paymentflow.application.usecase.CategoryUseCase
import com.paymentflow.application.usecase.PaymentMethodUseCase
import com.paymentflow.application.usecase.TransactionUseCase
import com.paymentflow.domain.model.TransactionType
import com.paymentflow.presentation.dto.request.CreateTransactionRequest
import com.paymentflow.presentation.dto.request.UpdateTransactionRequest
import com.paymentflow.presentation.dto.response.TransactionResponse
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.time.LocalDate

/**
 * 取引コントローラー
 */
@RestController
@RequestMapping("/api/transactions")
class TransactionController(
    private val transactionUseCase: TransactionUseCase,
    private val paymentMethodUseCase: PaymentMethodUseCase,
    private val categoryUseCase: CategoryUseCase
) {
    /**
     * 取引一覧取得（クエリパラメータでフィルタ）
     */
    @GetMapping
    fun getTransactions(
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) from: LocalDate?,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) to: LocalDate?,
        @RequestParam(required = false) categoryId: String?,
        @RequestParam(required = false) paymentMethodId: String?,
        @RequestParam(required = false) type: TransactionType?
    ): ResponseEntity<List<TransactionResponse>> {
        val transactions = transactionUseCase.getTransactionsByFilter(
            from = from,
            to = to,
            categoryId = categoryId,
            paymentMethodId = paymentMethodId,
            type = type
        )

        val response = transactions.mapNotNull { transaction ->
            val paymentMethod = paymentMethodUseCase.getPaymentMethodById(transaction.paymentMethodId)
            val category = categoryUseCase.getCategoryById(transaction.categoryId)

            if (paymentMethod != null && category != null) {
                TransactionResponse.from(transaction, paymentMethod, category)
            } else {
                null
            }
        }

        return ResponseEntity.ok(response)
    }

    /**
     * 取引取得
     */
    @GetMapping("/{id}")
    fun getTransaction(@PathVariable id: String): ResponseEntity<TransactionResponse> {
        val transaction = transactionUseCase.getTransactionById(id)
            ?: return ResponseEntity.notFound().build()

        val paymentMethod = paymentMethodUseCase.getPaymentMethodById(transaction.paymentMethodId)
            ?: return ResponseEntity.notFound().build()

        val category = categoryUseCase.getCategoryById(transaction.categoryId)
            ?: return ResponseEntity.notFound().build()

        return ResponseEntity.ok(TransactionResponse.from(transaction, paymentMethod, category))
    }

    /**
     * 取引登録
     */
    @PostMapping
    fun createTransaction(
        @RequestBody request: CreateTransactionRequest
    ): ResponseEntity<TransactionResponse> {
        val transaction = transactionUseCase.createTransaction(
            date = request.date,
            amount = request.amount,
            type = request.type,
            paymentMethodId = request.paymentMethodId,
            categoryId = request.categoryId,
            memo = request.memo
        )

        val paymentMethod = paymentMethodUseCase.getPaymentMethodById(transaction.paymentMethodId)!!
        val category = categoryUseCase.getCategoryById(transaction.categoryId)!!

        return ResponseEntity.status(HttpStatus.CREATED)
            .body(TransactionResponse.from(transaction, paymentMethod, category))
    }

    /**
     * 取引更新
     */
    @PutMapping("/{id}")
    fun updateTransaction(
        @PathVariable id: String,
        @RequestBody request: UpdateTransactionRequest
    ): ResponseEntity<TransactionResponse> {
        val transaction = transactionUseCase.updateTransaction(
            id = id,
            date = request.date,
            amount = request.amount,
            type = request.type,
            paymentMethodId = request.paymentMethodId,
            categoryId = request.categoryId,
            memo = request.memo
        )

        val paymentMethod = paymentMethodUseCase.getPaymentMethodById(transaction.paymentMethodId)!!
        val category = categoryUseCase.getCategoryById(transaction.categoryId)!!

        return ResponseEntity.ok(TransactionResponse.from(transaction, paymentMethod, category))
    }

    /**
     * 取引削除
     */
    @DeleteMapping("/{id}")
    fun deleteTransaction(@PathVariable id: String): ResponseEntity<Void> {
        val deleted = transactionUseCase.deleteTransaction(id)
        return if (deleted) {
            ResponseEntity.noContent().build()
        } else {
            ResponseEntity.notFound().build()
        }
    }
}
