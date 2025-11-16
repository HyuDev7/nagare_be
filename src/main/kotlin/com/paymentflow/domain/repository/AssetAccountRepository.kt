package com.paymentflow.domain.repository

import com.paymentflow.domain.model.AssetAccount

/**
 * 資産アカウントリポジトリインターフェース
 */
interface AssetAccountRepository {
    /**
     * 資産アカウントを取得
     * Phase 1互換: 最初のアカウントを返す
     */
    fun find(): AssetAccount?

    /**
     * すべての資産アカウントを取得
     */
    fun findAll(): List<AssetAccount>

    /**
     * IDで資産アカウントを取得
     */
    fun findById(id: String): AssetAccount?

    /**
     * 資産アカウントを保存
     */
    fun save(assetAccount: AssetAccount): AssetAccount

    /**
     * 資産アカウントを削除
     */
    fun delete(id: String): Boolean
}
