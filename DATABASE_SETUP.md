# Database Setup Guide

## ローカルPostgreSQLの起動

### 前提条件
- Docker Desktop がインストールされていること

### PostgreSQLの起動

```bash
# PostgreSQLコンテナを起動
docker-compose up -d

# ログを確認
docker-compose logs -f postgres

# 起動確認
docker-compose ps
```

### PostgreSQLへの接続確認

```bash
# PostgreSQLコンテナに接続
docker exec -it flowpay-postgres psql -U postgres -d flowpay

# 接続確認用のクエリ
\l  # データベース一覧
\dt # テーブル一覧（起動後はまだ空）
\q  # 終了
```

### PostgreSQLの停止

```bash
# コンテナを停止（データは保持される）
docker-compose stop

# コンテナを停止して削除（データは保持される）
docker-compose down

# コンテナとデータを完全に削除
docker-compose down -v
```

## 接続情報

### ローカル開発環境
- **Host**: localhost
- **Port**: 5432
- **Database**: flowpay
- **User**: postgres
- **Password**: postgres

### JDBC URL
```
jdbc:postgresql://localhost:5432/flowpay
```

## ストレージタイプの切り替え

### CSV使用（デフォルト）
```bash
./gradlew bootRun
# または
./gradlew bootRun --args='--spring.profiles.active=csv'
```

### PostgreSQL使用
```bash
./gradlew bootRun --args='--spring.profiles.active=jpa'
```

## トラブルシューティング

### ポート5432が既に使用されている場合
```bash
# 使用中のプロセスを確認
lsof -i :5432

# docker-compose.ymlのポートを変更
# 例: "5433:5432"
```

### データのリセット
```bash
# すべてのデータを削除して再起動
docker-compose down -v
docker-compose up -d
```

### PostgreSQLのログ確認
```bash
docker-compose logs postgres
```
