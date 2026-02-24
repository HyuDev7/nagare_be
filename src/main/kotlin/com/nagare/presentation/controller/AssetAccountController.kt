package com.nagare.presentation.controller

import com.nagare.application.usecase.AssetAccountUseCase
import com.nagare.presentation.dto.request.CreateAssetAccountRequest
import com.nagare.presentation.dto.request.UpdateAssetAccountNameRequest
import com.nagare.presentation.dto.request.UpdateAssetAccountRequest
import com.nagare.presentation.dto.response.AssetAccountResponse
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
     * 資産アカウント情報取得（Phase 1互換）
     */
    @GetMapping
    fun getAssetAccount(): ResponseEntity<AssetAccountResponse> {
        val assetAccount = assetAccountUseCase.getAssetAccount()
            ?: return ResponseEntity.notFound().build()

        return ResponseEntity.ok(AssetAccountResponse.from(assetAccount))
    }

    /**
     * 資産アカウント情報更新（残高調整、Phase 1互換）
     */
    @PutMapping
    fun updateAssetAccount(
        @RequestBody request: UpdateAssetAccountRequest
    ): ResponseEntity<AssetAccountResponse> {
        val updatedAccount = assetAccountUseCase.updateBalance(request.balance)
        return ResponseEntity.ok(AssetAccountResponse.from(updatedAccount))
    }
}

/**
 * 複数資産アカウント管理コントローラー
 */
@RestController
@RequestMapping("/api/asset-accounts")
class AssetAccountsController(
    private val assetAccountUseCase: AssetAccountUseCase
) {
    /**
     * すべての資産アカウントを取得
     */
    @GetMapping
    fun getAllAssetAccounts(): ResponseEntity<List<AssetAccountResponse>> {
        val accounts = assetAccountUseCase.getAllAssetAccounts()
        return ResponseEntity.ok(accounts.map { AssetAccountResponse.from(it) })
    }

    /**
     * 資産アカウントを作成
     */
    @PostMapping
    fun createAssetAccount(
        @RequestBody request: CreateAssetAccountRequest
    ): ResponseEntity<AssetAccountResponse> {
        val account = assetAccountUseCase.createAssetAccount(
            name = request.name,
            initialBalance = request.initialBalance
        )
        return ResponseEntity.ok(AssetAccountResponse.from(account))
    }

    /**
     * 特定の資産アカウントを取得
     */
    @GetMapping("/{id}")
    fun getAssetAccountById(@PathVariable id: String): ResponseEntity<AssetAccountResponse> {
        val account = assetAccountUseCase.getAssetAccountById(id)
        return ResponseEntity.ok(AssetAccountResponse.from(account))
    }

    /**
     * 資産アカウント名を更新
     */
    @PutMapping("/{id}/name")
    fun updateAssetAccountName(
        @PathVariable id: String,
        @RequestBody request: UpdateAssetAccountNameRequest
    ): ResponseEntity<AssetAccountResponse> {
        val account = assetAccountUseCase.updateAssetAccountName(id, request.name)
        return ResponseEntity.ok(AssetAccountResponse.from(account))
    }

    /**
     * 資産アカウントの残高を調整
     */
    @PutMapping("/{id}/balance")
    fun updateBalance(
        @PathVariable id: String,
        @RequestBody request: UpdateAssetAccountRequest
    ): ResponseEntity<AssetAccountResponse> {
        val account = assetAccountUseCase.updateBalanceById(id, request.balance)
        return ResponseEntity.ok(AssetAccountResponse.from(account))
    }

    /**
     * 資産アカウントを削除
     */
    @DeleteMapping("/{id}")
    fun deleteAssetAccount(@PathVariable id: String): ResponseEntity<Void> {
        assetAccountUseCase.deleteAssetAccount(id)
        return ResponseEntity.noContent().build()
    }
}
