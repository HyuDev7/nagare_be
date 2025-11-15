package com.paymentflow.domain.repository

import com.paymentflow.domain.model.AssetAccount

/**
 * 資産アカウントリポジトリインターフェース
 * Phase 1では単一の資産アカウントのみ扱う
 */
interface AssetAccountRepository {
    /**
     * 資産アカウントを取得
     * Phase 1では単一のアカウントを返す
     */
    fun find(): AssetAccount?

    /**
     * 資産アカウントを保存
     */
    fun save(assetAccount: AssetAccount): AssetAccount
}
