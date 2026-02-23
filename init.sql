-- PaymentFlow Database Initialization Script

-- データベースが既に存在する場合は何もしない
-- Docker Composeの環境変数で自動作成されるため、ここでは追加設定のみ

-- タイムゾーンの設定
SET timezone = 'Asia/Tokyo';

-- 拡張機能の有効化（必要に応じて）
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- ログ出力
SELECT 'PaymentFlow database initialized successfully' AS message;
