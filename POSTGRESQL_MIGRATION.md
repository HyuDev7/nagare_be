# PostgreSQL Migration Guide

このガイドでは、FlowPayアプリケーションをCSVストレージからPostgreSQLデータベースに移行する手順を説明します。

## 実装概要

### アーキテクチャ

クリーンアーキテクチャを採用しているため、ストレージ層の切り替えは簡単に行えます。

```
Domain Layer (変更なし)
    ↓
Application Layer (変更なし)
    ↓
Infrastructure Layer (CSV実装 ⇄ JPA実装)
```

### 実装内容

以下のコンポーネントが追加されました：

#### 1. JPA Entity Classes
`src/main/kotlin/com/flowpay/infrastructure/persistence/jpa/entity/`

- `TransactionEntity` - 取引エンティティ
- `AssetAccountEntity` - 資産アカウントエンティティ
- `PaymentMethodEntity` - 支払い手段エンティティ
- `CategoryEntity` - カテゴリエンティティ
- `RecurringTransactionEntity` - 定期取引エンティティ

各エンティティには以下のメソッドが含まれています：
- `fromDomain()` - ドメインモデルからエンティティへの変換
- `toDomain()` - エンティティからドメインモデルへの変換

#### 2. Spring Data JPA Repository Interfaces
`src/main/kotlin/com/flowpay/infrastructure/persistence/jpa/`

- `TransactionJpaRepository`
- `AssetAccountJpaRepository`
- `PaymentMethodJpaRepository`
- `CategoryJpaRepository`
- `RecurringTransactionJpaRepository`

#### 3. JPA Repository Implementations
`src/main/kotlin/com/flowpay/infrastructure/persistence/jpa/`

- `JpaTransactionRepositoryImpl`
- `JpaAssetAccountRepositoryImpl`
- `JpaPaymentMethodRepositoryImpl`
- `JpaCategoryRepositoryImpl`
- `JpaRecurringTransactionRepositoryImpl`

#### 4. Configuration Class
`src/main/kotlin/com/flowpay/presentation/config/RepositoryConfig.kt`

Spring Profileに基づいてCSV実装とJPA実装を切り替える設定クラス。

#### 5. Application Profiles
- `application-csv.yml` - CSV使用時の設定
- `application-jpa.yml` - PostgreSQL使用時の設定

## セットアップ手順

### 1. PostgreSQLの起動

Docker Desktopが起動していることを確認してから：

```bash
# PostgreSQLコンテナを起動
docker-compose up -d

# ログを確認
docker-compose logs -f postgres

# 起動確認
docker-compose ps
```

### 2. データベース接続確認

```bash
# PostgreSQLコンテナに接続
docker exec -it flowpay-postgres psql -U postgres -d flowpay

# 接続確認
\l  # データベース一覧
\dt # テーブル一覧（最初は空）
\q  # 終了
```

## アプリケーションの起動

### CSV使用（デフォルト）

```bash
./gradlew bootRun
```

または明示的に指定：

```bash
./gradlew bootRun --args='--spring.profiles.active=csv'
```

### PostgreSQL使用

```bash
# PostgreSQLが起動していることを確認
docker-compose ps

# JPA profileで起動
./gradlew bootRun --args='--spring.profiles.active=jpa'
```

初回起動時、Hibernateが自動的にテーブルを作成します（`ddl-auto: update`設定による）。

## 動作確認

### 1. アプリケーションログを確認

起動時に以下のログが表示されるはずです：

```
Hibernate: create table asset_accounts ...
Hibernate: create table categories ...
Hibernate: create table payment_methods ...
Hibernate: create table recurring_transactions ...
Hibernate: create table transactions ...
```

### 2. データベースを確認

```bash
docker exec -it flowpay-postgres psql -U postgres -d flowpay

# テーブル一覧を確認
\dt

# テーブルの構造を確認
\d transactions
\d asset_accounts
\d payment_methods
\d categories
\d recurring_transactions

\q
```

### 3. APIテスト

フロントエンドアプリケーションまたはcurlでAPIをテストします：

```bash
# 資産アカウント一覧取得
curl http://localhost:8080/api/asset-accounts

# カテゴリ一覧取得
curl http://localhost:8080/api/categories

# 支払い手段一覧取得
curl http://localhost:8080/api/payment-methods
```

## データ移行（CSVからPostgreSQL）

現在CSVにデータがある場合、以下の手順で移行できます：

### 手動移行

1. CSV profileでアプリケーションを起動し、既存データをエクスポート
2. PostgreSQLプロファイルで起動
3. APIを使用してデータを再インポート

### 今後の実装予定

専用の移行ツールを作成予定：

```kotlin
@Component
class DataMigrationTool(
    @Qualifier("csvTransactionRepository") private val csvRepo: TransactionRepository,
    @Qualifier("jpaTransactionRepository") private val jpaRepo: TransactionRepository
) {
    fun migrateTransactions() {
        val transactions = csvRepo.findAll()
        transactions.forEach { jpaRepo.save(it) }
    }
}
```

## トラブルシューティング

### PostgreSQLに接続できない

```bash
# Dockerが起動しているか確認
docker ps

# PostgreSQLコンテナの状態確認
docker-compose ps
docker-compose logs postgres

# PostgreSQLを再起動
docker-compose restart postgres
```

### ポート5432が既に使用されている

```bash
# 使用中のプロセスを確認
lsof -i :5432

# docker-compose.ymlのポートを変更
# 例: "5433:5432"
```

その後、`application-jpa.yml`のJDBC URLも更新：

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5433/flowpay
```

### テーブルが作成されない

`application-jpa.yml`の`ddl-auto`設定を確認：

```yaml
spring:
  jpa:
    hibernate:
      ddl-auto: update  # create, update, validate, noneから選択
```

- `update`: テーブルが存在しない場合は作成、存在する場合は更新
- `create`: 毎回テーブルを削除して再作成（開発用）
- `validate`: スキーマのみ検証、変更なし（本番推奨）
- `none`: 何もしない

### データをリセットしたい

```bash
# コンテナとデータを完全削除
docker-compose down -v

# 再起動
docker-compose up -d
```

## プロファイル設定の詳細

### CSV Profile (`application-csv.yml`)

```yaml
spring:
  profiles: csv

app:
  storage:
    type: csv

csv:
  data:
    path: src/main/resources/data
```

### JPA Profile (`application-jpa.yml`)

```yaml
spring:
  profiles: jpa
  datasource:
    url: jdbc:postgresql://localhost:5432/flowpay
    username: postgres
    password: postgres
    driver-class-name: org.postgresql.Driver
  jpa:
    hibernate:
      ddl-auto: update
    database-platform: org.hibernate.dialect.PostgreSQLDialect
    show-sql: true  # SQLログを表示

app:
  storage:
    type: jpa
```

## 本番環境への移行

本番環境では以下の設定変更を推奨：

1. **環境変数でパスワード管理**:
```yaml
spring:
  datasource:
    password: ${DB_PASSWORD}  # 環境変数から取得
```

2. **DDL自動実行を無効化**:
```yaml
spring:
  jpa:
    hibernate:
      ddl-auto: validate  # または none
```

3. **SQLログを無効化**:
```yaml
spring:
  jpa:
    show-sql: false
```

4. **Flyway/Liquibase導入検討**:
データベースマイグレーションツールの使用を推奨

## まとめ

- ✅ PostgreSQL + JPA実装が完了
- ✅ CSV実装と並行運用可能
- ✅ プロファイル切り替えで簡単に変更可能
- ✅ クリーンアーキテクチャによりドメイン層は変更なし
- 🔜 データ移行ツールの実装（今後）
- 🔜 TransferRepository、SettingsRepositoryのJPA対応（今後）

## 次のステップ

Phase 3で以下の機能を追加予定：

1. データ移行ツールの実装
2. 残りのリポジトリ（Transfer、Settings）のJPA対応
3. Supabaseへの移行
4. 認証機能の統合
5. リアルタイム同期機能
