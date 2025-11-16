package com.paymentflow.presentation.controller

import com.paymentflow.application.usecase.CategoryUseCase
import com.paymentflow.application.usecase.PaymentMethodUseCase
import com.paymentflow.application.usecase.TransactionUseCase
import com.paymentflow.domain.model.TransactionType
import com.paymentflow.presentation.dto.request.CreateTransactionRequest
import com.paymentflow.presentation.dto.request.UpdateTransactionRequest
import com.paymentflow.presentation.dto.response.TransactionResponse
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import java.time.LocalDate
import java.time.format.DateTimeFormatter

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

    /**
     * 取引キャンセル（論理削除）
     */
    @PostMapping("/{id}/cancel")
    fun cancelTransaction(@PathVariable id: String): ResponseEntity<TransactionResponse> {
        val transaction = transactionUseCase.cancelTransaction(id)

        val paymentMethod = paymentMethodUseCase.getPaymentMethodById(transaction.paymentMethodId)!!
        val category = categoryUseCase.getCategoryById(transaction.categoryId)!!

        return ResponseEntity.ok(TransactionResponse.from(transaction, paymentMethod, category))
    }

    /**
     * 取引履歴CSVエクスポート
     */
    @GetMapping("/export")
    fun exportTransactions(
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) from: LocalDate?,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) to: LocalDate?,
        @RequestParam(required = false) categoryId: String?,
        @RequestParam(required = false) paymentMethodId: String?,
        @RequestParam(required = false) type: TransactionType?
    ): ResponseEntity<ByteArray> {
        // フィルター条件で取引を取得
        val transactions = transactionUseCase.getTransactionsByFilter(
            from = from,
            to = to,
            categoryId = categoryId,
            paymentMethodId = paymentMethodId,
            type = type
        )

        // CSV生成
        val csv = StringBuilder()
        csv.append("日付,種類,金額,支払い手段,カテゴリ,メモ,引き落とし日,ステータス\n")

        transactions.forEach { transaction ->
            val paymentMethod = paymentMethodUseCase.getPaymentMethodById(transaction.paymentMethodId)
            val category = categoryUseCase.getCategoryById(transaction.categoryId)

            if (paymentMethod != null && category != null) {
                val typeLabel = if (transaction.type == TransactionType.EXPENSE) "支出" else "収入"
                val statusLabel = if (transaction.isWithdrawn) "確定" else "未確定"
                val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

                csv.append("${transaction.date.format(dateFormatter)},")
                csv.append("$typeLabel,")
                csv.append("${transaction.amount},")
                csv.append("${paymentMethod.name},")
                csv.append("${category.name},")
                csv.append("\"${transaction.memo ?: ""}\",")
                csv.append("${transaction.withdrawalDate?.format(dateFormatter) ?: ""},")
                csv.append("$statusLabel\n")
            }
        }

        // レスポンスヘッダー設定
        val headers = HttpHeaders()
        headers.contentType = MediaType("text", "csv")
        headers.setContentDispositionFormData("attachment", "transactions.csv")

        return ResponseEntity.ok()
            .headers(headers)
            .body(csv.toString().toByteArray(Charsets.UTF_8))
    }

    /**
     * 取引履歴CSVインポート
     */
    @PostMapping("/import")
    fun importTransactions(
        @RequestParam("file") file: MultipartFile
    ): ResponseEntity<Map<String, Any>> {
        if (file.isEmpty) {
            return ResponseEntity.badRequest().body(mapOf("error" to "ファイルが空です"))
        }

        try {
            val content = String(file.bytes, Charsets.UTF_8)
            val lines = content.lines().drop(1)  // ヘッダー行をスキップ

            var successCount = 0
            var failureCount = 0
            val errors = mutableListOf<String>()

            for ((index, line) in lines.withIndex()) {
                if (line.isBlank()) continue

                try {
                    val parts = line.split(",").map { it.trim().removeSurrounding("\"") }
                    if (parts.size < 6) {
                        errors.add("行${index + 2}: フォーマットエラー")
                        failureCount++
                        continue
                    }

                    val date = LocalDate.parse(parts[0], DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                    val type = when (parts[1]) {
                        "支出" -> TransactionType.EXPENSE
                        "収入" -> TransactionType.INCOME
                        else -> {
                            errors.add("行${index + 2}: 不正な種類: ${parts[1]}")
                            failureCount++
                            continue
                        }
                    }
                    val amount = parts[2].toBigDecimal()
                    val paymentMethodName = parts[3]
                    val categoryName = parts[4]
                    val memo = parts.getOrNull(5)?.takeIf { it.isNotBlank() }

                    // 名前から支払い手段とカテゴリを検索
                    val paymentMethod = paymentMethodUseCase.getAllPaymentMethods()
                        .find { it.name == paymentMethodName }
                    val category = categoryUseCase.getAllCategories()
                        .find { it.name == categoryName && it.type.name == type.name }

                    if (paymentMethod == null) {
                        errors.add("行${index + 2}: 支払い手段が見つかりません: $paymentMethodName")
                        failureCount++
                        continue
                    }

                    if (category == null) {
                        errors.add("行${index + 2}: カテゴリが見つかりません: $categoryName")
                        failureCount++
                        continue
                    }

                    transactionUseCase.createTransaction(
                        date = date,
                        amount = amount,
                        type = type,
                        paymentMethodId = paymentMethod.id,
                        categoryId = category.id,
                        memo = memo
                    )

                    successCount++
                } catch (e: Exception) {
                    errors.add("行${index + 2}: ${e.message}")
                    failureCount++
                }
            }

            return ResponseEntity.ok(mapOf(
                "successCount" to successCount,
                "failureCount" to failureCount,
                "errors" to errors
            ))
        } catch (e: Exception) {
            return ResponseEntity.badRequest().body(mapOf("error" to "CSVパースエラー: ${e.message}"))
        }
    }
}
