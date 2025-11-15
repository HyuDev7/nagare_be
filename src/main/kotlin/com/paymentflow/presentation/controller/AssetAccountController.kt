package com.paymentflow.presentation.controller

import com.paymentflow.application.usecase.AssetAccountUseCase
import com.paymentflow.presentation.dto.request.UpdateAssetAccountRequest
import com.paymentflow.presentation.dto.response.AssetAccountResponse
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

/**
 * 資産アカウントコントローラー
 */
@RestController
@RequestMapping("/api/asset-account")
class AssetAccountController(
    private val assetAccountUseCase: AssetAccountUseCase
) {
    /**
     * 資産アカウント情報取得
     */
    @GetMapping
    fun getAssetAccount(): ResponseEntity<AssetAccountResponse> {
        val assetAccount = assetAccountUseCase.getAssetAccount()
            ?: return ResponseEntity.notFound().build()

        return ResponseEntity.ok(AssetAccountResponse.from(assetAccount))
    }

    /**
     * 資産アカウント情報更新（残高調整）
     */
    @PutMapping
    fun updateAssetAccount(
        @RequestBody request: UpdateAssetAccountRequest
    ): ResponseEntity<AssetAccountResponse> {
        val updatedAccount = assetAccountUseCase.updateBalance(request.balance)
        return ResponseEntity.ok(AssetAccountResponse.from(updatedAccount))
    }
}
