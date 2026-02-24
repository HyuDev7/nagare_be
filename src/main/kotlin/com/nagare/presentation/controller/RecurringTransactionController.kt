package com.nagare.presentation.controller

import com.nagare.application.usecase.RecurringTransactionUseCase
import com.nagare.presentation.dto.request.CreateRecurringTransactionRequest
import com.nagare.presentation.dto.response.RecurringTransactionResponse
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/recurring-transactions")
class RecurringTransactionController(
    private val recurringTransactionUseCase: RecurringTransactionUseCase
) {

    @GetMapping
    fun getAllRecurringTransactions(
        @RequestParam(required = false, defaultValue = "false") activeOnly: Boolean
    ): ResponseEntity<List<RecurringTransactionResponse>> {
        val recurring = if (activeOnly) {
            recurringTransactionUseCase.getActiveRecurringTransactions()
        } else {
            recurringTransactionUseCase.getAllRecurringTransactions()
        }
        return ResponseEntity.ok(recurring.map { RecurringTransactionResponse.from(it) })
    }

    @PostMapping
    fun createRecurringTransaction(
        @RequestBody request: CreateRecurringTransactionRequest
    ): ResponseEntity<RecurringTransactionResponse> {
        val recurring = recurringTransactionUseCase.createRecurringTransaction(
            name = request.name,
            amount = request.amount,
            type = request.type,
            paymentMethodId = request.paymentMethodId,
            categoryId = request.categoryId,
            frequency = request.frequency,
            startDate = request.startDate,
            endDate = request.endDate,
            dayOfMonth = request.dayOfMonth,
            dayOfWeek = request.dayOfWeek,
            memo = request.memo
        )
        return ResponseEntity.ok(RecurringTransactionResponse.from(recurring))
    }

    @PutMapping("/{id}")
    fun updateRecurringTransaction(
        @PathVariable id: String,
        @RequestBody request: CreateRecurringTransactionRequest
    ): ResponseEntity<RecurringTransactionResponse> {
        val recurring = recurringTransactionUseCase.updateRecurringTransactionFull(
            id = id,
            name = request.name,
            amount = request.amount,
            type = request.type,
            paymentMethodId = request.paymentMethodId,
            categoryId = request.categoryId,
            frequency = request.frequency,
            startDate = request.startDate,
            endDate = request.endDate,
            dayOfMonth = request.dayOfMonth,
            dayOfWeek = request.dayOfWeek,
            memo = request.memo
        )
        return ResponseEntity.ok(RecurringTransactionResponse.from(recurring))
    }

    @PutMapping("/{id}/activate")
    fun activateRecurringTransaction(@PathVariable id: String): ResponseEntity<RecurringTransactionResponse> {
        val recurring = recurringTransactionUseCase.updateRecurringTransaction(id, null, null, true)
        return ResponseEntity.ok(RecurringTransactionResponse.from(recurring))
    }

    @PutMapping("/{id}/deactivate")
    fun deactivateRecurringTransaction(@PathVariable id: String): ResponseEntity<RecurringTransactionResponse> {
        val recurring = recurringTransactionUseCase.updateRecurringTransaction(id, null, null, false)
        return ResponseEntity.ok(RecurringTransactionResponse.from(recurring))
    }

    @DeleteMapping("/{id}")
    fun deleteRecurringTransaction(@PathVariable id: String): ResponseEntity<Void> {
        recurringTransactionUseCase.deleteRecurringTransaction(id)
        return ResponseEntity.noContent().build()
    }

    @PostMapping("/execute")
    fun executeRecurringTransactions(): ResponseEntity<Void> {
        recurringTransactionUseCase.executeRecurringTransactionsForToday()
        return ResponseEntity.ok().build()
    }

    @PostMapping("/check")
    fun checkPendingRecurringTransactions(): ResponseEntity<Void> {
        recurringTransactionUseCase.checkAndExecutePendingRecurringTransactions()
        return ResponseEntity.ok().build()
    }
}
