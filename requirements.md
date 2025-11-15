# 家計簿アプリ要件定義書

## 1. プロジェクト概要

### 1.1 プロジェクト名
**PaymentFlow** (仮称)

### 1.2 コンセプト
クレジットカードやICカードなど、**支払い発生日と実際の引き落とし日が異なる支払い手段の管理に特化**した家計簿アプリ。
ユーザーは「現在持っている金額」と「これから引かれる予定の金額」を俯瞰でき、予算管理と支出コントロールを実現する。

### 1.3 ターゲットユーザー
- 個人利用（Phase 1）
- クレジットカードを複数利用している人
- 引き落とし日のズレによる資金管理に課題を感じている人

---

## 2. 技術スタック

### 2.1 アーキテクチャ
- **形態**: Webアプリケーション
- **将来展望**: ネイティブアプリ（iOS/Android）
- **設計原則**: **クリーンアーキテクチャ**を採用
  - ドメイン層、アプリケーション層、インフラストラクチャ層、プレゼンテーション層の明確な分離
  - ビジネスロジックの独立性を保ち、フレームワークやデータベースへの依存を最小化
  - 将来的なCSVからRDBへの移行を容易にする設計

### 2.2 バックエンド
- **言語**: Kotlin (Server-side)
- **フレームワーク**: Spring Boot
  - Spring Web (REST API)
  - Spring Dependency Injection
- **データ永続化**: CSV形式（Phase 1）
  - 将来的にRDB（PostgreSQL/MySQL）へ移行予定
  - リポジトリパターンによる抽象化で移行を容易に

### 2.3 フロントエンド
- **言語**: TypeScript
- **フレームワーク**: React
  - React Router (ルーティング)
  - React Hooks (状態管理)
- **スタイリング**: Tailwind CSS

### 2.4 開発環境
- **ビルドツール**: Gradle (Backend), npm/yarn (Frontend)
- **バージョン管理**: Git

---

## 3. 機能要件

### 3.1 Phase 1 機能（MVP）

#### 優先度: C - 支払い手段の登録・管理
- 支払い手段の種類:
  - クレジットカード（締め日・引き落とし日あり）
  - デビットカード（即時引き落とし）
  - 電子マネー/ICカード
  - 現金
  - 銀行振込
- 登録情報:
  - 支払い手段名（例: 楽天カード、三井住友VISAカード）
  - 種類
  - 引き落とし先資産アカウント
  - 締め日（クレジットカードの場合）
  - 引き落とし日（クレジットカードの場合）
  - メモ

#### 優先度: A - 取引の登録・一覧表示
- 取引の種類:
  - 支出
  - 収入
- 登録情報:
  - 利用日（必須）
  - 金額（必須）
  - 支払い手段（必須）
  - カテゴリ（必須）
  - メモ（任意）
- 一覧表示機能:
  - 日付順ソート（降順/昇順）
  - フィルタリング（期間、カテゴリ、支払い手段）
  - 編集・削除機能

#### 優先度: B - 資産アカウント管理
- Phase 1では「全体の残高」として単一の資産アカウントを持つ
- 登録情報:
  - 現在の残高
  - 名称（例: メイン資産）
- 自動計算:
  - 収入取引 → 資産増加
  - 即時引き落とし支出 → 資産減少
  - クレカ引き落とし日 → 資産減少

#### 優先度: D - ダッシュボード
表示項目（上から優先度順）:
1. **現在の総資産**
   - 資産アカウントの残高
2. **今月の支出合計**
   - 当月の支出取引の合計
3. **今月の予算残額**
   - 設定予算 - 今月の支出合計
4. **未確定の支出**
   - クレジットカードで使用済みだが、まだ引き落とされていない金額
   - 「実質的に使える金額 = 総資産 - 未確定の支出」も表示
5. **今後の引き落とし予定**
   - 今後30日間の引き落とし予定（カード別）
6. **カテゴリ別の支出内訳**
   - 当月のカテゴリ別円グラフまたは棒グラフ

#### 優先度: E - 予算設定と警告表示
- 月次予算の設定
- 予算到達率の表示（例: 80%到達でアラート色変更）
- 予算超過時の警告表示
- Phase 1では画面内での視覚的な警告のみ（通知機能は後回し）

#### 優先度: F - CSVエクスポート機能
- 取引履歴のCSVエクスポート
- 期間指定可能

---

### 3.2 Phase 2以降の機能（将来展望）

- 通知機能（ブラウザ通知/メール）
- カレンダービュー
- グラフ・統計（月次推移、カテゴリ別トレンド）
- 複数資産アカウント対応
- 支払い手段間の移動記録（銀行出金 → 現金など）
- クレジットカード会社の明細CSVインポート
- 複数ユーザー対応
- ネイティブアプリ（iOS/Android）
- 繰り返し取引の自動登録（サブスク等）

---

## 4. データモデル

### 4.1 エンティティ概要

```
資産アカウント (AssetAccount)
  ↑ 引き落とし元
支払い手段 (PaymentMethod)
  ↑ 使用
取引 (Transaction)
```

### 4.2 資産アカウント (AssetAccount)

Phase 1では単一の資産アカウントのみ。

| フィールド名 | 型 | 必須 | 説明 |
|---|---|---|---|
| id | String | ○ | 一意識別子 |
| name | String | ○ | 名称（例: メイン資産） |
| balance | Decimal | ○ | 現在の残高 |
| createdAt | DateTime | ○ | 作成日時 |
| updatedAt | DateTime | ○ | 更新日時 |

### 4.3 支払い手段 (PaymentMethod)

| フィールド名 | 型 | 必須 | 説明 |
|---|---|---|---|
| id | String | ○ | 一意識別子 |
| name | String | ○ | 名称（例: 楽天カード） |
| type | Enum | ○ | CREDIT_CARD, DEBIT_CARD, E_MONEY, CASH, BANK_TRANSFER |
| assetAccountId | String | ○ | 引き落とし先資産アカウントID |
| closingDay | Int | △ | 締め日（1-31、クレカのみ） |
| withdrawalDay | Int | △ | 引き落とし日（1-31、クレカのみ） |
| memo | String |  | メモ |
| createdAt | DateTime | ○ | 作成日時 |
| updatedAt | DateTime | ○ | 更新日時 |

**type の定義:**
- `CREDIT_CARD`: 締め日・引き落とし日あり
- `DEBIT_CARD`: 即時引き落とし
- `E_MONEY`: 電子マネー/ICカード
- `CASH`: 現金
- `BANK_TRANSFER`: 銀行振込

### 4.4 カテゴリ (Category)

| フィールド名 | 型 | 必須 | 説明 |
|---|---|---|---|
| id | String | ○ | 一意識別子 |
| name | String | ○ | カテゴリ名 |
| type | Enum | ○ | EXPENSE（支出）, INCOME（収入） |
| createdAt | DateTime | ○ | 作成日時 |

**デフォルトカテゴリ（例）:**

支出:
- 食費
- 交通費
- 日用品
- 娯楽
- 医療費
- 通信費
- 光熱費
- その他

収入:
- 給与
- 副業
- その他

### 4.5 取引 (Transaction)

| フィールド名 | 型 | 必須 | 説明 |
|---|---|---|---|
| id | String | ○ | 一意識別子 |
| date | Date | ○ | 利用日 |
| amount | Decimal | ○ | 金額（正の数） |
| type | Enum | ○ | EXPENSE（支出）, INCOME（収入） |
| paymentMethodId | String | ○ | 支払い手段ID |
| categoryId | String | ○ | カテゴリID |
| memo | String |  | メモ |
| withdrawalDate | Date | △ | 実際の引き落とし日（システムが計算） |
| isWithdrawn | Boolean | ○ | 引き落とし済みフラグ |
| createdAt | DateTime | ○ | 作成日時 |
| updatedAt | DateTime | ○ | 更新日時 |

**withdrawalDate の計算ロジック:**
- クレジットカード: 利用日から締め日・引き落とし日を基に計算
  - 例: 締め日が月末、引き落とし日が翌27日の場合
    - 3/15利用 → 3/31締め → 4/27引き落とし
- その他の支払い手段: 利用日 = 引き落とし日

---

## 5. CSV ファイル構造

Phase 1では以下のCSVファイルでデータを永続化する。

### 5.1 asset_accounts.csv

```csv
id,name,balance,createdAt,updatedAt
acc_001,メイン資産,500000.00,2025-01-01T00:00:00Z,2025-01-15T10:30:00Z
```

### 5.2 payment_methods.csv

```csv
id,name,type,assetAccountId,closingDay,withdrawalDay,memo,createdAt,updatedAt
pm_001,楽天カード,CREDIT_CARD,acc_001,31,27,メインカード,2025-01-01T00:00:00Z,2025-01-01T00:00:00Z
pm_002,デビットカード,DEBIT_CARD,acc_001,,,給油用,2025-01-01T00:00:00Z,2025-01-01T00:00:00Z
pm_003,現金,CASH,acc_001,,,,2025-01-01T00:00:00Z,2025-01-01T00:00:00Z
```

### 5.3 categories.csv

```csv
id,name,type,createdAt
cat_001,食費,EXPENSE,2025-01-01T00:00:00Z
cat_002,交通費,EXPENSE,2025-01-01T00:00:00Z
cat_003,給与,INCOME,2025-01-01T00:00:00Z
```

### 5.4 transactions.csv

```csv
id,date,amount,type,paymentMethodId,categoryId,memo,withdrawalDate,isWithdrawn,createdAt,updatedAt
tx_001,2025-03-15,5000.00,EXPENSE,pm_001,cat_001,スーパーで買い物,2025-04-27,false,2025-03-15T14:30:00Z,2025-03-15T14:30:00Z
tx_002,2025-03-20,300000.00,INCOME,pm_002,cat_003,3月給与,2025-03-20,true,2025-03-20T09:00:00Z,2025-03-20T09:00:00Z
```

### 5.5 settings.csv

予算設定等のアプリ設定を保存。

```csv
key,value,updatedAt
monthly_budget,100000.00,2025-01-01T00:00:00Z
```

---

## 6. 画面設計

### 6.1 画面一覧

Phase 1では以下の画面を実装する。

1. ダッシュボード（トップページ）
2. 取引一覧・登録画面
3. 支払い手段管理画面
4. 設定画面（予算設定）

### 6.2 画面詳細

#### 6.2.1 ダッシュボード

**URL**: `/`

**表示内容:**
- ヘッダー
  - アプリ名
  - ナビゲーションメニュー（ダッシュボード / 取引 / 支払い手段 / 設定）
- メインコンテンツ
  - カード形式で以下を表示:
    1. 現在の総資産
    2. 今月の支出合計
    3. 今月の予算残額（プログレスバー付き）
    4. 未確定の支出
    5. 実質的に使える金額（総資産 - 未確定の支出）
  - 今後の引き落とし予定（テーブル形式）
    - 日付、支払い手段名、金額
  - カテゴリ別支出内訳（円グラフ）

#### 6.2.2 取引一覧・登録画面

**URL**: `/transactions`

**表示内容:**
- 新規取引登録フォーム
  - 日付（デフォルト: 今日）
  - 種類（支出/収入）
  - 金額
  - 支払い手段（プルダウン）
  - カテゴリ（プルダウン、種類に応じて絞り込み）
  - メモ
  - 登録ボタン
- 取引一覧テーブル
  - 列: 日付、種類、金額、支払い手段、カテゴリ、引き落とし日、ステータス（未確定/確定）、操作
  - フィルタ機能:
    - 期間指定
    - カテゴリ選択
    - 支払い手段選択
  - ソート機能（日付昇順/降順）
  - 操作: 編集、削除

#### 6.2.3 支払い手段管理画面

**URL**: `/payment-methods`

**表示内容:**
- 新規支払い手段登録フォーム
  - 名称
  - 種類（プルダウン）
  - 締め日（クレカの場合のみ表示）
  - 引き落とし日（クレカの場合のみ表示）
  - メモ
  - 登録ボタン
- 支払い手段一覧テーブル
  - 列: 名称、種類、締め日、引き落とし日、操作
  - 操作: 編集、削除

#### 6.2.4 設定画面

**URL**: `/settings`

**表示内容:**
- 予算設定
  - 月次予算額（入力フィールド）
  - 保存ボタン
- データ管理
  - CSVエクスポートボタン
    - 取引履歴エクスポート（期間指定）
- 資産アカウント情報
  - 現在の残高表示
  - 残高調整機能（手動で残高を修正）

---

## 7. ビジネスロジック

### 7.1 引き落とし日の計算

**クレジットカードの場合:**

```
利用日 → 締め日 → 引き落とし日

例: 締め日が月末(31)、引き落とし日が翌27日の場合
- 3/15利用 → 3/31締め → 4/27引き落とし
- 4/1利用 → 4/30締め → 5/27引き落とし
```

**実装上の注意:**
- 月末の場合、各月の日数を考慮（2月は28/29日）
- 引き落とし日が土日祝日の場合の考慮は Phase 2 に回す（Phase 1では設定された日付通り）

### 7.2 未確定の支出の計算

```
未確定の支出 = SUM(isWithdrawn = false かつ type = EXPENSE の取引の金額)
```

### 7.3 資産残高の更新

**収入取引が登録された場合:**
```
資産残高 += 取引金額
```

**支出取引が登録された場合（即時引き落とし）:**
```
if (支払い手段.type != CREDIT_CARD) {
  資産残高 -= 取引金額
  取引.isWithdrawn = true
  取引.withdrawalDate = 取引.date
}
```

**引き落とし日が到来した場合（バッチ処理またはダッシュボード表示時に判定）:**
```
該当する取引を抽出 (withdrawalDate <= 今日 かつ isWithdrawn = false)
for each 取引 {
  資産残高 -= 取引.金額
  取引.isWithdrawn = true
}
```

### 7.4 予算管理

**予算残額:**
```
予算残額 = 月次予算 - 今月の支出合計
```

**予算到達率:**
```
到達率 = (今月の支出合計 / 月次予算) × 100
```

**警告表示:**
- 80%以上: 黄色で警告
- 100%以上: 赤色で警告

---

## 8. 非機能要件

### 8.1 パフォーマンス
- ダッシュボードの初期表示: 2秒以内
- 取引登録: 1秒以内

### 8.2 セキュリティ
- Phase 1では認証機能なし（ローカル環境想定）
- Phase 2以降で認証・認可を実装

### 8.3 ユーザビリティ
- レスポンシブデザイン（スマートフォン・タブレット対応）
- 直感的なUI/UX
- 入力エラー時の分かりやすいエラーメッセージ

### 8.4 保守性
- コードの可読性を重視
- コメントの適切な記載
- テストコードの記載（Phase 2以降で充実）

---

## 9. 開発フェーズ

### Phase 1（MVP）
**目標**: 基本機能の実装（2-3ヶ月）

- [ ] プロジェクトセットアップ
- [ ] データモデル実装
- [ ] CSV読み書き処理実装
- [ ] バックエンドAPI実装
  - [ ] 支払い手段CRUD
  - [ ] 取引CRUD
  - [ ] ダッシュボードデータ取得API
  - [ ] 予算設定API
- [ ] フロントエンド実装
  - [ ] ダッシュボード画面
  - [ ] 取引一覧・登録画面
  - [ ] 支払い手段管理画面
  - [ ] 設定画面
- [ ] 動作確認・バグ修正

### Phase 2
**目標**: 機能拡張・UX改善

- [ ] グラフ・統計機能
- [ ] カレンダービュー
- [ ] 通知機能
- [ ] クレジットカード明細CSVインポート
- [ ] テストコード充実

### Phase 3
**目標**: データベース移行・スケール対応

- [ ] RDB（PostgreSQL/MySQL）への移行
- [ ] 認証・認可機能実装
- [ ] 複数ユーザー対応
- [ ] パフォーマンス最適化

### Phase 4
**目標**: ネイティブアプリ展開

- [ ] iOS/Androidアプリ開発
- [ ] アプリ内通知
- [ ] オフライン対応

---

## 10. API設計（Phase 1）

### 10.1 エンドポイント一覧

#### 資産アカウント
- `GET /api/asset-account` - 資産アカウント情報取得
- `PUT /api/asset-account` - 資産アカウント情報更新（残高調整）

#### 支払い手段
- `GET /api/payment-methods` - 支払い手段一覧取得
- `POST /api/payment-methods` - 支払い手段登録
- `PUT /api/payment-methods/:id` - 支払い手段更新
- `DELETE /api/payment-methods/:id` - 支払い手段削除

#### カテゴリ
- `GET /api/categories` - カテゴリ一覧取得
- `POST /api/categories` - カテゴリ登録
- `PUT /api/categories/:id` - カテゴリ更新
- `DELETE /api/categories/:id` - カテゴリ削除

#### 取引
- `GET /api/transactions` - 取引一覧取得（クエリパラメータでフィルタ）
- `POST /api/transactions` - 取引登録
- `PUT /api/transactions/:id` - 取引更新
- `DELETE /api/transactions/:id` - 取引削除
- `GET /api/transactions/export` - 取引履歴CSVエクスポート

#### ダッシュボード
- `GET /api/dashboard` - ダッシュボードデータ取得

#### 設定
- `GET /api/settings` - 設定情報取得
- `PUT /api/settings` - 設定情報更新

### 10.2 レスポンス例

#### GET /api/dashboard

```json
{
  "assetAccount": {
    "id": "acc_001",
    "name": "メイン資産",
    "balance": 500000.00
  },
  "currentMonth": {
    "totalExpense": 85000.00,
    "totalIncome": 300000.00,
    "budget": 100000.00,
    "budgetRemaining": 15000.00,
    "budgetUsageRate": 85.0
  },
  "pendingWithdrawals": {
    "total": 45000.00,
    "availableBalance": 455000.00
  },
  "upcomingWithdrawals": [
    {
      "date": "2025-04-27",
      "paymentMethodName": "楽天カード",
      "amount": 25000.00
    },
    {
      "date": "2025-05-10",
      "paymentMethodName": "三井住友カード",
      "amount": 20000.00
    }
  ],
  "categoryBreakdown": [
    {
      "categoryName": "食費",
      "amount": 35000.00,
      "percentage": 41.2
    },
    {
      "categoryName": "交通費",
      "amount": 15000.00,
      "percentage": 17.6
    },
    {
      "categoryName": "娯楽",
      "amount": 35000.00,
      "percentage": 41.2
    }
  ]
}
```

#### GET /api/transactions

**クエリパラメータ:**
- `from`: 期間開始日（YYYY-MM-DD）
- `to`: 期間終了日（YYYY-MM-DD）
- `categoryId`: カテゴリID
- `paymentMethodId`: 支払い手段ID
- `type`: 取引種類（EXPENSE / INCOME）
- `sort`: ソート（date_asc / date_desc）

**レスポンス:**
```json
{
  "transactions": [
    {
      "id": "tx_001",
      "date": "2025-03-15",
      "amount": 5000.00,
      "type": "EXPENSE",
      "paymentMethod": {
        "id": "pm_001",
        "name": "楽天カード"
      },
      "category": {
        "id": "cat_001",
        "name": "食費"
      },
      "memo": "スーパーで買い物",
      "withdrawalDate": "2025-04-27",
      "isWithdrawn": false,
      "createdAt": "2025-03-15T14:30:00Z"
    }
  ],
  "total": 1,
  "page": 1,
  "pageSize": 50
}
```

---

## 11. ディレクトリ構成（推奨）

```
paymentflow/
├── backend/                 # Kotlin + Spring Bootバックエンド
│   ├── src/
│   │   ├── main/
│   │   │   ├── kotlin/
│   │   │   │   └── com/paymentflow/
│   │   │   │       ├── PaymentFlowApplication.kt  # Spring Bootエントリーポイント
│   │   │   │       │
│   │   │   │       ├── domain/                    # ドメイン層（ビジネスロジック）
│   │   │   │       │   ├── model/                 # エンティティ
│   │   │   │       │   │   ├── AssetAccount.kt
│   │   │   │       │   │   ├── PaymentMethod.kt
│   │   │   │       │   │   ├── Category.kt
│   │   │   │       │   │   └── Transaction.kt
│   │   │   │       │   ├── repository/            # リポジトリインターフェース（Port）
│   │   │   │       │   │   ├── AssetAccountRepository.kt
│   │   │   │       │   │   ├── PaymentMethodRepository.kt
│   │   │   │       │   │   ├── CategoryRepository.kt
│   │   │   │       │   │   └── TransactionRepository.kt
│   │   │   │       │   └── service/               # ドメインサービス
│   │   │   │       │       └── WithdrawalCalculator.kt
│   │   │   │       │
│   │   │   │       ├── application/               # アプリケーション層（ユースケース）
│   │   │   │       │   ├── usecase/
│   │   │   │       │   │   ├── AssetAccountUseCase.kt
│   │   │   │       │   │   ├── PaymentMethodUseCase.kt
│   │   │   │       │   │   ├── TransactionUseCase.kt
│   │   │   │       │   │   ├── DashboardUseCase.kt
│   │   │   │       │   │   └── SettingsUseCase.kt
│   │   │   │       │   └── dto/                   # アプリケーション層DTO
│   │   │   │       │       ├── DashboardData.kt
│   │   │   │       │       └── TransactionFilter.kt
│   │   │   │       │
│   │   │   │       ├── infrastructure/            # インフラストラクチャ層（技術詳細）
│   │   │   │       │   └── persistence/
│   │   │   │       │       └── csv/               # CSV実装（Adapter）
│   │   │   │       │           ├── CsvAssetAccountRepository.kt
│   │   │   │       │           ├── CsvPaymentMethodRepository.kt
│   │   │   │       │           ├── CsvCategoryRepository.kt
│   │   │   │       │           ├── CsvTransactionRepository.kt
│   │   │   │       │           └── CsvHelper.kt   # CSV読み書きユーティリティ
│   │   │   │       │
│   │   │   │       └── presentation/              # プレゼンテーション層（API）
│   │   │   │           ├── controller/            # RESTコントローラー
│   │   │   │           │   ├── AssetAccountController.kt
│   │   │   │           │   ├── PaymentMethodController.kt
│   │   │   │           │   ├── TransactionController.kt
│   │   │   │           │   ├── DashboardController.kt
│   │   │   │           │   └── SettingsController.kt
│   │   │   │           ├── dto/                   # リクエスト・レスポンスDTO
│   │   │   │           │   ├── request/
│   │   │   │           │   │   ├── CreateTransactionRequest.kt
│   │   │   │           │   │   ├── CreatePaymentMethodRequest.kt
│   │   │   │           │   │   └── UpdateSettingsRequest.kt
│   │   │   │           │   └── response/
│   │   │   │           │       ├── DashboardResponse.kt
│   │   │   │           │       ├── TransactionResponse.kt
│   │   │   │           │       └── PaymentMethodResponse.kt
│   │   │   │           └── config/                # Spring設定
│   │   │   │               ├── WebConfig.kt       # CORS設定等
│   │   │   │               └── DependencyConfig.kt # DI設定
│   │   │   │
│   │   │   └── resources/
│   │   │       ├── application.yml               # Spring Boot設定
│   │   │       └── data/                         # CSVファイル保存ディレクトリ
│   │   │           ├── asset_accounts.csv
│   │   │           ├── payment_methods.csv
│   │   │           ├── categories.csv
│   │   │           ├── transactions.csv
│   │   │           └── settings.csv
│   │   └── test/
│   │       └── kotlin/
│   │           └── com/paymentflow/
│   │               ├── domain/                    # ドメイン層のテスト
│   │               ├── application/               # ユースケースのテスト
│   │               └── presentation/              # コントローラーのテスト
│   ├── build.gradle.kts
│   └── settings.gradle.kts
│
├── frontend/                # TypeScript + Reactフロントエンド
│   ├── src/
│   │   ├── components/          # 共通コンポーネント
│   │   │   ├── layout/
│   │   │   │   ├── Header.tsx
│   │   │   │   ├── Navigation.tsx
│   │   │   │   └── Layout.tsx
│   │   │   ├── ui/              # 再利用可能なUIコンポーネント
│   │   │   │   ├── Button.tsx
│   │   │   │   ├── Card.tsx
│   │   │   │   ├── Input.tsx
│   │   │   │   ├── Select.tsx
│   │   │   │   └── Modal.tsx
│   │   │   └── features/        # 機能別コンポーネント
│   │   │       ├── dashboard/
│   │   │       │   ├── AssetCard.tsx
│   │   │       │   ├── BudgetCard.tsx
│   │   │       │   ├── PendingWithdrawalsCard.tsx
│   │   │       │   └── CategoryChart.tsx
│   │   │       ├── transactions/
│   │   │       │   ├── TransactionForm.tsx
│   │   │       │   ├── TransactionList.tsx
│   │   │       │   └── TransactionFilter.tsx
│   │   │       └── paymentMethods/
│   │   │           ├── PaymentMethodForm.tsx
│   │   │           └── PaymentMethodList.tsx
│   │   │
│   │   ├── pages/               # ページコンポーネント
│   │   │   ├── Dashboard.tsx
│   │   │   ├── Transactions.tsx
│   │   │   ├── PaymentMethods.tsx
│   │   │   └── Settings.tsx
│   │   │
│   │   ├── hooks/               # カスタムフック
│   │   │   ├── useTransactions.ts
│   │   │   ├── usePaymentMethods.ts
│   │   │   └── useDashboard.ts
│   │   │
│   │   ├── api/                 # APIクライアント
│   │   │   ├── client.ts        # Axios設定
│   │   │   ├── transactionApi.ts
│   │   │   ├── paymentMethodApi.ts
│   │   │   ├── dashboardApi.ts
│   │   │   └── settingsApi.ts
│   │   │
│   │   ├── types/               # 型定義
│   │   │   ├── models.ts        # エンティティ型
│   │   │   ├── api.ts           # API型
│   │   │   └── enums.ts         # Enum定義
│   │   │
│   │   ├── utils/               # ユーティリティ
│   │   │   ├── dateFormatter.ts
│   │   │   ├── currencyFormatter.ts
│   │   │   └── validation.ts
│   │   │
│   │   ├── App.tsx
│   │   ├── main.tsx
│   │   └── index.css            # Tailwind CSS import
│   │
│   ├── public/
│   ├── package.json
│   ├── tsconfig.json
│   ├── tailwind.config.js
│   ├── postcss.config.js
│   └── vite.config.ts
│
├── docs/                    # ドキュメント
│   └── requirements.md      # 本ドキュメント
│
└── README.md
```

### クリーンアーキテクチャの層構造

```
┌─────────────────────────────────────────────┐
│     Presentation Layer (presentation/)      │ ← Controllers, DTOs
├─────────────────────────────────────────────┤
│     Application Layer (application/)        │ ← Use Cases
├─────────────────────────────────────────────┤
│        Domain Layer (domain/)               │ ← Entities, Repository Interfaces
├─────────────────────────────────────────────┤
│   Infrastructure Layer (infrastructure/)    │ ← CSV Repository Implementation
└─────────────────────────────────────────────┘

依存の方向: 外側 → 内側
- Presentation → Application → Domain
- Infrastructure → Domain (Repository interface の実装)
- Domain層は他の層に依存しない（最も重要）
```

---

## 12. 開発時の注意事項

### 12.1 クリーンアーキテクチャの実装方針

**依存関係のルール:**
- Domain層は他のどの層にも依存しない（最も重要）
- Application層はDomain層にのみ依存
- Presentation層とInfrastructure層はDomain層とApplication層に依存
- 外側の層から内側の層への依存のみ許可（逆は禁止）

**各層の責務:**
- **Domain層**: ビジネスルール、エンティティ、リポジトリインターフェース
  - フレームワークに依存しない純粋なKotlinコード
  - 例: `Transaction`, `PaymentMethod`, `TransactionRepository`（interface）
  
- **Application層**: ユースケース、アプリケーション固有のビジネスロジック
  - ドメイン層のエンティティとリポジトリを使用
  - 例: `TransactionUseCase.createTransaction()`, `DashboardUseCase.getDashboardData()`
  
- **Infrastructure層**: 技術的詳細の実装
  - リポジトリの具体的な実装（CSV操作）
  - 外部サービスとの連携（将来的にメール送信等）
  - 例: `CsvTransactionRepository`（`TransactionRepository`の実装）
  
- **Presentation層**: ユーザーインターフェース（REST API）
  - HTTPリクエスト/レスポンスの処理
  - DTOへの変換
  - 例: `TransactionController`, リクエスト/レスポンスDTO

**依存性注入（DI）:**
- Spring Bootの`@Component`, `@Service`, `@Repository`アノテーションを活用
- インターフェースに対してプログラミングし、具体的な実装はDIコンテナが注入
- 例:
  ```kotlin
  @Service
  class TransactionUseCase(
      private val transactionRepository: TransactionRepository  // インターフェース
  ) {
      // ...
  }
  ```

### 12.2 CSV操作について
- CSV読み書き時は必ずロックを取得（同時書き込み防止）
- バックアップ機能を検討（Phase 2）
- エンコーディング: UTF-8 BOM付き推奨（Excel対応）
- CSVの実装詳細はInfrastructure層に閉じ込める
  - Domain層のリポジトリインターフェースのみを外部に公開
  - 将来的なRDB移行時、Infrastructure層のみ変更すればよい設計に

### 12.3 日付・時刻の扱い
- タイムゾーン: 日本標準時（JST）を基準
- ISO 8601形式で保存（例: 2025-03-15T14:30:00+09:00）
- フロントエンドでの表示は日本語形式（2025年3月15日 14:30）
- Kotlinでは`java.time.LocalDate`, `java.time.LocalDateTime`を使用

### 12.4 金額の扱い
- 浮動小数点ではなくDecimal型を使用（精度保証）
- Kotlinでは`java.math.BigDecimal`を使用
- 小数点以下2桁まで対応

### 12.5 エラーハンドリング
- 適切なHTTPステータスコードを返す
  - 200: 成功
  - 201: 作成成功
  - 400: バリデーションエラー
  - 404: リソースが見つからない
  - 500: サーバーエラー
- Spring Bootの`@ControllerAdvice`でグローバルエラーハンドリング
- フロントエンドでユーザーフレンドリーなエラーメッセージを表示

### 12.6 テスタビリティ
- クリーンアーキテクチャにより、各層を独立してテスト可能
- Domain層とApplication層はフレームワークに依存しないため、単純なユニットテストが可能
- Infrastructure層のテストではモックCSVやインメモリデータを使用
- Presentation層のテストではSpring Boot Testを活用

---

## 13. テスト方針

### Phase 1
- 手動テスト中心
- 主要な画面遷移・機能の動作確認

### Phase 2以降
- ユニットテスト（バックエンド）
- インテグレーションテスト（API）
- E2Eテスト（フロントエンド）
- カバレッジ目標: 70%以上

---

## 14. リリース計画

### Phase 1（MVP）
- ローカル環境での動作確認
- 個人利用開始

### Phase 2
- クローズドβ版（友人・家族に共有）
- フィードバック収集・改善

### Phase 3
- オープンβ版
- 一般公開準備

### Phase 4
- 正式リリース
- App Store / Google Play 公開

---

## 15. 参考資料・リンク

- [Kotlin公式ドキュメント](https://kotlinlang.org/docs/home.html)
- [Ktor公式ドキュメント](https://ktor.io/docs/)
- [TypeScript公式ドキュメント](https://www.typescriptlang.org/docs/)
- [React公式ドキュメント](https://react.dev/)
- [CSV RFC 4180](https://www.ietf.org/rfc/rfc4180.txt)

---

## 16. 変更履歴

| 日付 | バージョン | 変更内容 | 担当者 |
|---|---|---|---|
| 2025-11-03 | 1.0 | 初版作成 | - |

---

**このドキュメントは、開発の進行に合わせて随時更新されます。**
