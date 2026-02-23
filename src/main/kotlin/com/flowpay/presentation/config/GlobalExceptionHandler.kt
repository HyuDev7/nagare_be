package com.flowpay.presentation.config

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler

/**
 * グローバル例外ハンドラー
 */
@ControllerAdvice
class GlobalExceptionHandler {

    /**
     * エラーレスポンス
     */
    data class ErrorResponse(
        val message: String,
        val status: Int
    )

    /**
     * IllegalArgumentException（バリデーションエラー）
     */
    @ExceptionHandler(IllegalArgumentException::class)
    fun handleIllegalArgumentException(ex: IllegalArgumentException): ResponseEntity<ErrorResponse> {
        val errorResponse = ErrorResponse(
            message = ex.message ?: "Invalid request",
            status = HttpStatus.BAD_REQUEST.value()
        )
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse)
    }

    /**
     * IllegalStateException（状態エラー）
     */
    @ExceptionHandler(IllegalStateException::class)
    fun handleIllegalStateException(ex: IllegalStateException): ResponseEntity<ErrorResponse> {
        val errorResponse = ErrorResponse(
            message = ex.message ?: "Invalid state",
            status = HttpStatus.CONFLICT.value()
        )
        return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse)
    }

    /**
     * その他の例外（サーバーエラー）
     */
    @ExceptionHandler(Exception::class)
    fun handleGenericException(ex: Exception): ResponseEntity<ErrorResponse> {
        ex.printStackTrace() // ログ出力（本番環境ではロガーを使用）
        val errorResponse = ErrorResponse(
            message = "Internal server error",
            status = HttpStatus.INTERNAL_SERVER_ERROR.value()
        )
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse)
    }
}
