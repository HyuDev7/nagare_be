# PaymentFlow Backend API - cURL コマンド集

このドキュメントには、PaymentFlow BackendのすべてのAPIエンドポイントのcURLコマンドが含まれています。

## 基本情報

- ベースURL: `http://localhost:8080`
- Content-Type: `application/json`

---

## 1. ダッシュボード API

### 1.1 ダッシュボードデータ取得

```bash
curl -X GET http://localhost:8080/api/dashboard \
  -H "Content-Type: application/json"
```

---

## 2. 資産アカウント API

### 2.1 資産アカウント情報取得

```bash
curl -X GET http://localhost:8080/api/asset-account \
  -H "Content-Type: application/json"
```

### 2.2 資産アカウント情報更新（残高調整）

```bash
curl -X PUT http://localhost:8080/api/asset-account \
  -H "Content-Type: application/json" \
  -d '{
    "balance": 100000.00
  }'
```

---

## 3. 設定 API

### 3.1 設定情報取得

```bash
curl -X GET http://localhost:8080/api/settings \
  -H "Content-Type: application/json"
```

### 3.2 設定情報更新

```bash
curl -X PUT http://localhost:8080/api/settings \
  -H "Content-Type: application/json" \
  -d '{
    "monthlyBudget": 200000.00
  }'
```

---

## 4. カテゴリ API

### 4.1 カテゴリ一覧取得

```bash
curl -X GET http://localhost:8080/api/categories \
  -H "Content-Type: application/json"
```

### 4.2 カテゴリ取得（ID指定）

```bash
curl -X GET http://localhost:8080/api/categories/{id} \
  -H "Content-Type: application/json"
```

例:
```bash
curl -X GET http://localhost:8080/api/categories/cat001 \
  -H "Content-Type: application/json"
```

### 4.3 カテゴリ登録

```bash
curl -X POST http://localhost:8080/api/categories \
  -H "Content-Type: application/json" \
  -d '{
    "name": "食費",
    "type": "EXPENSE"
  }'
```

**CategoryType の値:**
- `EXPENSE`: 支出
- `INCOME`: 収入

### 4.4 カテゴリ更新

```bash
curl -X PUT http://localhost:8080/api/categories/{id} \
  -H "Content-Type: application/json" \
  -d '{
    "name": "食費（更新）",
    "type": "EXPENSE"
  }'
```

例:
```bash
curl -X PUT http://localhost:8080/api/categories/cat001 \
  -H "Content-Type: application/json" \
  -d '{
    "name": "食費（更新）",
    "type": "EXPENSE"
  }'
```

### 4.5 カテゴリ削除

```bash
curl -X DELETE http://localhost:8080/api/categories/{id} \
  -H "Content-Type: application/json"
```

例:
```bash
curl -X DELETE http://localhost:8080/api/categories/cat001 \
  -H "Content-Type: application/json"
```

---

## 5. 支払い手段 API

### 5.1 支払い手段一覧取得

```bash
curl -X GET http://localhost:8080/api/payment-methods \
  -H "Content-Type: application/json"
```

### 5.2 支払い手段取得（ID指定）

```bash
curl -X GET http://localhost:8080/api/payment-methods/{id} \
  -H "Content-Type: application/json"
```

例:
```bash
curl -X GET http://localhost:8080/api/payment-methods/pm001 \
  -H "Content-Type: application/json"
```

### 5.3 支払い手段登録

```bash
curl -X POST http://localhost:8080/api/payment-methods \
  -H "Content-Type: application/json" \
  -d '{
    "name": "楽天カード",
    "type": "CREDIT_CARD",
    "assetAccountId": "asset001",
    "closingDay": 31,
    "withdrawalDay": 27,
    "memo": "メインカード"
  }'
```

**PaymentMethodType の値:**
- `CREDIT_CARD`: クレジットカード（締め日・引き落とし日あり）
- `DEBIT_CARD`: デビットカード（即時引き落とし）
- `E_MONEY`: 電子マネー/ICカード
- `CASH`: 現金
- `BANK_TRANSFER`: 銀行振込

**オプションフィールド:**
- `closingDay`: 締め日（1-31）
- `withdrawalDay`: 引き落とし日（1-31）
- `memo`: メモ

### 5.4 支払い手段更新

```bash
curl -X PUT http://localhost:8080/api/payment-methods/{id} \
  -H "Content-Type: application/json" \
  -d '{
    "name": "楽天カード（更新）",
    "type": "CREDIT_CARD",
    "assetAccountId": "asset001",
    "closingDay": 31,
    "withdrawalDay": 27,
    "memo": "メインカード（更新）"
  }'
```

例:
```bash
curl -X PUT http://localhost:8080/api/payment-methods/pm001 \
  -H "Content-Type: application/json" \
  -d '{
    "name": "楽天カード（更新）",
    "type": "CREDIT_CARD",
    "assetAccountId": "asset001",
    "closingDay": 31,
    "withdrawalDay": 27,
    "memo": "メインカード（更新）"
  }'
```

### 5.5 支払い手段削除

```bash
curl -X DELETE http://localhost:8080/api/payment-methods/{id} \
  -H "Content-Type: application/json"
```

例:
```bash
curl -X DELETE http://localhost:8080/api/payment-methods/pm001 \
  -H "Content-Type: application/json"
```

---

## 6. 取引 API

### 6.1 取引一覧取得

```bash
curl -X GET http://localhost:8080/api/transactions \
  -H "Content-Type: application/json"
```

### 6.2 取引一覧取得（フィルタ付き）

```bash
# 日付範囲でフィルタ
curl -X GET "http://localhost:8080/api/transactions?from=2025-01-01&to=2025-01-31" \
  -H "Content-Type: application/json"

# カテゴリでフィルタ
curl -X GET "http://localhost:8080/api/transactions?categoryId=cat001" \
  -H "Content-Type: application/json"

# 支払い手段でフィルタ
curl -X GET "http://localhost:8080/api/transactions?paymentMethodId=pm001" \
  -H "Content-Type: application/json"

# 取引タイプでフィルタ
curl -X GET "http://localhost:8080/api/transactions?type=EXPENSE" \
  -H "Content-Type: application/json"

# 複数の条件を組み合わせ
curl -X GET "http://localhost:8080/api/transactions?from=2025-01-01&to=2025-01-31&type=EXPENSE&categoryId=cat001" \
  -H "Content-Type: application/json"
```

**クエリパラメータ:**
- `from`: 開始日（YYYY-MM-DD形式）
- `to`: 終了日（YYYY-MM-DD形式）
- `categoryId`: カテゴリID
- `paymentMethodId`: 支払い手段ID
- `type`: 取引タイプ（`EXPENSE` または `INCOME`）

### 6.3 取引取得（ID指定）

```bash
curl -X GET http://localhost:8080/api/transactions/{id} \
  -H "Content-Type: application/json"
```

例:
```bash
curl -X GET http://localhost:8080/api/transactions/tx001 \
  -H "Content-Type: application/json"
```

### 6.4 取引登録

```bash
curl -X POST http://localhost:8080/api/transactions \
  -H "Content-Type: application/json" \
  -d '{
    "date": "2025-01-15",
    "amount": 1500.00,
    "type": "EXPENSE",
    "paymentMethodId": "pm001",
    "categoryId": "cat001",
    "memo": "ランチ代"
  }'
```

**TransactionType の値:**
- `EXPENSE`: 支出
- `INCOME`: 収入

**オプションフィールド:**
- `memo`: メモ

### 6.5 取引更新

```bash
curl -X PUT http://localhost:8080/api/transactions/{id} \
  -H "Content-Type: application/json" \
  -d '{
    "date": "2025-01-15",
    "amount": 1800.00,
    "type": "EXPENSE",
    "paymentMethodId": "pm001",
    "categoryId": "cat001",
    "memo": "ランチ代（更新）"
  }'
```

例:
```bash
curl -X PUT http://localhost:8080/api/transactions/tx001 \
  -H "Content-Type: application/json" \
  -d '{
    "date": "2025-01-15",
    "amount": 1800.00,
    "type": "EXPENSE",
    "paymentMethodId": "pm001",
    "categoryId": "cat001",
    "memo": "ランチ代（更新）"
  }'
```

### 6.6 取引削除

```bash
curl -X DELETE http://localhost:8080/api/transactions/{id} \
  -H "Content-Type: application/json"
```

例:
```bash
curl -X DELETE http://localhost:8080/api/transactions/tx001 \
  -H "Content-Type: application/json"
```

---

## Postmanへのインポート方法

1. Postmanを起動
2. 左上の「Import」ボタンをクリック
3. 「Raw text」タブを選択
4. 上記のcURLコマンドをコピー＆ペースト
5. 「Import」ボタンをクリック

または、各cURLコマンドをターミナルで直接実行することもできます。

---

## テストの流れ例

1. **初期設定**
   ```bash
   # 資産アカウント情報取得
   curl -X GET http://localhost:8080/api/asset-account -H "Content-Type: application/json"

   # 設定情報取得
   curl -X GET http://localhost:8080/api/settings -H "Content-Type: application/json"
   ```

2. **カテゴリ作成**
   ```bash
   curl -X POST http://localhost:8080/api/categories \
     -H "Content-Type: application/json" \
     -d '{"name": "食費", "type": "EXPENSE"}'
   ```

3. **支払い手段作成**
   ```bash
   curl -X POST http://localhost:8080/api/payment-methods \
     -H "Content-Type: application/json" \
     -d '{
       "name": "楽天カード",
       "type": "CREDIT_CARD",
       "assetAccountId": "asset001",
       "closingDay": 31,
       "withdrawalDay": 27
     }'
   ```

4. **取引登録**
   ```bash
   curl -X POST http://localhost:8080/api/transactions \
     -H "Content-Type: application/json" \
     -d '{
       "date": "2025-01-15",
       "amount": 1500.00,
       "type": "EXPENSE",
       "paymentMethodId": "{作成した支払い手段のID}",
       "categoryId": "{作成したカテゴリのID}",
       "memo": "ランチ代"
     }'
   ```

5. **ダッシュボード確認**
   ```bash
   curl -X GET http://localhost:8080/api/dashboard -H "Content-Type: application/json"
   ```

---

## 注意事項

- `{id}` の部分は実際のリソースIDに置き換えてください
- リクエストボディのJSON形式が正しいか確認してください
- 日付は `YYYY-MM-DD` 形式で指定してください
- 金額は数値型で指定してください（文字列ではありません）
- サーバーが起動していることを確認してください（`./gradlew bootRun`）
